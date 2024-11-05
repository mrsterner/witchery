package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.HunterArmorModel
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

class HunterArmorRendererFabric : ArmorRenderer {

    var armor: HunterArmorModel? = null

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
            armor = HunterArmorModel(Minecraft.getInstance().entityModels.bakeLayer(HunterArmorModel.LAYER_LOCATION))
        } else {
            contextModel.copyPropertiesTo(armor!!)
            armor!!.setAllVisible(false)


            armor!!.head.visible = slot == EquipmentSlot.HEAD
            armor!!.body.visible = slot == EquipmentSlot.CHEST
            armor!!.leftArm.visible = slot == EquipmentSlot.CHEST
            armor!!.rightArm.visible = slot == EquipmentSlot.CHEST
            armor!!.leftLeg.visible = slot == EquipmentSlot.FEET
            armor!!.rightLeg.visible = slot == EquipmentSlot.FEET

        }

        val armorTexture = Witchery.id("textures/models/armor/witch_hunter_armor.png")
        val armorOverlayTexture = Witchery.id("textures/models/armor/witch_hunter_armor_overlay.png")

        val armorVertexConsumer = ItemRenderer.getArmorFoilBuffer(
            vertexConsumers,
            RenderType.armorCutoutNoCull(armorTexture),
            stack.hasFoil()
        )
        armor!!.renderToBuffer(
            matrices,
            armorVertexConsumer,
            light,
            OverlayTexture.NO_OVERLAY,
            DyeColor.BLACK.textureDiffuseColor
        )

        val armorOverlayVertexConsumer = ItemRenderer.getArmorFoilBuffer(
            vertexConsumers,
            RenderType.armorCutoutNoCull(armorOverlayTexture),
            stack.hasFoil()
        )
        armor!!.renderToBuffer(matrices, armorOverlayVertexConsumer, light, OverlayTexture.NO_OVERLAY)

    }
}