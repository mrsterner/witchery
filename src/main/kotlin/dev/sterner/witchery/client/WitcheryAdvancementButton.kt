package dev.sterner.witchery.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class WitcheryAdvancementButton(
    x: Int,
    y: Int,
    onPress: OnPress?,
    tooltip: Tooltip?
) : Button(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT, Component.empty(), onPress, DEFAULT_NARRATION) {
    init {
        this.tooltip = tooltip
    }

    public override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {

        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(0f, 0f, 200f)

        val u = U
        val v = V_NORMAL

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        guiGraphics.blit(
            BUTTON_TEXTURE,
            this.x, this.y, u.toFloat(), v.toFloat(), this.width, this.height, 16, 16
        )

        guiGraphics.pose().popPose()
    }

    companion object {
        const val U: Int = 0
        const val V_NORMAL: Int = 0
        const val WIDTH: Int = 16
        const val HEIGHT: Int = 16


        private val BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("witchery", "textures/gui/advancement_button.png")
    }
}