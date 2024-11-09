package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object ErosionHandler{

    val LOADER = ErosionResourceReloadListener(Gson(), "erosion")
    val EROSION_PAIR = mutableMapOf<Block, Block>()

    fun registerListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, object : PreparableReloadListener {
            override fun getName() = "erosion"

            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                preparationsProfiler: ProfilerFiller,
                reloadProfiler: ProfilerFiller,
                backgroundExecutor: Executor,
                gameExecutor: Executor
            ): CompletableFuture<Void> {
                return LOADER.reload(
                    preparationBarrier,
                    resourceManager,
                    preparationsProfiler,
                    reloadProfiler,
                    backgroundExecutor,
                    gameExecutor
                )
            }
        })
    }

    class ErosionResourceReloadListener(gson: Gson, directory: String) : SimpleJsonResourceReloadListener(gson, directory) {

        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            `object`.forEach { (file, element) ->
                try {
                    if (element.isJsonArray)
                        element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                    else if (element.isJsonObject)
                        parseJson(element.asJsonObject, file)
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.fillInStackTrace())
                }
            }

        }

        private fun parseJson(json: JsonObject, file: ResourceLocation) {
            val blockJson = json.get("fromBlock")?.asString
            val targetJson = json.get("toBlock")?.asString
            if (blockJson != null) {
                if (ResourceLocation.tryParse(blockJson) == null || ResourceLocation.tryParse(targetJson) == null) {
                    return
                }
                val data = ErosionData.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(::IllegalArgumentException).first

                val fromBlock = BuiltInRegistries.BLOCK.getOptional(data.fromBlock)
                val toBlock = BuiltInRegistries.BLOCK.getOptional(data.toBlock)

                if (fromBlock.isPresent && toBlock.isPresent) {
                    EROSION_PAIR[fromBlock.get()] = toBlock.get()
                }
            }
        }
    }

    data class ErosionData(val fromBlock: ResourceLocation, val toBlock: ResourceLocation) {

        companion object {
            val CODEC: Codec<ErosionData> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ErosionData> ->
                    instance.group(
                        ResourceLocation.CODEC.fieldOf("fromBlock").forGetter(ErosionData::fromBlock),
                        ResourceLocation.CODEC.fieldOf("toBlock").forGetter(ErosionData::toBlock),
                    ).apply(
                        instance
                    ) { fromBlock, toBlock ->
                        ErosionData(fromBlock, toBlock)
                    }
                }
        }
    }
}