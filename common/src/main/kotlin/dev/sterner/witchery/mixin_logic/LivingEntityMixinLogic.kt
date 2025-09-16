package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.handler.BarkBeltHandler
import dev.sterner.witchery.handler.PotionHandler
import dev.sterner.witchery.handler.affliction.AfflictionHandler
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.handler.affliction.WerewolfSpecificEventHandler
import dev.sterner.witchery.handler.poppet.PoppetHandler
import dev.sterner.witchery.handler.affliction.TransformationHandler
import dev.sterner.witchery.platform.ManifestationPlayerAttachment.getData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.getPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.setPoppetData
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object LivingEntityMixinLogic {

    fun modifyHurtGhost(livingEntity: LivingEntity, original: Float): Float {
        if (livingEntity is Player) {
            if (getData(livingEntity).manifestationTimer > 0) {
                return 0f
            }
        }
        return original
    }

    fun modifyHurt(entity: LivingEntity, original: Float, damageSource: DamageSource): Float {
        var remainingDamage = original

        if (damageSource.entity is Player) {
            val attacker = damageSource.entity as Player
            val wereData = AfflictionPlayerAttachment.getData(attacker)

            if (wereData.getLevel(AfflictionTypes.LYCANTHROPY) > 0) {
                if (TransformationHandler.isWolf(attacker) || TransformationHandler.isWerewolf(attacker)) {
                    remainingDamage = WerewolfSpecificEventHandler.modifyWerewolfDamage(
                        attacker, entity, damageSource, remainingDamage
                    )
                }
            }
        }

        val isVamp = entity is Player && AfflictionPlayerAttachment.getData(entity).getLevel(AfflictionTypes.VAMPIRISM) > 0
        val isWereMan = entity is Player && AfflictionPlayerAttachment.getData(entity).isWolfManForm()
        val isWere = entity is Player && AfflictionPlayerAttachment.getData(entity).isWolfForm()

        if (!isVamp && !isWere && !isWereMan) {
            val barkMitigated = BarkBeltHandler.hurt(entity, damageSource, remainingDamage)
            remainingDamage = barkMitigated.coerceAtMost(remainingDamage)

            if (remainingDamage > 0f) {
                remainingDamage = PoppetHandler.onLivingHurt(entity, damageSource, remainingDamage)
            }
        } else if (isVamp) {
            if (remainingDamage > 0f) {
                remainingDamage = AfflictionHandler.handleHurt(entity, damageSource, remainingDamage)
            }
        } else if (isWereMan) {
            if (remainingDamage > 0f) {
                remainingDamage = WerewolfSpecificEventHandler.handleHurtWolfman(entity, damageSource, remainingDamage)
            }
        } else if (isWere) {
            if (remainingDamage > 0f) {
                remainingDamage = WerewolfSpecificEventHandler.handleHurtWolf(entity, damageSource, remainingDamage)
            }
        }

        if (remainingDamage > 0f) {
            remainingDamage = PotionHandler.handleHurt(entity, damageSource, remainingDamage)
        }

        return remainingDamage
    }

    fun modifyBaseTick(livingEntity: LivingEntity) {
        val prevData = getPoppetData(livingEntity)

        if (prevData.underWaterTicks > 0) {
            val newTicks = prevData.underWaterTicks - 1

            setPoppetData(livingEntity, VoodooPoppetData(
                isUnderWater = true,
                underWaterTicks = newTicks
            ))
        } else if (prevData.isUnderWater) {
            setPoppetData(livingEntity, VoodooPoppetData(
                isUnderWater = false,
                underWaterTicks = 0
            ))
        }
    }

}