package dev.sterner.witchery.mixin.client;

import dev.sterner.witchery.api.WitcheryApi;
import dev.sterner.witchery.handler.CurseHandler;
import dev.sterner.witchery.features.affliction.event.TransformationHandler;
import dev.sterner.witchery.registry.WitcheryCurseRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Shadow
    public Input input;

    @Unique
    private long witchery$lastInputDisruptTime = 0L;

    @Unique
    private static final long witchery$INPUT_DISRUPT_COOLDOWN = 40L;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void witchery$onAiStep(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Level level = player.level();

        if (!CurseHandler.INSTANCE.hasCurse(player, WitcheryCurseRegistry.INSTANCE.getBEFUDDLEMENT().get())) {
            return;
        }

        long currentTime = level.getGameTime();
        if (currentTime - witchery$lastInputDisruptTime < witchery$INPUT_DISRUPT_COOLDOWN) {
            return;
        }

        float effectivenessMultiplier = WitcheryApi.INSTANCE.isWitchy(player) ? 1.0f : 0.3f;
        float disruptChance = 0.05f * effectivenessMultiplier;

        if (level.random.nextFloat() < disruptChance) {
            witchery$disruptInput(player);
            witchery$lastInputDisruptTime = currentTime;
        }
    }

    @Unique
    private void witchery$disruptInput(LocalPlayer player) {
        int randomInput = player.level().random.nextInt(5);

        switch (randomInput) {
            case 0:
                input.forwardImpulse = 1.0f;
                break;
            case 1:
                input.forwardImpulse = -1.0f;
                break;
            case 2:
                input.leftImpulse = 1.0f;
                break;
            case 3:
                input.leftImpulse = -1.0f;
                break;
            case 4:
                input.jumping = true;
                break;
        }

        witchery$playDisruptSound(player);

        Minecraft.getInstance().tell(() -> {
            if (CurseHandler.INSTANCE.hasCurse(player, WitcheryCurseRegistry.INSTANCE.getBEFUDDLEMENT().get())) {
                input.forwardImpulse = 0.0f;
                input.leftImpulse = 0.0f;
                input.jumping = false;
            }
        });
    }

    @Unique
    private void witchery$playDisruptSound(LocalPlayer player) {
        player.level().playLocalSound(
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.NOTE_BLOCK_HAT.value(),
                SoundSource.PLAYERS,
                0.3f,
                0.5f + player.level().random.nextFloat() * 0.5f,
                false
        );
    }

    @ModifyArgs(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    private void witchery$preventGroundBatMovement(Args args) {
        LocalPlayer player = LocalPlayer.class.cast(this);

        if (TransformationHandler.isBat(player) && player.onGround()) {
            Vec3 old = args.get(1);
            args.set(1, new Vec3(0f, old.y, 0f));
        }
    }
}