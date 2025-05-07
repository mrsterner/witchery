package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.ReloadListenerRegistry
import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.registry.WitcheryFetishEffects
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object FetishEffectHandler {

    val loader = FetishResourceReloadListener(Gson(), "fetish")
    val dataMap = mutableMapOf<ResourceLocation, Data>()

    fun findMatchingEffect(spirit: Int, banshee: Int, specter: Int, poltergeist: Int): ResourceLocation? {
        return dataMap.entries.firstOrNull { (_, data) ->
            data.bansheeCount == banshee &&
                    data.specterCount == specter &&
                    data.poltergeistCount == poltergeist &&
                    data.spiritCount == spirit
        }?.key
    }

    fun getEffect(location: ResourceLocation): FetishEffect? {
        return dataMap[location]?.effectLocation?.let { WitcheryFetishEffects.FETISH_EFFECTS.get(it) }
    }

    fun registerListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, object : PreparableReloadListener {
            override fun getName() = "fetish"

            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                preparationsProfiler: ProfilerFiller,
                reloadProfiler: ProfilerFiller,
                backgroundExecutor: Executor,
                gameExecutor: Executor
            ): CompletableFuture<Void> {
                return loader.reload(
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

    class FetishResourceReloadListener(gson: Gson, directory: String) :
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

            val spiritCount = json.get("spirit_count")?.asInt ?: 0
            val bansheeCount = json.get("banshee_count")?.asInt ?: 0
            val specterCount = json.get("specter_count")?.asInt ?: 0
            val poltergeistCount = json.get("poltergeist_count")?.asInt ?: 0

            val effectString = json.get("effect")?.asString ?: return
            val effectLocation = ResourceLocation.tryParse(effectString) ?: return

            val data = Data(
                spiritCount = spiritCount,
                bansheeCount = bansheeCount,
                specterCount = specterCount,
                poltergeistCount = poltergeistCount,
                effectLocation = effectLocation
            )

            dataMap[file] = data
        }
    }

    data class Data(
        var spiritCount: Int = 0,
        var bansheeCount: Int = 0,
        var specterCount: Int = 0,
        var poltergeistCount: Int = 0,
        var effectLocation: ResourceLocation
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.optionalFieldOf("spirit_count", 0).forGetter { it.spiritCount },
                    Codec.INT.optionalFieldOf("banshee_count", 0).forGetter { it.bansheeCount },
                    Codec.INT.optionalFieldOf("specter_count", 0).forGetter { it.specterCount },
                    Codec.INT.optionalFieldOf("poltergeist_count", 0).forGetter { it.poltergeistCount },
                    ResourceLocation.CODEC.fieldOf("effect").forGetter { it.effectLocation }
                ).apply(instance, ::Data)
            }
        }
    }
}