package dev.sterner.witchery.mixin.possession.client;


import com.mojang.authlib.GameProfile;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    @Shadow
    public Input input;

    public LocalPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "suffocatesAt", at = @At(value = "RETURN"), cancellable = true)
    private void witchery$suffocatesAt(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            Entity possessed = PossessionComponentAttachment.INSTANCE.get(this).getHost();
            if (possessed != null && possessed.getBbHeight() < 1F) {
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyArg(
            method = "aiStep",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSwimming()Z", ordinal = 1)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V", ordinal = 0)
    )
    private boolean witchery$aiStep(boolean value) {
        LocalPlayer self = (LocalPlayer)(Object)this;
        if (this.getAbilities().flying && this.input.forwardImpulse > 0F && this.isSprinting() && AfflictionPlayerAttachment.getData(self).isSoulForm()) {
            return true;
        }
        return value;
    }
}
