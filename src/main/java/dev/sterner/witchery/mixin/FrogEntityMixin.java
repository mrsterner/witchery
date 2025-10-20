package dev.sterner.witchery.mixin;

import dev.sterner.witchery.features.ritual.RainingToadAttachment;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class FrogEntityMixin {

    @Inject(method = "setOnGround", at = @At("HEAD"))
    protected void witchery$onSetOnGround(boolean onGround, CallbackInfo ci) {

        Entity self = Entity.class.cast(this);
        if (onGround && self instanceof Frog frog) {
            RainingToadAttachment.Data data = RainingToadAttachment.getData(frog);
            data.setSafeFall(false);
            RainingToadAttachment.setData(frog, data);
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    protected void witchery$onPush(Entity entity, CallbackInfo ci) {
        Entity self = Entity.class.cast(this);
        if (self instanceof Frog frog && entity instanceof LivingEntity living) {
            RainingToadAttachment.Data data = RainingToadAttachment.getData(frog);
            if (data.isPoisonous()) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 5, 0));
            }
        }
    }
}
