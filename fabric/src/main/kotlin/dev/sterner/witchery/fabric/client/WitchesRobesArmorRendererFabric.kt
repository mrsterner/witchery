package dev.sterner.witchery.fabric.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.effect.MobEffects
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

            if (entity.hasEffect(MobEffects.INVISIBILITY)) {
                armor!!.head.visible = false
                armor!!.body.visible = false
                armor!!.leftArm.visible = false
                armor!!.rightArm.visible = false
                armor!!.leftLeg.visible = false
                armor!!.rightLeg.visible = false
            } else {
                armor!!.head.visible = slot == EquipmentSlot.HEAD
                armor!!.body.visible = slot == EquipmentSlot.CHEST
                armor!!.leftArm.visible = slot == EquipmentSlot.CHEST
                armor!!.rightArm.visible = slot == EquipmentSlot.CHEST
                armor!!.leftLeg.visible = slot == EquipmentSlot.FEET
                armor!!.rightLeg.visible = slot == EquipmentSlot.FEET
            }
        }


        if (slot == EquipmentSlot.HEAD && stack.`is`(WitcheryItems.BABA_YAGAS_HAT.get())) {
            val babaTexture = Witchery.id("textures/models/armor/baba_yagas_hat.png")
            val babaVertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(babaTexture), stack.hasFoil())
            armor!!.renderToBuffer(matrices, babaVertexConsumer, light, OverlayTexture.NO_OVERLAY)
        } else if (slot == EquipmentSlot.HEAD) {
            val armorTexture = Witchery.id("textures/models/armor/witches_robes.png")
            val armorVertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(armorTexture), stack.hasFoil())
            armor!!.renderToBuffer(matrices, armorVertexConsumer, light, OverlayTexture.NO_OVERLAY, DyeColor.BLACK.textureDiffuseColor)
        }

        if (slot != EquipmentSlot.HEAD) {
            val armorTexture = Witchery.id("textures/models/armor/witches_robes.png")
            val armorVertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(armorTexture), stack.hasFoil())
            armor!!.renderToBuffer(matrices, armorVertexConsumer, light, OverlayTexture.NO_OVERLAY, DyeColor.BLACK.textureDiffuseColor)
        }

    }
}