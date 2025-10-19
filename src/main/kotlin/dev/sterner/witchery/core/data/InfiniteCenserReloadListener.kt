package dev.sterner.witchery.core.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.effect.MobEffect

object InfiniteCenserReloadListener {

    val LOADER = InfiniteCenserResourceReloadListener(Gson(), "infinite_censer")
    val INFINITE_POTIONS = mutableSetOf<Holder<MobEffect>>()
    val INFINITE_SPECIAL_POTIONS = mutableSetOf<ResourceLocation>()

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

            val special = WitcherySpecialPotionEffects.SPECIAL_REGISTRY.getHolder(data.potion)
            if (special.isPresent) {
                INFINITE_SPECIAL_POTIONS.add(special.get().value().id)
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