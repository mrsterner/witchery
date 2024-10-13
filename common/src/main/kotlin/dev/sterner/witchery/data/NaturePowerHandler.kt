package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
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
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.logging.Logger

object NaturePowerHandler {
    private val LOGGER = LogUtils.getLogger()
    private val LOADER = NaturePowerLoader()

    private val NATURE_POWER_VALUES = mutableMapOf<ResourceLocation, Pair<Int, Int>>()

    private val CODEC = RecordCodecBuilder.create { instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("block").forGetter(Triple<ResourceLocation, Int, Int>::first),
        Codec.INT.fieldOf("power").forGetter(Triple<ResourceLocation, Int, Int>::second),
        Codec.INT.fieldOf("limit").forGetter(Triple<ResourceLocation, Int, Int>::third)
    ).apply(instance, ::Triple) }

    /**
     * This method gets the base power provided by the given Block.
     */
    fun getPower(block: Block): Int? = NATURE_POWER_VALUES[BuiltInRegistries.BLOCK.getKey(block)]?.first

    /**
     * This method gets the limit for the Block.
     * Please check against this value to determine if its base power should be added!!!!
     */
    fun getLimit(block: Block): Int? = NATURE_POWER_VALUES[BuiltInRegistries.BLOCK.getKey(block)]?.second

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
            `object`.forEach { (file, element) ->
                // Handle JSON here
                try {
                    element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { json ->
                        val (loc, power, limit) = CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(::IllegalArgumentException).first

                        if (BuiltInRegistries.BLOCK.getOptional(loc).isPresent) {
                            if (NATURE_POWER_VALUES.containsKey(loc))
                                LOGGER.info("Overriding $loc from power ${NATURE_POWER_VALUES[loc]?.first} and limit ${NATURE_POWER_VALUES[loc]?.second} with a power of $power and a limit of $limit")
                            else
                                LOGGER.info("Registering $loc with a base power of $power and a limit of $limit")
                            NATURE_POWER_VALUES[loc] = Pair(power, limit)
                        } else
                            LOGGER.error("Invalid Block $loc from file $file!!!! Skipping it...")
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.fillInStackTrace())
                }
            }
        }
    }
}