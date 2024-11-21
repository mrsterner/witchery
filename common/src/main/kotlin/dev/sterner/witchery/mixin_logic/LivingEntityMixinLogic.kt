package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.handler.PoppetHandler.handleVampiricPoppet
import dev.sterner.witchery.handler.vampire.VampireEventHandler
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import dev.sterner.witchery.platform.ManifestationPlayerAttachment.getData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.getPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.setPoppetData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
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

        val isVamp = entity is Player && VampirePlayerAttachment.getData(entity).vampireLevel > 0
        if (!isVamp) {

            val barkMitigated = BarkBeltPlayerAttachment.hurt(entity, damageSource, original)
            remainingDamage =- barkMitigated.coerceAtMost(original)

            if (remainingDamage > 0f) {
                remainingDamage = handleVampiricPoppet(entity, damageSource, remainingDamage)
            }
        } else {
            if (remainingDamage > 0f) {
                remainingDamage = VampireEventHandler.handleHurt(entity, damageSource, remainingDamage)
            }
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