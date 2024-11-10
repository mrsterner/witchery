package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.InfusionData.Companion.MAX_CHARGE
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

class InfernalInfusionData(val currentCreature: CreatureType = CreatureType.NONE) {

    companion object {

        fun strikeLightning(lightningBolt: LightningBolt?, level: Level?, vec3: Vec3?, entities: MutableList<Entity>?) {
            if (entities != null) {
                for (entity in entities) {
                    if (entity is Player && InfernalInfusionDataAttachment.getData(entity).currentCreature == CreatureType.CREEPER) {
                        PlayerInfusionDataAttachment.increaseInfusionCharge(entity, MAX_CHARGE)
                    }
                }
            }
        }

        fun tick(player: Player?) {
            val data = player?.let { InfernalInfusionDataAttachment.getData(it) }

            if (data != null) {
                if (data.currentCreature == CreatureType.ZOMBIE_PIGMAN || data.currentCreature == CreatureType.GHAST || data.currentCreature == CreatureType.BLAZE) {
                    player.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 3, 0))
                }

                if (data.currentCreature == CreatureType.SLIME || data.currentCreature == CreatureType.MAGMA_CUBE) {
                    player.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 3, 0))
                }

                if (data.currentCreature == CreatureType.SILVERFISH || data.currentCreature == CreatureType.WOLF || data.currentCreature == CreatureType.OCELOT  || data.currentCreature == CreatureType.HORSE) {
                    if (!player.isInWaterOrRain) {
                        player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 3, 0))
                    }
                }
            }
        }



        val CODEC: Codec<InfernalInfusionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                CreatureType.CODEC.fieldOf("currentCreature").forGetter { it.currentCreature }
            ).apply(instance, ::InfernalInfusionData)
        }
        val ID: ResourceLocation = Witchery.id("infernal_infusion_data")
    }

    enum class CreatureType(val entityType: EntityType<*>?): StringRepresentable {
        NONE(null) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        CREEPER(EntityType.CREEPER) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SKELETON(EntityType.SKELETON) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SPIDER(EntityType.SKELETON) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        ZOMBIE(EntityType.ZOMBIE) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SLIME(EntityType.SLIME) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        GHAST(EntityType.GHAST) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        ZOMBIE_PIGMAN(EntityType.ZOMBIFIED_PIGLIN) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        ENDERMAN(EntityType.ENDERMAN) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        CAVE_SPIDER(EntityType.CAVE_SPIDER) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SILVERFISH(EntityType.SILVERFISH) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        BLAZE(EntityType.BLAZE) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        MAGMA_CUBE(EntityType.MAGMA_CUBE) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        BAT(EntityType.BAT) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        PIG(EntityType.PIG) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SHEEP(EntityType.SHEEP) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        COW(EntityType.COW) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        VILLAGER(EntityType.VILLAGER) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        CHICKEN(EntityType.CHICKEN) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SQUID(EntityType.SQUID) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        WOLF(EntityType.WOLF) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        MOOSHROOM(EntityType.MOOSHROOM) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        OCELOT(EntityType.OCELOT) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        HORSE(EntityType.HORSE) { //PASSIVE DONE
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        OWL(WitcheryEntityTypes.OWL.get()) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        };


        abstract fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean

        fun getCost() : Int {
            return 100
        }

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val CODEC: Codec<CreatureType> = Codec.STRING.xmap(
                { name -> entries.find { it.name.equals(name, ignoreCase = true) } ?: NONE },
                { it.name }
            )
        }
    }
}