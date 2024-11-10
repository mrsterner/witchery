package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d

class InfernalInfusionData(val currentCreature: CreatureType = CreatureType.NONE) {

    companion object {
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
        CREEPER(EntityType.CREEPER) {
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
        SLIME(EntityType.SLIME) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        GHAST(EntityType.GHAST) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        ZOMBIE_PIGMAN(EntityType.ZOMBIFIED_PIGLIN) {
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
        SILVERFISH(EntityType.SILVERFISH) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        BLAZE(EntityType.BLAZE) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        MAGMA_CUBE(EntityType.MAGMA_CUBE) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        BAT(EntityType.BAT) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        PIG(EntityType.PIG) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SHEEP(EntityType.SHEEP) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        COW(EntityType.COW) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        VILLAGER(EntityType.VILLAGER) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        CHICKEN(EntityType.CHICKEN) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        SQUID(EntityType.SQUID) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        WOLF(EntityType.WOLF) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        MOOSHROOM(EntityType.MOOSHROOM) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        OCELOT(EntityType.OCELOT) {
            override fun usePower(level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
                return false
            }
        },
        HORSE(EntityType.HORSE) {
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