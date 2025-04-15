package dev.sterner.witchery.mixin.client;

import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @ModifyArgs(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    private void witchery$preventGroundBatMovement(Args args) {
        LocalPlayer player = LocalPlayer.class.cast(this);

        if (TransformationPlayerAttachment.isBat(player) && player.onGround()) {
            Vec3 old = args.get(1);
            args.set(1, new Vec3(0f, old.y, 0f));
        }
    }
}