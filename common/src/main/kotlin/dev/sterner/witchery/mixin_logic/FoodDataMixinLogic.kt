package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object FoodDataMixinLogic {

    fun getFood(
        player: Player?,
        cir: CallbackInfoReturnable<Int>
    ) {
        if (player != null && getData(player).vampireLevel > 0) {
            val bloodData = BloodPoolLivingEntityAttachment.getData(player)
            val maxBlood = bloodData.maxBlood
            if (maxBlood > 0) {
                val blood = bloodData.bloodPool
                val scaled = ((blood / maxBlood.toFloat()) * 20).toInt()
                cir.returnValue = scaled
            }
        }
    }

    fun getSaturation(player: Player?, cir : CallbackInfoReturnable<Float>) {
        if (player != null && getData(player).vampireLevel > 0) {
            cir.setReturnValue(0f)
        }
    }

    fun onAdd(instance: Player, foodLevel: Int, saturationLevel: Float) {
        if (getData(instance).vampireLevel == 0) {
            val bloodData = BloodPoolLivingEntityAttachment.getData(instance)
            if (bloodData.bloodPool < bloodData.maxBlood) {
                BloodPoolLivingEntityAttachment.increaseBlood(instance, foodLevel)
            }
        }
    }
}