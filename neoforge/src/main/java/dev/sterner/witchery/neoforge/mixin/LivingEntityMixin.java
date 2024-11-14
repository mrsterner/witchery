package dev.sterner.witchery.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.handler.PoppetHandler;
import dev.sterner.witchery.handler.VampireHandler;
import dev.sterner.witchery.platform.ManifestationPlayerAttachment;
import dev.sterner.witchery.platform.poppet.VoodooPoppetLivingEntityAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurt(float original, @Local(argsOnly = true) DamageSource damageSource) {
        var entity = LivingEntity.class.cast(this);
        var f = PoppetHandler.INSTANCE.handleVampiricPoppet(entity, damageSource, original);
        if (f != 0) {
            f = VampireHandler.INSTANCE.handleHurt(entity, damageSource, original);
        }
        return f;
    }

    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float witchery$modifyHurtGhost(float original, @Local(argsOnly = true) DamageSource damageSource) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        if (livingEntity instanceof Player player) {
            if (ManifestationPlayerAttachment.getData(player).getManifestationTimer() > 0) {
                return 0;
            }
        }
        return original;
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void witchery$modifyBaseTick(CallbackInfo ci) {
        LivingEntity livingEntity = LivingEntity.class.cast(this);
        var prevData = VoodooPoppetLivingEntityAttachment.getPoppetData(livingEntity);
        if (prevData.isUnderWater())  {
            VoodooPoppetLivingEntityAttachment.setPoppetData(livingEntity, new VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false));
        }
    }
}
