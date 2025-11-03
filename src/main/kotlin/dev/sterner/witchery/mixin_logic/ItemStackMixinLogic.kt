package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.features.poppet.PoppetHandler.activatePoppet
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player

object ItemStackMixinLogic {

    fun armorProtection(player: Player?): Boolean {
        if (player != null) {
            val armorPoppetType: PoppetType = WitcheryPoppetRegistry.ARMOR_PROTECTION.get()

            val armorDamageSource: DamageSource = player.level().damageSources().generic()

            if (activatePoppet(player, armorPoppetType, armorDamageSource)) {
                return true
            }
        }
        return false
    }
}