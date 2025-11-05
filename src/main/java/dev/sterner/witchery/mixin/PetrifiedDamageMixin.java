package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.features.petrification.PetrificationHandler;
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PetrifiedDamageMixin {

    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setLastHurtByMob(Lnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private void witchery$removeDamageOverlay(LivingEntity instance, LivingEntity attacker, Operation<Void> original) {
        PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(instance);

        if (!data.isPetrified()) {
            original.call(instance, attacker);
        }
    }

    @Inject(
            method = "swing(Lnet/minecraft/world/InteractionHand;Z)V",
            at = @At("HEAD")
    )
    private void witchery$trackPetrifiedSwing(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player player) {
            PetrifiedEntityAttachment.Data data = PetrifiedEntityAttachment.INSTANCE.getData(player);

            if (data.isPetrified()) {
                PetrificationHandler.INSTANCE.handlePlayerPunch(player);
            }
        }
    }
}