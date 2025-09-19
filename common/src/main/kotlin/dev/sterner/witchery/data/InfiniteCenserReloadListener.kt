package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.ReloadListenerRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object InfiniteCenserReloadListener {

    val LOADER = InfiniteCenserResourceReloadListener(Gson(), "infinite_censer")
    val INFINITE_POTIONS = mutableSetOf<Holder<MobEffect>>()
    val INFINITE_SPECIAL_POTIONS = mutableSetOf<ResourceLocation>()

    fun registerListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, object : PreparableReloadListener {
            override fun getName() = "infinite_censer"

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

    class InfiniteCenserResourceReloadListener(gson: Gson, directory: String) :
        SimpleJsonResourceReloadListener(gson, directory) {

        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            INFINITE_POTIONS.clear()

            `object`.forEach { (file, element) ->
                try {
                    if (element.isJsonArray) {
                        element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                    } else if (element.isJsonObject) {
                        parseJson(element.asJsonObject, file)
                    }
                } catch (e: Exception) {
                    Witchery.LOGGER.error("Failed to load infinite censer data from $file", e)
                }
            }

            Witchery.LOGGER.info("Loaded ${INFINITE_POTIONS.size} infinite censer potions")
        }

        private fun parseJson(json: JsonObject, file: ResourceLocation) {
            val data = InfiniteCenserData.CODEC.decode(JsonOps.INSTANCE, json)
                .getOrThrow { IllegalArgumentException("Invalid infinite censer data in $file: $it") }.first

            val potionRegistry = BuiltInRegistries.POTION
            val potionHolder = potionRegistry.getHolder(data.potion)

            val special = WitcherySpecialPotionEffects.SPECIALS.getHolder(data.potion)
            if (special != null) {
                INFINITE_SPECIAL_POTIONS.add(special.value().id)
            }

            potionHolder.ifPresent { holder ->
                holder.value().effects.forEach {
                    INFINITE_POTIONS.add(it.effect)
                }
            }

            if (potionHolder.isEmpty) {
                Witchery.LOGGER.warn("Unknown potion in infinite censer config: ${data.potion}")
            }
        }
    }

    data class InfiniteCenserData(val potion: ResourceLocation) {
        companion object {
            val CODEC: Codec<InfiniteCenserData> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<InfiniteCenserData> ->
                    instance.group(
                        ResourceLocation.CODEC.fieldOf("potion").forGetter(InfiniteCenserData::potion)
                    ).apply(instance) { potion ->
                        InfiniteCenserData(potion)
                    }
                }
        }
    }
}