package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.ability.AfflictionAbility
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes

import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.vampire.VampireAbility
import dev.sterner.witchery.features.affliction.werewolf.WerewolfAbility
import dev.sterner.witchery.network.UpdateSelectedAbilitiesC2SPayload
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

class AbilitySelectionScreen(
    private val player: Player
) : Screen(Component.literal("Select Abilities")) {

    private val ABILITIES_PER_ROW = 5
    private val SLOT_SIZE = 26
    private val MAX_SELECTED = 5

    private var availableAbilities: List<AfflictionAbility> = emptyList()
    private var selectedAbilities: MutableList<String> = mutableListOf()
    private var hoveredAbility: AfflictionAbility? = null

    override fun init() {
        super.init()
        availableAbilities = AfflictionAbilityHandler.getAllAvailableAbilities(player)

        val data = AfflictionPlayerAttachment.getData(player)
        selectedAbilities = data.getSelectedAbilities().toMutableList()

        addRenderableWidget(
            Button.builder(
                Component.literal("Confirm"),
                { button ->
                    saveAndClose()
                }
            ).pos(width / 2 - 50, height - 30)
                .size(100, 20)
                .build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {

        guiGraphics.drawCenteredString(
            font,
            "Select 5 Active Abilities",
            width / 2,
            20,
            0xFFFFFF
        )

        val selectedColor = if (selectedAbilities.size == MAX_SELECTED) 0x00FF00 else 0xFFFFFF
        guiGraphics.drawString(
            font,
            "Selected: ${selectedAbilities.size}/$MAX_SELECTED",
            width / 2 - 40,
            35,
            selectedColor
        )

        renderAbilityGrid(guiGraphics, mouseX, mouseY)

        renderSelectedBar(guiGraphics)

        hoveredAbility?.let {
            renderTooltip(guiGraphics, it, mouseX, mouseY)
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun renderBackground(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {

    }

    private fun renderAbilityGrid(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val startX = width / 2 - (ABILITIES_PER_ROW * SLOT_SIZE) / 2
        val startY = 60

        hoveredAbility = null

        availableAbilities.forEachIndexed { index, ability ->
            val row = index / ABILITIES_PER_ROW
            val col = index % ABILITIES_PER_ROW

            val x = startX + col * SLOT_SIZE
            val y = startY + row * SLOT_SIZE

            val isSelected = selectedAbilities.contains(ability.id)
            val slotColor = when {
                isSelected -> 0x4400FF00
                isMouseOver(mouseX, mouseY, x, y, SLOT_SIZE, SLOT_SIZE) -> 0x44FFFFFF
                else -> 0x44000000
            }

            guiGraphics.fill(x, y, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, slotColor)
            guiGraphics.renderOutline(x, y, SLOT_SIZE - 2, SLOT_SIZE - 2, 0xFFAAAAAA.toInt())

            val iconPath = when (ability) {
                is VampireAbility -> "textures/gui/affliction_abilities/${ability.id}.png"
                is WerewolfAbility -> "textures/gui/affliction_abilities/${ability.id}.png"
                is LichdomAbility -> "textures/gui/affliction_abilities/${ability.id}.png"
                else -> "textures/gui/affliction_abilities/unknown.png"
            }

            guiGraphics.blit(
                Witchery.id(iconPath),
                x + 4, y + 4,
                16, 16,
                0f, 0f, 16, 16,
                16, 16
            )

            if (isMouseOver(mouseX, mouseY, x, y, SLOT_SIZE, SLOT_SIZE)) {
                hoveredAbility = ability
            }
        }
    }

    private fun renderSelectedBar(guiGraphics: GuiGraphics) {
        val barY = height - 70
        val barWidth = MAX_SELECTED * SLOT_SIZE
        val startX = width / 2 - barWidth / 2

        guiGraphics.fill(startX - 2, barY - 2, startX + barWidth + 2, barY + SLOT_SIZE, 0x88000000.toInt())

        for (i in 0 until MAX_SELECTED) {
            val x = startX + i * SLOT_SIZE

            guiGraphics.renderOutline(x, barY, SLOT_SIZE - 2, SLOT_SIZE - 2, 0xFFFFFFFF.toInt())

            if (i < selectedAbilities.size) {
                val abilityId = selectedAbilities[i]
                val ability = availableAbilities.find { it.id == abilityId }

                ability?.let {
                    val iconPath = "textures/gui/affliction_abilities/${it.id}.png"
                    guiGraphics.blit(
                        Witchery.id(iconPath),
                        x + 5, barY + 5,
                        16, 16,
                        0f, 0f, 16, 16,
                        16, 16
                    )
                }
            }
        }

        guiGraphics.drawCenteredString(
            font,
            "Active Abilities",
            width / 2,
            barY - 15,
            0xFFFFFF
        )
    }

    private fun renderTooltip(guiGraphics: GuiGraphics, ability: AfflictionAbility, mouseX: Int, mouseY: Int) {
        val tooltip = mutableListOf<Component>()

        tooltip.add(
            Component.literal(ability.id.replace('_', ' ').capitalize())
                .withStyle(ChatFormatting.YELLOW)
        )

        val typeColor = when (ability.affliction) {
            AfflictionTypes.VAMPIRISM -> ChatFormatting.RED
            AfflictionTypes.LYCANTHROPY -> ChatFormatting.GOLD
            else -> ChatFormatting.WHITE
        }
        tooltip.add(
            Component.literal("Type: ${ability.affliction.name}")
                .withStyle(typeColor)
        )

        tooltip.add(
            Component.literal("Required Level: ${ability.requiredLevel}")
                .withStyle(ChatFormatting.GRAY)
        )

        val isSelected = selectedAbilities.contains(ability.id)
        if (isSelected) {
            tooltip.add(
                Component.literal("Selected")
                    .withStyle(ChatFormatting.GREEN)
            )
        } else if (selectedAbilities.size >= MAX_SELECTED) {
            tooltip.add(
                Component.literal("Max abilities selected")
                    .withStyle(ChatFormatting.RED)
            )
        } else {
            tooltip.add(
                Component.literal("Click to select")
                    .withStyle(ChatFormatting.GREEN)
            )
        }

        guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val startX = width / 2 - (ABILITIES_PER_ROW * SLOT_SIZE) / 2
            val startY = 60

            availableAbilities.forEachIndexed { index, ability ->
                val row = index / ABILITIES_PER_ROW
                val col = index % ABILITIES_PER_ROW

                val x = startX + col * SLOT_SIZE
                val y = startY + row * SLOT_SIZE

                if (isMouseOver(mouseX.toInt(), mouseY.toInt(), x, y, SLOT_SIZE, SLOT_SIZE)) {
                    toggleAbility(ability)
                    return true
                }
            }

            val barY = height - 70
            val barWidth = MAX_SELECTED * SLOT_SIZE
            val barStartX = width / 2 - barWidth / 2

            for (i in selectedAbilities.indices) {
                val x = barStartX + i * SLOT_SIZE
                if (isMouseOver(mouseX.toInt(), mouseY.toInt(), x, barY, SLOT_SIZE, SLOT_SIZE)) {
                    selectedAbilities.removeAt(i)
                    minecraft?.soundManager?.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                    return true
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun toggleAbility(ability: AfflictionAbility) {
        if (selectedAbilities.contains(ability.id)) {
            selectedAbilities.remove(ability.id)
            minecraft?.soundManager?.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.8f))
        } else if (selectedAbilities.size < MAX_SELECTED) {
            selectedAbilities.add(ability.id)
            minecraft?.soundManager?.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2f))
        } else {
            minecraft?.soundManager?.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.5f))
        }
    }

    private fun isMouseOver(mouseX: Int, mouseY: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
    }

    private fun saveAndClose() {
        if (minecraft?.player != null) {
            if (selectedAbilities.isEmpty()) {
                player.inventory.selected = 0
            }

            PacketDistributor.sendToServer(UpdateSelectedAbilitiesC2SPayload(selectedAbilities))
        }

        onClose()
    }

    override fun isPauseScreen(): Boolean = false
}