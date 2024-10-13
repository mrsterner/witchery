package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.entity.SmokerBlockEntity
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor

object NaturePowerHandler {
    private val LOGGER = LogUtils.getLogger()
    private val LOADER = NaturePowerLoader()
    private val NATURE_POWER_VALUES = mutableMapOf<ResourceLocation, Pair<Int, Int>>()

    private val BLOCK_CODEC = RecordCodecBuilder.create { instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("block").forGetter(Triple<ResourceLocation, Int, Int>::first),
        Codec.INT.fieldOf("power").forGetter(Triple<ResourceLocation, Int, Int>::second),
        Codec.INT.fieldOf("limit").forGetter(Triple<ResourceLocation, Int, Int>::third)
    ).apply(instance, ::Triple) }

    private val TAG_CODEC = RecordCodecBuilder.create { instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(Triple<ResourceLocation, Int, Int>::first),
        Codec.INT.fieldOf("power").forGetter(Triple<ResourceLocation, Int, Int>::second),
        Codec.INT.fieldOf("limit").forGetter(Triple<ResourceLocation, Int, Int>::third)
    ).apply(instance, ::Triple) }

    /**
     * Do not touch. This is simply used to handle tags AFTER they are loaded!
     */
    val tagQueue = ConcurrentLinkedQueue<Triple<ResourceLocation, Int, Int>>()

    /**
     * This method gets the base power provided by the given Block.
     */
    fun getPower(block: Block): Int? = NATURE_POWER_VALUES[BuiltInRegistries.BLOCK.getKey(block)]?.first

    /**
     * This method gets the limit for the Block.
     * Please check against this value to determine if its base power should be added!!!!
     */
    fun getLimit(block: Block): Int? = NATURE_POWER_VALUES[BuiltInRegistries.BLOCK.getKey(block)]?.second

    private fun addBlock(block: Block, power: Int, limit: Int) {
        val loc = BuiltInRegistries.BLOCK.getKey(block)

        if (NATURE_POWER_VALUES.containsKey(loc))
            LOGGER.info("Overriding $loc from power ${NATURE_POWER_VALUES[loc]?.first} and limit ${NATURE_POWER_VALUES[loc]?.second} with a power of $power and a limit of $limit")
        else
            LOGGER.info("Registering $loc with a base power of $power and a limit of $limit")

        NATURE_POWER_VALUES[loc] = Pair(power, limit)
    }

    private fun addTag(tag: TagKey<Block>, power: Int, limit: Int) {
        val blocks = BuiltInRegistries.BLOCK.getTag(tag)
        if (blocks.isPresent)
            blocks.get().forEach { addBlock(it.value(), power, limit) }
        else
            LOGGER.error("Invalid Tag ${tag.location}!!!! Skipping it...")
    }

    /**
     * Only call after world (read datapacks) are loaded so we can handle tags.
     */
    fun addPendingTags() {
        tagQueue.forEach { (loc, power, limit) ->
            addTag(TagKey.create(Registries.BLOCK, loc), power, limit)
        }
    }

    /**
     * This function is for registering the reload listener.
     * Only call during initialization!!!
     */
    fun registerListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, object: PreparableReloadListener {
            override fun getName() = "nature"
           
            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                preparationsProfiler: ProfilerFiller,
                reloadProfiler: ProfilerFiller,
                backgroundExecutor: Executor,
                gameExecutor: Executor
            ): CompletableFuture<Void> {
                return LOADER.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)
            }
        })
    }

    private class NaturePowerLoader : SimpleJsonResourceReloadListener(Gson(), "nature") {
        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            LifecycleEvent.SERVER_LEVEL_LOAD.register { level ->
                addPendingTags()
            }

            `object`.forEach { (file, element) ->
                try {
                    element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { json ->
                        if (json.has("tag")) {
                            tagQueue.add(TAG_CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(::IllegalArgumentException).first)
                        } else if (json.has("block")) {
                            val (loc, power, limit) = BLOCK_CODEC.decode(JsonOps.INSTANCE, json)
                                .getOrThrow(::IllegalArgumentException).first

                            val block = BuiltInRegistries.BLOCK.getOptional(loc)

                            if (block.isPresent) addBlock(block.get(), power, limit)
                            else LOGGER.error("Invalid Block $loc from file $file!!!! Skipping it...")
                        } else
                            LOGGER.error("JSON missing block or tag in file $file!!!! Skipping it...")
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.fillInStackTrace())
                }
            }
        }
    }
}