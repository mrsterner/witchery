package dev.sterner.witchery.client.screen


import dev.sterner.witchery.client.hud.HudPositionData
import dev.sterner.witchery.client.hud.HudPositionData.*
import dev.sterner.witchery.core.registry.WitcheryKeyMappings
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth

class HudEditorScreen : Screen(Component.literal("HUD Editor")) {

    private var draggedElement: HudElement? = null
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    private val hudElements = mutableListOf<HudElement>()

    enum class ElementType {
        INFUSION,
        MANIFESTATION,
        BARK_BELT,
        QUEST_HUD
    }

    data class HudElement(
        val type: ElementType,
        var x: Int,
        var y: Int,
        var width: Int,
        var height: Int,
        var label: String
    )

    override fun init() {
        super.init()

        val player = minecraft!!.player ?: return
        val positions = HudPlayerAttachment.getData(player).hudPositions

        hudElements.clear()

        val (infusionX, infusionY) = positions.getInfusionPos(height)
        hudElements.add(
            HudElement(
                ElementType.INFUSION,
                infusionX - 4,
                infusionY - 4,
                23,
                55,
                "Infusion Meter"
            )
        )

        val (manifestationX, manifestationY) = positions.getManifestationPos(height)
        hudElements.add(
            HudElement(
                ElementType.MANIFESTATION,
                manifestationX - 4,
                manifestationY - 4,
                20,
                32,
                "Manifestation Meter"
            )
        )

        val (barkX, barkY) = positions.getBarkBeltPos(width, height)
        hudElements.add(
            HudElement(
                ElementType.BARK_BELT,
                barkX - 4,
                barkY - 4,
                88,
                16,
                "Bark Belt"
            )
        )

        val (questX, questY) = positions.getQuestHudPos(width, height)
        hudElements.add(
            HudElement(
                ElementType.QUEST_HUD,
                questX - 4,
                questY - 4,
                64,
                64,
                "Quests"
            )
        )

        addRenderableWidget(
            Button.builder(Component.literal("Reset Positions")) { _ ->
                resetPositions()
            }
                .bounds(width / 2 - 75, height - 30, 150, 20)
                .build()
        )
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.fill(
            RenderType.guiOverlay(),
            0, 0, width, height,
            0x88000000.toInt()
        )

        for (el in hudElements) {
            val isHovered = isMouseOver(mouseX, mouseY, el)
            val isDragged = draggedElement == el

            val color = when {
                isDragged -> 0xFF00FF00.toInt()
                isHovered -> 0xFFFFFF00.toInt()
                else -> 0xFF888888.toInt()
            }

            guiGraphics.renderOutline(el.x, el.y, el.width, el.height, color)

            guiGraphics.fill(
                RenderType.guiOverlay(),
                el.x + 1, el.y + 1, el.x + el.width - 1, el.y + el.height - 1,
                0x44444444
            )

            val labelWidth = font.width(el.label)
            guiGraphics.drawString(
                font,
                el.label,
                el.x + (el.width - labelWidth) / 2,
                el.y - 12,
                0xFFFFFF
            )
        }

        guiGraphics.drawCenteredString(
            font,
            "Click and drag HUD elements to reposition them",
            width / 2,
            10,
            0xFFFFFF
        )

        for (renderable in this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            for (el in hudElements) {
                if (isMouseOver(mouseX.toInt(), mouseY.toInt(), el)) {
                    draggedElement = el
                    dragOffsetX = mouseX.toInt() - el.x
                    dragOffsetY = mouseY.toInt() - el.y
                    return true
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && draggedElement != null) {
            savePositions()
            draggedElement = null
            return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (button == 0 && draggedElement != null) {
            val el = draggedElement!!

            var newX = mouseX.toInt() - dragOffsetX
            var newY = mouseY.toInt() - dragOffsetY

            newX = Mth.clamp(newX, 0, width - el.width)
            newY = Mth.clamp(newY, 0, height - el.height)

            el.x = newX
            el.y = newY

            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    private fun isMouseOver(mouseX: Int, mouseY: Int, el: HudElement): Boolean {
        return mouseX >= el.x && mouseX <= el.x + el.width &&
                mouseY >= el.y && mouseY <= el.y + el.height
    }

    private fun savePositions() {
        val player = minecraft!!.player ?: return

        var infusionCoord = HudPositionData.Coord(10, -1)
        var manifestationCoord = HudPositionData.Coord(28, -1)
        var barkCoord = HudPositionData.Coord(-1, -1)
        var questCoord = HudPositionData.Coord(10, -1)

        for (el in hudElements) {
            when (el.type) {
                ElementType.INFUSION -> {
                    infusionCoord = Coord(el.x + 4, el.y + 4)
                }
                ElementType.MANIFESTATION -> {
                    manifestationCoord = Coord(el.x + 4, el.y + 4)
                }
                ElementType.BARK_BELT -> {
                    barkCoord = Coord(el.x + 4, el.y + 4)
                }

                ElementType.QUEST_HUD -> {
                    questCoord = Coord(el.x + 4, el.y + 4)
                }
            }
        }

        val newPositions = HudPositionData(
            infusionCoord,
            manifestationCoord,
            barkCoord,
            questCoord
        )

        HudPlayerAttachment.setHudPositions(player, newPositions)
    }

    private fun resetPositions() {
        val player = minecraft!!.player ?: return
        HudPlayerAttachment.setHudPositions(player, HudPositionData())
        onClose()
        minecraft!!.setScreen(HudEditorScreen())
    }

    override fun isPauseScreen(): Boolean = false

    companion object {
        fun handleKeyPress() {
            val mc = Minecraft.getInstance()

            if (WitcheryKeyMappings.EDIT_HUD.consumeClick()) {
                mc.setScreen(HudEditorScreen())
            }
        }
    }
}