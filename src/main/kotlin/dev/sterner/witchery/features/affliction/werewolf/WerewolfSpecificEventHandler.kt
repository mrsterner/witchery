package dev.sterner.witchery.features.affliction.werewolf


import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.affliction.TransformationPlayerAttachment
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.event.TransformationHandler
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.player.Player

object WerewolfSpecificEventHandler {

    fun killEntity(livingEntity: LivingEntity?, damageSource: DamageSource?) {
        if (damageSource?.entity !is ServerPlayer) return

        val player = damageSource.entity as ServerPlayer
        val wereData = AfflictionPlayerAttachment.getData(player)
        val wereLevel = wereData.getLevel(AfflictionTypes.LYCANTHROPY)

        when (livingEntity) {
            is Piglin -> WerewolfLeveling.increaseKilledPiglin(player)
            is Sheep -> WerewolfLeveling.increaseKilledSheep(player)
            is Wolf -> WerewolfLeveling.increaseKilledWolf(player)
            is HornedHuntsmanEntity -> WerewolfLeveling.setHasKilledHuntsman(player)
        }

        if (wereLevel >= 4) {
            if (TransformationHandler.isWolf(player) || TransformationHandler.isWerewolf(player)) {
                player.foodData.eat(2, 0.5f)

                if (player.level() is ServerLevel) {
                    (player.level() as ServerLevel).sendParticles(
                        ParticleTypes.DAMAGE_INDICATOR,
                        livingEntity!!.x,
                        livingEntity.y + livingEntity.eyeHeight / 2,
                        livingEntity.z,
                        10,
                        0.3,
                        0.3,
                        0.3,
                        0.1
                    )
                }
            }
        }

        val bl3 =
            player.fallDistance > 0.0f && !player.onGround() && !player.onClimbable() && !player.isInWater && !player.isPassenger

        if (wereLevel >= 6 && player.isSprinting && bl3) {
            AfflictionPlayerAttachment.smartUpdate(player) {
                incrementAirSlayMonster()
            }
        }
    }

    @JvmStatic
    fun tick(player: Player) {
        if (player !is ServerPlayer) return

        if (!player.level().isClientSide) {

            if (player.level().gameTime % 20 == 0L) {
                val wereData = AfflictionPlayerAttachment.getData(player)

                if (!player.level().isDay && player.level().moonPhase == 0) {
                    if (wereData.getWerewolfLevel() > 0) {
                        val type = TransformationPlayerAttachment.getData(player).transformationType
                        if (type == TransformationPlayerAttachment.TransformationType.NONE) {
                            tryForceTurnToWerewolf(player, wereData)
                        }
                    }

                } else if (player.level().isDay || player.level().moonPhase != 0) {
                    if (wereData.getWerewolfLevel() > 0) {
                        tryForceTurnWerewolfToHuman(player, wereData)
                    }
                }
            }

            if (TransformationHandler.isWolf(player)) {
                wolfTick(player)
            } else if (TransformationHandler.isWerewolf(player)) {
                werewolfTick(player)
            }
        }
    }

    fun modifyWerewolfDamage(
        attacker: Player,
        target: LivingEntity,
        originalDamage: Float
    ): Float {
        val wereData = AfflictionPlayerAttachment.getData(attacker)
        val wereLevel = wereData.getLevel(AfflictionTypes.LYCANTHROPY)

        if (!TransformationHandler.isWolf(attacker) && !TransformationHandler.isWerewolf(attacker)) {
            return originalDamage
        }

        var damage = originalDamage

        if (wereLevel >= 6 && attacker.isSprinting) {
            damage *= 1.5f

            if (attacker.fallDistance > 0.0f && !attacker.onGround() &&
                !attacker.isPassenger && !attacker.hasEffect(MobEffects.BLINDNESS)
            ) {
                damage *= 1.5f

                if (attacker.level() is ServerLevel) {
                    (attacker.level() as ServerLevel).sendParticles(
                        ParticleTypes.CRIT,
                        target.x,
                        target.y + target.eyeHeight / 2,
                        target.z,
                        15,
                        0.3,
                        0.3,
                        0.3,
                        0.2
                    )
                }
            }
        }

        return damage
    }

    fun handleHurtWolfman(damageSource: DamageSource, remainingDamage: Float): Float {
        var damage = remainingDamage

        damage *= 0.75f

        if (damageSource.`is`(DamageTypes.FALL)) {
            return 0f
        }

        return damage
    }

    fun handleHurtWolf(damageSource: DamageSource, remainingDamage: Float): Float {
        var damage = remainingDamage

        damage *= 0.85f

        if (damageSource.`is`(DamageTypes.FALL)) {
            damage *= 0.5f
        }

        return damage
    }

    private fun werewolfTick(player: Player) {
        TransformationPlayerAttachment.sync(player, TransformationPlayerAttachment.getData(player))
    }

    private fun wolfTick(player: Player) {
        TransformationPlayerAttachment.sync(player, TransformationPlayerAttachment.getData(player))
    }


    private fun tryForceTurnWerewolfToHuman(player: Player, data: AfflictionPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.removeForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.removeForm(player)
        }
    }

    private fun tryForceTurnToWerewolf(player: Player, data: AfflictionPlayerAttachment.Data) {
        if (data.getWerewolfLevel() < 3) {
            TransformationHandler.setWolfForm(player)
        } else if (data.getWerewolfLevel() < 5) {
            TransformationHandler.setWereWolfForm(player)
        }
    }

    fun infectPlayer(player: ServerPlayer) {
        if (AfflictionPlayerAttachment.getData(player).getWerewolfLevel() == 0) {
            WerewolfLeveling.increaseWerewolfLevel(player)
        }
    }
}