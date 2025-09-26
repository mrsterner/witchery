package dev.sterner.witchery.data_attachment.possession.movement

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParser
import com.google.gson.JsonSerializer
import com.google.gson.JsonSyntaxException
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.DataSyncS2CPayload

object MovementAltererManager : PreparableReloadListener, MovementRegistry {

    private val LOGGER = LoggerFactory.getLogger(MovementAltererManager::class.java)
    private val LOCATION = Witchery.id("entity_mobility.json")

    val GSON: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(object : TypeToken<EntityType<*>>() {}.type, EntityTypeAdapter())
        .registerTypeAdapter(TriState::class.java, TriStateTypeAdapter())
        .registerTypeAdapter(MovementMode::class.java, MovementModeTypeAdapter())
        .registerTypeAdapter(SwimMode::class.java, SwimModeTypeAdapter())
        .registerTypeAdapter(WalkMode::class.java, WalkModeTypeAdapter())
        .create()


    private val entityMovementConfigs = mutableMapOf<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>()

    override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        preparationsProfiler: ProfilerFiller,
        reloadProfiler: ProfilerFiller,
        backgroundExecutor: Executor,
        gameExecutor: Executor
    ): CompletableFuture<Void> {
        return load(resourceManager, preparationsProfiler, backgroundExecutor)
            .thenCompose(preparationBarrier::wait)
            .thenCompose { configs ->
                gameExecutor.let {
                    CompletableFuture.runAsync({
                        apply(configs, resourceManager, reloadProfiler)
                    }, it)
                }
            }
    }

    private fun load(
        manager: ResourceManager,
        profiler: ProfilerFiller,
        executor: Executor
    ): CompletableFuture<Map<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>> {
        return CompletableFuture.supplyAsync({
            val ret = mutableMapOf<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>()

            try {
                manager.getResourceStack(LOCATION).forEach { resource ->
                    try {
                        InputStreamReader(resource.open()).use { reader ->
                            val jsonObject = JsonParser.parseReader(reader).asJsonObject

                            for ((key, value) in jsonObject.entrySet()) {
                                val entityId = ResourceLocation.tryParse(key)
                                if (entityId != null) {
                                    val entityType = BuiltInRegistries.ENTITY_TYPE.get(entityId)
                                    if (entityType != EntityType.PIG) { // PIG is the default for missing types
                                        val config = GSON.fromJson(value, MovementAltererAttachment.SerializableMovementConfig::class.java)
                                        ret[entityType] = config
                                    } else if (getRequired(value.asJsonObject)) {
                                        throw JsonSyntaxException("Not a valid entity type: $key")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        LOGGER.warn("Could not read movement config from JSON file ${resource.sourcePackId()}", e)
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Could not read movement configs", e)
            }

            ret
        }, executor)
    }

    private fun getRequired(jsonObject: JsonObject): Boolean {
        return if (jsonObject.has("required")) {
            jsonObject.get("required").asBoolean
        } else {
            true
        }
    }

    private fun apply(
        configs: Map<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        entityMovementConfigs.clear()
        entityMovementConfigs.putAll(configs)

        // Sync to all connected clients
        syncToClients()

        LOGGER.info("Loaded ${configs.size} entity movement configurations")
    }

    fun syncToClients() {
        PacketDistributor.sendToAllPlayers(DataSyncS2CPayload(entityMovementConfigs))
    }

    fun syncToClient(player: ServerPlayer) {
        PacketDistributor.sendToPlayer(player, DataSyncS2CPayload(entityMovementConfigs))
    }

    override fun getEntityMovementConfig(type: EntityType<*>): MovementAltererAttachment.SerializableMovementConfig? {
        return entityMovementConfigs[type]
    }

    // Type Adapters
    class EntityTypeAdapter : JsonDeserializer<EntityType<*>>, JsonSerializer<EntityType<*>> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EntityType<*>? {
            val id = ResourceLocation.tryParse(json.asString) ?: return null
            return BuiltInRegistries.ENTITY_TYPE.get(id)
        }

        override fun serialize(src: EntityType<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(BuiltInRegistries.ENTITY_TYPE.getKey(src).toString())
        }
    }

    class TriStateTypeAdapter : JsonDeserializer<TriState>, JsonSerializer<TriState> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TriState {
            return when (json.asString.lowercase()) {
                "true" -> TriState.TRUE
                "false" -> TriState.FALSE
                else -> TriState.DEFAULT
            }
        }

        override fun serialize(src: TriState, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.serializedName)
        }
    }

    class MovementModeTypeAdapter : JsonDeserializer<MovementMode>, JsonSerializer<MovementMode> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MovementMode {
            return try {
                MovementMode.valueOf(json.asString.uppercase())
            } catch (e: Exception) {
                MovementMode.UNSPECIFIED
            }
        }

        override fun serialize(src: MovementMode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.serializedName)
        }
    }

    class SwimModeTypeAdapter : JsonDeserializer<SwimMode>, JsonSerializer<SwimMode> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SwimMode {
            return try {
                SwimMode.valueOf(json.asString.uppercase())
            } catch (e: Exception) {
                SwimMode.UNSPECIFIED
            }
        }

        override fun serialize(src: SwimMode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.serializedName)
        }
    }

    class WalkModeTypeAdapter : JsonDeserializer<WalkMode>, JsonSerializer<WalkMode> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): WalkMode {
            return try {
                WalkMode.valueOf(json.asString.uppercase())
            } catch (e: Exception) {
                WalkMode.UNSPECIFIED
            }
        }

        override fun serialize(src: WalkMode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.serializedName)
        }
    }
}