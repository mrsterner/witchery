package dev.sterner.witchery.features.affliction

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.ability.AbilityCooldownManager
import dev.sterner.witchery.features.affliction.ability.AfflictionAbility
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player

object AfflictionClientEventHandler {

    private val overlay = Witchery.id("textures/gui/ability_hotbar_selection.png")

    fun drawAbilityBar(guiGraphics: GuiGraphics, player: Player, x: Int, y: Int) {
        val abilityIndex = AfflictionPlayerAttachment.getData(player).getAbilityIndex()
        val abilities: List<AfflictionAbility> = AfflictionAbilityHandler.getAbilities(player)

        for (i in abilities.indices) {
            val name = abilities[i].id

            val iconX = x - (25 * i) + 4
            val iconY = y + 4

            guiGraphics.blit(
                Witchery.id("textures/gui/affliction_abilities/${name}.png"),
                iconX, iconY,
                16, 16,
                0f, 0f, 16, 16,
                16, 16
            )

            val cooldown = AbilityCooldownManager.getCooldown(player, abilities[i])
            if (cooldown > 0) {
                drawCooldownOverlay(guiGraphics, iconX, iconY, cooldown, abilities[i].cooldown)
            }
        }

        if (abilityIndex != -1) {
            guiGraphics.blit(overlay, x - (25 * abilityIndex), y, 24, 23, 0f, 0f, 24, 23, 24, 23)
        }
    }

    private fun drawCooldownOverlay(
        guiGraphics: GuiGraphics,
        iconX: Int,
        iconY: Int,
        currentCooldown: Int,
        maxCooldown: Int
    ) {
        val cooldownPercent = currentCooldown.toFloat() / maxCooldown.toFloat()
        val fillStart = iconY + Mth.floor(16f * (1.0f - cooldownPercent))
        val fillEnd = fillStart + Mth.ceil(16f * cooldownPercent)

        guiGraphics.fill(
            RenderType.guiOverlay(),
            iconX,
            fillStart,
            iconX + 16,
            fillEnd,
            0xAA000000.toInt()
        )
    }
}