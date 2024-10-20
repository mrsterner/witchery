package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.WitchesRobesModel
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import java.awt.Color

class WitchesRobesArmorRendererFabric : ArmorRenderer {

    var armor: WitchesRobesModel? = null

    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        stack: ItemStack,
        entity: LivingEntity,
        slot: EquipmentSlot,
        light: Int,
        contextModel: HumanoidModel<LivingEntity>
    ) {

        if (armor == null) {
            armor = WitchesRobesModel(Minecraft.getInstance().entityModels.bakeLayer(WitchesRobesModel.LAYER_LOCATION))
        } else {
            contextModel.copyPropertiesTo(armor!!)
            armor!!.setAllVisible(false)

            armor!!.head.visible = slot == EquipmentSlot.HEAD
            armor!!.body.visible = slot == EquipmentSlot.HEAD
            armor!!.leftArm.visible = slot == EquipmentSlot.CHEST
            armor!!.leftLeg.visible = slot == EquipmentSlot.FEET
            armor!!.rightArm.visible = slot == EquipmentSlot.CHEST
            armor!!.rightLeg.visible = slot == EquipmentSlot.FEET
        }

        if (armor != null) {
            val vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(Witchery.id("textures/models/armor/witches_robes.png")), stack.hasFoil())
            armor!!.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, DyeColor.BLACK.textureDiffuseColor)
        }
    }
}