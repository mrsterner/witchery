package dev.sterner.witchery.client.renderer.accessory

import com.mojang.blaze3d.vertex.PoseStack
import io.wispforest.accessories.api.client.AccessoryRenderer
import io.wispforest.accessories.api.client.DefaultAccessoryRenderer
import io.wispforest.accessories.api.slot.SlotReference
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack


class BatwingPendantRenderer : AccessoryRenderer {

    override fun <M : LivingEntity?> render(
        stack: ItemStack?,
        reference: SlotReference?,
        matrices: PoseStack?,
        model: EntityModel<M>?,
        multiBufferSource: MultiBufferSource?,
        light: Int,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {

    }
}