package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.SlotWidget
import net.minecraft.client.gui.GuiGraphics
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class WitcherySlotWidget(stack: EmiStack?, x: Int, y: Int, val scale: Double = 1.0) : SlotWidget(stack, x, y) {

    override fun shouldDrawSlotHighlight(mouseX: Int, mouseY: Int): Boolean {
        return false
    }

    override fun render(draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(draw, mouseX, mouseY, delta)
    }

    override fun drawStack(draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val pose = draw.pose()
        val bounds = getBounds()

        // Calculate the center offset and account for scale
        val xOff = (bounds.width() - (16 * scale)).toInt() / 2
        val yOff = (bounds.height() - (16 * scale)).toInt() / 2

        // Translate and scale to ensure proper centering
        pose.pushPose()

        // Translate to the scaled position
        pose.translate((bounds.x() + xOff).toDouble(), (bounds.y() + yOff).toDouble(), 0.0)

        // Apply scaling
        pose.scale(scale.toFloat(), scale.toFloat(), 1f)

        // Render the stack at its scaled position
        getStack().render(draw, 0, 0, delta)

        pose.popPose()
    }
}