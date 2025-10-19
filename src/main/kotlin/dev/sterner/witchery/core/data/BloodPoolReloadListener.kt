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
            val entityJson = json.get("entityType")?.asString
            val bloodJson = json.get("bloodDrops")?.asString
            if (entityJson != null) {
                if (ResourceLocation.tryParse(entityJson) == null || ResourceLocation.tryParse(bloodJson) == null) {
                    return
                }
                val data = BloodData.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(::IllegalArgumentException).first

                val entity = data.entity

                BLOOD_PAIR[entity] = data
            }
        }
    }

    data class BloodData(val entity: EntityType<*>, val bloodDrops: Int, val qualityBloodDrops: Int) {

        companion object {
            val CODEC: Codec<BloodData> =
                RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BloodData> ->
                    instance.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(BloodData::entity),
                        Codec.INT.fieldOf("bloodDrops").forGetter(BloodData::bloodDrops),
                        Codec.INT.fieldOf("qualityBloodDrops").forGetter(BloodData::qualityBloodDrops),
                    ).apply(
                        instance
                    ) { entity, bloodDrops, qualityBloodDrops ->
                        BloodData(entity, bloodDrops, qualityBloodDrops)
                    }
                }
        }
    }
}