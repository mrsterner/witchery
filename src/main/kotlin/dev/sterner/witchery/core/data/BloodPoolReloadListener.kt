package dev.sterner.witchery.core.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.EntityType

object BloodPoolReloadListener {

    val LOADER = BloodPoolResourceReloadListener(Gson(), "blood_pool")
    val BLOOD_PAIR = mutableMapOf<EntityType<*>, BloodData>()

    class BloodPoolResourceReloadListener(gson: Gson, directory: String) :
        SimpleJsonResourceReloadListener(gson, directory) {

        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            `object`.forEach { (file, element) ->
                if (element.isJsonArray)
                    element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                else if (element.isJsonObject)
                    parseJson(element.asJsonObject, file)
            }

        }

        private fun parseJson(json: JsonObject, file: ResourceLocation) {
            val data = BloodData.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(::IllegalArgumentException).first

            val entity = data.resolveEntity()
            if (entity != null) {
                BLOOD_PAIR[entity] = data
            }
        }
    }

    data class BloodData(val entityId: ResourceLocation, val bloodDrops: Int, val qualityBloodDrops: Int) {

        companion object {
            val CODEC: Codec<BloodData> =
                RecordCodecBuilder.create { inst ->
                    inst.group(
                        ResourceLocation.CODEC.fieldOf("entityType").forGetter(BloodData::entityId),
                        Codec.INT.fieldOf("bloodDrops").forGetter(BloodData::bloodDrops),
                        Codec.INT.fieldOf("qualityBloodDrops").forGetter(BloodData::qualityBloodDrops)
                    ).apply(inst) { entityId, bloodDrops, qualityBloodDrops ->
                        BloodData(entityId, bloodDrops, qualityBloodDrops)
                    }
                }
        }

        fun resolveEntity(): EntityType<*>? =
            BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElse(null)
    }

}