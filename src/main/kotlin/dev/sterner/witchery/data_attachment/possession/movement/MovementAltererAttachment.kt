package dev.sterner.witchery.data_attachment.possession.movement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player

object MovementAltererAttachment {

    fun get(player: Player): MovementAlterer {
        return player.getData(WitcheryDataAttachments.MOVEMENT_ALTERER)
    }

    fun set(player: Player, alterer: MovementAlterer) {
        player.setData(WitcheryDataAttachments.MOVEMENT_ALTERER, alterer)
    }

    data class MovementAlterer(
        var configi: SerializableMovementConfig? = null,
        var huggingWall: Boolean = false
    ) {
        fun setConfig(newConfig: SerializableMovementConfig?) {
            configi = newConfig
            applyConfig()
        }

        fun applyConfig() {
        }

        fun alterControls() {
        }

        fun getSwimmingAcceleration(baseAcceleration: Float): Float {
            return configi?.let {
                baseAcceleration * it.waterSpeedModifier
            } ?: baseAcceleration
        }

        fun getSwimmingUpwardsVelocity(baseUpwardsVelocity: Double): Double {
            return configi?.let {
                when (it.swimMode) {
                    SwimMode.SINKING -> 0.0
                    else -> baseUpwardsVelocity
                }
            } ?: baseUpwardsVelocity
        }

        fun canClimbWalls(): Boolean = configi?.climbsWalls ?: false

        fun isNoClipping(): Boolean = configi?.phasesThroughWalls ?: false

        fun updateSwimming() {
        }

        fun disablesSwimming(): Boolean {
            return configi?.swimMode == SwimMode.DISABLED
        }

        fun hugWall(hugging: Boolean) {
            huggingWall = hugging
        }

        companion object {
            val CODEC: Codec<MovementAlterer> = RecordCodecBuilder.create { instance ->
                instance.group(
                    SerializableMovementConfig.CODEC.optionalFieldOf("config", null).forGetter { it.configi },
                    Codec.BOOL.fieldOf("huggingWall").forGetter { it.huggingWall }
                ).apply(instance, ::MovementAlterer)
            }
        }
    }

    data class SerializableMovementConfig(
        val flightMode: MovementMode = MovementMode.UNSPECIFIED,
        val swimMode: SwimMode = SwimMode.UNSPECIFIED,
        val walkMode: WalkMode = WalkMode.UNSPECIFIED,
        val sinksInWater: TriState = TriState.DEFAULT,
        val flopsOnLand: TriState = TriState.DEFAULT,
        val climbsWalls: Boolean = false,
        val phasesThroughWalls: Boolean = false,
        val gravity: Float = 0f,
        val fallSpeedModifier: Float = 1f,
        val landedSpeedModifier: Float = 1f,
        val waterSpeedModifier: Float = 1f,
        val inertia: Float = 0f
    ) {
        companion object {
            val SOUL = SerializableMovementConfig(
                flightMode = MovementMode.ENABLED,
                swimMode = SwimMode.ENABLED,
                walkMode = WalkMode.NORMAL,
                sinksInWater = TriState.FALSE,
                flopsOnLand = TriState.FALSE,
                climbsWalls = false,
                phasesThroughWalls = true,
                gravity = 0f,
                fallSpeedModifier = 1f,
                waterSpeedModifier = 1f,
                landedSpeedModifier = 1f,
                inertia = 0.1f
            )

            val CODEC: Codec<SerializableMovementConfig> = RecordCodecBuilder.create { instance ->
                instance.group(
                    MovementMode.CODEC.optionalFieldOf("flight_mode", MovementMode.UNSPECIFIED)
                        .forGetter { it.flightMode },
                    SwimMode.CODEC.optionalFieldOf("swim_mode", SwimMode.UNSPECIFIED).forGetter { it.swimMode },
                    WalkMode.CODEC.optionalFieldOf("walk_mode", WalkMode.UNSPECIFIED).forGetter { it.walkMode },
                    TriState.CODEC.optionalFieldOf("sinks_in_water", TriState.DEFAULT).forGetter { it.sinksInWater },
                    TriState.CODEC.optionalFieldOf("flops_on_land", TriState.DEFAULT).forGetter { it.flopsOnLand },
                    Codec.BOOL.optionalFieldOf("climbs_walls", false).forGetter { it.climbsWalls },
                    Codec.BOOL.optionalFieldOf("phases_through_walls", false).forGetter { it.phasesThroughWalls },
                    Codec.FLOAT.optionalFieldOf("gravity", 0f).forGetter { it.gravity },
                    Codec.FLOAT.optionalFieldOf("fall_speed_modifier", 1f).forGetter { it.fallSpeedModifier },
                    Codec.FLOAT.optionalFieldOf("landed_speed_modifier", 1f).forGetter { it.landedSpeedModifier },
                    Codec.FLOAT.optionalFieldOf("water_speed_modifier", 1f).forGetter { it.waterSpeedModifier },
                    Codec.FLOAT.optionalFieldOf("inertia", 0f).forGetter { it.inertia }
                ).apply(instance, ::SerializableMovementConfig)
            }
        }
    }
}