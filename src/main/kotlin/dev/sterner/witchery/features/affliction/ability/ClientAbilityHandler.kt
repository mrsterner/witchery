package dev.sterner.witchery.features.affliction.ability

import dev.sterner.witchery.client.screen.AbilitySelectionScreen
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
object ClientAbilityHandler {

    fun getClientPlayer(): Player? {
        return Minecraft.getInstance().player
    }

    fun handleScroll(scrollDeltaX: Double, scrollDeltaY: Double): Boolean {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return false
        val abilities = AfflictionAbilityHandler.getAbilities(player)
        if (abilities.isEmpty()) return false

        return AbilityScrollHandler().handleScroll(player, scrollDeltaY, AfflictionAbilityHandler)
    }

    fun openSelectionScreen(player: Player) {
        Minecraft.getInstance().setScreen(AbilitySelectionScreen(player))
    }
}
