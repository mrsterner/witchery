package dev.sterner.witchery.item.brew

import dev.sterner.witchery.features.affliction.AfflictionAbilityHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

class BrewOfSoulSeveranceItem(color: Int, properties: Properties) : BrewItem(color, properties) {

    override fun applyEffectOnSelf(player: Player, hasFrog: Boolean) {
        if (player is ServerPlayer) {
            AfflictionAbilityHandler.addAbilityOnLevelUp(player, LichdomAbility.SOUL_FORM.requiredLevel, AfflictionTypes.LICHDOM, force = true)
            LichdomSpecificEventHandler.activateSoulForm(player)
        }
    }
}