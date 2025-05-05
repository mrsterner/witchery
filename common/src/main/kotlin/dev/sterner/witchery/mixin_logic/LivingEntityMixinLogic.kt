package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.handler.BarkBeltHandler
import dev.sterner.witchery.handler.PoppetHandler.handleVampiricPoppet
import dev.sterner.witchery.handler.PotionHandler
import dev.sterner.witchery.handler.vampire.VampireEventHandler
import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import dev.sterner.witchery.platform.ManifestationPlayerAttachment.getData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.getPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.setPoppetData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
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

        val isVamp = entity is Player && VampirePlayerAttachment.getData(entity).getVampireLevel() > 0
        val isWereMan = entity is Player && WerewolfPlayerAttachment.getData(entity).isWolfManFormActive
        val isWere = entity is Player && WerewolfPlayerAttachment.getData(entity).isWolfFormActive
        if (!isVamp && !isWere) {
            val barkMitigated = BarkBeltHandler.hurt(entity, damageSource, remainingDamage)
            remainingDamage = barkMitigated.coerceAtMost(remainingDamage)

            if (remainingDamage > 0f) {
                remainingDamage = handleVampiricPoppet(entity, damageSource, remainingDamage)
            }
        } else if (isVamp) {

            if (remainingDamage > 0f) {
                remainingDamage = VampireEventHandler.handleHurt(entity, damageSource, remainingDamage)
            }
        } else if (isWereMan) {
            if (remainingDamage > 0f) {
                remainingDamage = WerewolfEventHandler.handleHurtWolfman(entity, damageSource, remainingDamage)
            }
        } else if (isWere) {
            if (remainingDamage > 0f) {
                remainingDamage = WerewolfEventHandler.handleHurtWolf(entity, damageSource, remainingDamage)
            }
        }

        if (remainingDamage > 0f) {
            remainingDamage = PotionHandler.handleHurt(entity, damageSource, remainingDamage)
        }

        return remainingDamage
    }

    fun modifyBaseTick(livingEntity: LivingEntity) {
        val prevData = getPoppetData(livingEntity)
        if (prevData.isUnderWater) {
            setPoppetData(livingEntity, VoodooPoppetData(false))
        }
    }
}