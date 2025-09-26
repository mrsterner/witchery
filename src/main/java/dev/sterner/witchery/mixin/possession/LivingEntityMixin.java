package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.util.DamageHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected int lastHurtByPlayerTime;

    @Shadow @Nullable protected Player lastHurtByPlayer;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyArg(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private int spawnFewerFallParticles(int amount) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            return amount / 4;
        }
        return amount;
    }

    @Inject(method = "isPickable", at = @At("RETURN"), cancellable = true)
    private void requiem$preventTargetingSouls(CallbackInfoReturnable<Boolean> info) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = {"doPush", "push"}, at = @At("HEAD"), cancellable = true)
    private void stopPushingAway(Entity entity, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self instanceof Player player && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "dropAllDeathLoot", at = @At(value = "HEAD"), argsOnly = true)
    private DamageSource enableHumanity(DamageSource deathCause) {
        Player possessor = null;
        if (deathCause.getEntity() != null) {
            possessor = ((Possessable) deathCause.getEntity()).getPossessor();
        }
        if (possessor != null) {
            this.lastHurtByPlayerTime = 100;
            this.lastHurtByPlayer = possessor;
            DamageSource proxiedDamage = DamageHelper.INSTANCE.createProxiedDamage(deathCause, possessor);
            if (proxiedDamage != null) {
                return proxiedDamage;
            }
        }
        return deathCause;
    }
}
