package dev.sterner.witchery.features.infusion

import com.mojang.serialization.Codec
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.LargeFireball
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

enum class CreatureType(val entityType: EntityType<*>?) : StringRepresentable {
    NONE(null) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    CREEPER(EntityType.CREEPER) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    SKELETON(EntityType.SKELETON) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            val itemStack: ItemStack = Items.BOW.defaultInstance
            val itemStack2: ItemStack = Items.ARROW.defaultInstance
            val abstractArrow: AbstractArrow = ProjectileUtil.getMobArrow(player, itemStack2, 1.0f, itemStack)
            val d: Double = lookVec.x() - player.x
            val e: Double = lookVec.y() - abstractArrow.y + 0.3333
            val f: Double = lookVec.z() - player.z
            val g = sqrt(d * d + f * f)
            abstractArrow.shoot(d, e + g * 0.2f, f, 1.6f, (14 - level.difficulty.id * 4).toFloat())
            player.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (level.getRandom().nextFloat() * 0.4f + 0.8f))
            level.addFreshEntity(abstractArrow)
            return true
        }
    },
    SPIDER(EntityType.SPIDER) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    ZOMBIE(EntityType.ZOMBIE) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 30))
            player.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20))
            return true
        }
    },
    SLIME(EntityType.SLIME) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 30, 3))
            return false
        }
    },
    GHAST(EntityType.GHAST) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {

            val largeFireball = LargeFireball(
                level,
                player, lookVec.normalize(),
                1
            )
            largeFireball.setPos(
                player.x + lookVec.x * 4.0,
                player.y + 0.5,
                largeFireball.z + lookVec.z * 4.0
            )
            level.addFreshEntity(largeFireball)
            return true
        }
    },
    ZOMBIE_PIGMAN(EntityType.ZOMBIFIED_PIGLIN) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    ENDERMAN(EntityType.ENDERMAN) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    CAVE_SPIDER(EntityType.CAVE_SPIDER) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    SILVERFISH(EntityType.SILVERFISH) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3))
            return true
        }
    },
    BLAZE(EntityType.BLAZE) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    MAGMA_CUBE(EntityType.MAGMA_CUBE) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 20, 3))
            return true
        }
    },
    BAT(EntityType.BAT) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 20))
            return false
        }
    },
    PIG(EntityType.PIG) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    SHEEP(EntityType.SHEEP) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    COW(EntityType.COW) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    VILLAGER(EntityType.VILLAGER) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    CHICKEN(EntityType.CHICKEN) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    SQUID(EntityType.SQUID) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    WOLF(EntityType.WOLF) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3))
            return true
        }
    },
    MOOSHROOM(EntityType.MOOSHROOM) {
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            return false
        }
    },
    OCELOT(EntityType.OCELOT) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3))
            return true
        }
    },
    HORSE(EntityType.HORSE) { //PASSIVE DONE
        override fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3))
            return true
        }
    };


    abstract fun usePower(player: Player, level: Level, lookVec: Vec3, hitResult: HitResult): Boolean

    fun getCost(): Int {
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