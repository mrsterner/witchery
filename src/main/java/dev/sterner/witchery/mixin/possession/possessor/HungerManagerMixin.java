package dev.sterner.witchery.mixin.possession.possessor;

import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {
    @Unique
    private static final ThreadLocal<Player> PLAYER_ENTITY_THREAD_LOCAL = new ThreadLocal<>();

    @Shadow
    private float exhaustionLevel;
    @Shadow private int foodLevel;

    @Inject(method = "tick", at = @At(value = "HEAD", ordinal = 0))
    private void updateSoulHunger(Player player, CallbackInfo ci) {
        Possessable possessed = (Possessable) PossessionComponentAttachment.INSTANCE.get(player).getHost();
        if (possessed != null && !possessed.isRegularEater()) {
            this.exhaustionLevel = 0;
            this.foodLevel = 20;
        }
        PLAYER_ENTITY_THREAD_LOCAL.set(player);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private float healPossessedEntity(float amount) {
        LivingEntity possessedEntity = PossessionComponentAttachment.INSTANCE.get(PLAYER_ENTITY_THREAD_LOCAL.get()).getHost();
        if (possessedEntity != null && ((Possessable) possessedEntity).isRegularEater()) {
            possessedEntity.heal(amount);
        }
        return amount;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float damagePossessedEntity(float amount) {
        LivingEntity possessedEntity = PossessionComponentAttachment.INSTANCE.get(PLAYER_ENTITY_THREAD_LOCAL.get()).getHost();
        if (possessedEntity != null && ((Possessable) possessedEntity).isRegularEater()) {
            possessedEntity.hurt(possessedEntity.damageSources().starve(), amount);
        }
        return amount;
    }
}