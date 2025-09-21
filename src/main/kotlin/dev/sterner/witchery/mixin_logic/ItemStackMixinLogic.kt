package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.api.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetHandler.activatePoppet
import dev.sterner.witchery.registry.WitcheryPoppetRegistry
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player

object ItemStackMixinLogic {

    /**
     * Activates the armor protection poppet for the specified player, providing protection
     * against generic damage sources if the player meets the necessary conditions.
     *
     * @param player The player entity for which the armor protection poppet will be activated.
     *               If null, the method will return false.
     * @return True if the armor protection poppet was successfully activated for the player,
     *         otherwise false.
     */
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