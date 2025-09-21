package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import java.util.concurrent.ConcurrentLinkedQueue

object NaturePowerReloadListener {
    private val LOGGER = LogUtils.getLogger()
    val LOADER = NaturePowerLoader()
    val NATURE_POWER_VALUES = mutableMapOf<Either<Block, TagKey<Block>>, Pair<Int, Int>>()

    private var pendingDataProcessed = false

    /**
     * Do not touch. This is simply used to handle tags AFTER they are loaded!
     */
    val tagQueue = ConcurrentLinkedQueue<Data>()

    /**
     * Do not touch. This is simply used to handle blocks AFTER tags are handled!
     */
    val blockQueue = ConcurrentLinkedQueue<Data>()

    /**
     * This method gets the base power provided by the given Block.
     */
    fun getPower(block: BlockState): Int? {
        var power = NATURE_POWER_VALUES[Either.left(block.block)]?.first
        if (power != null) return power

        val tags = NATURE_POWER_VALUES.filterKeys { it.right().isPresent && block.`is`(it.right().get()) }
            .toSortedMap { first, second ->
                (NATURE_POWER_VALUES[second]?.first ?: 0) - (NATURE_POWER_VALUES[first]?.first ?: 0)
            }.keys
        if (tags.isNotEmpty()) power = NATURE_POWER_VALUES[tags.first()]?.first
        return power
    }

    /**
     * This method gets the limit for the Block.
     * Please check against this value to determine if its base power should be added!!!!
     */
    fun getLimit(block: BlockState): Pair<ResourceLocation, Int>? {
        var limit = NATURE_POWER_VALUES[Either.left(block.block)]?.second
        if (limit != null) return Pair(BuiltInRegistries.BLOCK.getKey(block.block), limit)

        val tags = NATURE_POWER_VALUES.filterKeys { it.right().isPresent && block.`is`(it.right().get()) }
            .toSortedMap { first, second ->
                (NATURE_POWER_VALUES[second]?.first ?: 0) - (NATURE_POWER_VALUES[first]?.first ?: 0)
            }.keys
        if (tags.isNotEmpty()) limit = NATURE_POWER_VALUES[tags.first()]?.second
        return limit?.let { Pair(tags.first().right().get().location, it) }
    }

    private fun addEitherBlockOrTag(either: Either<Block, TagKey<Block>>, power: Int, limit: Int) {
        NATURE_POWER_VALUES[either] = Pair(power, limit)
    }

    /**
     * Only call after world (read datapacks) are loaded so we can handle tags.
     */
    fun addPending() {
        if (pendingDataProcessed) return

        tagQueue.forEach {
            addEitherBlockOrTag(Either.right(TagKey.create(Registries.BLOCK, it.block)), it.power, it.limit)
        }

        blockQueue.forEach {
            addEitherBlockOrTag(Either.left(BuiltInRegistries.BLOCK.get(it.block)), it.power, it.limit)
        }

        pendingDataProcessed = true
        tagQueue.clear()
        blockQueue.clear()
    }

    fun registerReloadListener(event: AddReloadListenerEvent) {
        event.addListener(LOADER)
    }

    fun onServerStarting(event: ServerStartingEvent) {
        addPending()
    }

    class NaturePowerLoader : SimpleJsonResourceReloadListener(Gson(), "nature") {
        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            // Clear and reset on reload
            NATURE_POWER_VALUES.clear()
            tagQueue.clear()
            blockQueue.clear()
            pendingDataProcessed = false

            `object`.forEach { (file, element) ->
                try {
                    if (element.isJsonArray)
                        element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                    else if (element.isJsonObject)
                        parseJson(element.asJsonObject, file)
                    else
                        LOGGER.error("The file $file seems to have neither a JSON object or a JSON array... Skipping...")
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.fillInStackTrace())
                }
            }

            // Try to process immediately if tags are available
            // This happens during /reload commands
            try {
                addPending()
            } catch (e: Exception) {
                // Tags might not be ready yet, will process on server start
                LOGGER.debug("Tags not ready during reload, will process on server start")
            }
        }

        fun parseJson(json: JsonObject, file: ResourceLocation) {
            val tag = json.get("tag")?.asString
            val block = json.get("block")?.asString
            if (tag != null) {
                if (ResourceLocation.tryParse(tag) == null) {
                    LOGGER.error("Invalid ResourceLocation of $tag in $file")
                    return
                }
                tagQueue.add(
                    Data.TAG_CODEC.decode(JsonOps.INSTANCE, json)
                        .getOrThrow(::IllegalArgumentException).first
                )
            } else if (block != null) {
                if (ResourceLocation.tryParse(block) == null) {
                    LOGGER.error("Invalid ResourceLocation of $block in $file")
                    return
                }
                val data = Data.CODEC.decode(JsonOps.INSTANCE, json)
                    .getOrThrow(::IllegalArgumentException).first

                val block = BuiltInRegistries.BLOCK.getOptional(data.block)

                if (block.isPresent)
                    blockQueue.add(data)
                else
                    LOGGER.error("Invalid Block ${data.block} from file $file!!!! Skipping it...")
            } else
                LOGGER.error("JSON missing block or tag in file $file!!!! Skipping it...")
        }
    }

    class Data(val block: ResourceLocation, val power: Int, val limit: Int) {
        companion object {
            val CODEC: Codec<Data> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Data> ->
                    instance.group(
                        ResourceLocation.CODEC.fieldOf("block").forGetter(Data::block),
                        Codec.INT.fieldOf("power").forGetter(Data::power),
                        Codec.INT.fieldOf("limit").forGetter(Data::limit)
                    ).apply(
                        instance
                    ) { name, parentCombinations, mutationChance ->
                        Data(name, parentCombinations, mutationChance)
                    }
                }

            val TAG_CODEC: Codec<Data> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Data> ->
                    instance.group(
                        ResourceLocation.CODEC.fieldOf("tag").forGetter(Data::block),
                        Codec.INT.fieldOf("power").forGetter(Data::power),
                        Codec.INT.fieldOf("limit").forGetter(Data::limit)
                    ).apply(
                        instance
                    ) { name, parentCombinations, mutationChance ->
                        Data(name, parentCombinations, mutationChance)
                    }
                }
        }
    }
}