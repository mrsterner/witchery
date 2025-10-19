package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.features.affliction.event.AfflictionEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1), cancellable = true)
    private void rightClickAir(CallbackInfo ci, @Local ItemStack itemStack, @Local InteractionHand interactionHand) {
        if (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS) {
            var bl = AfflictionEventHandler.INSTANCE.clientRightClickAbility(player, interactionHand);
            if (bl) {
                ci.cancel();
            }
        }
    }
}
