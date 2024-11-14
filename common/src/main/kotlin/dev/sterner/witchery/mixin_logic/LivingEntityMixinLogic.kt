package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.handler.PoppetHandler.handleVampiricPoppet
import dev.sterner.witchery.handler.VampireHandler.handleHurt
import dev.sterner.witchery.platform.ManifestationPlayerAttachment.getData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.getPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment.setPoppetData
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
        var f = handleVampiricPoppet(entity, damageSource, original)
        if (f != 0f) {
            f = handleHurt(entity, damageSource, original)
        }
        return f
    }

    fun modifyBaseTick(livingEntity: LivingEntity) {
        val prevData = getPoppetData(livingEntity)
        if (prevData.isUnderWater) {
            setPoppetData(livingEntity, VoodooPoppetData(false))
        }
    }
}