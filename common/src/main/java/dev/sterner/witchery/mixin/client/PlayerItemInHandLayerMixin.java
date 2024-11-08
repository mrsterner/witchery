package dev.sterner.witchery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment;
import dev.sterner.witchery.registry.WitcheryItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public class PlayerItemInHandLayerMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void witchery$hideWitchesHand(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci){
        if (livingEntity instanceof Player player && LightInfusionDataAttachment.isInvisible(player).isInvisible() && itemStack.is(WitcheryItems.INSTANCE.getWITCHES_HAND().get())) {
            ci.cancel();
        }
    }
}
