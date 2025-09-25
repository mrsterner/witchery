package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

    @Shadow private float exhaustionLevel;
    @Shadow private int foodLevel;

    @Inject(method = "tick", at = @At(value = "HEAD", ordinal = 0))
    private void updateSoulHunger(Player player, CallbackInfo ci) {
        Mob possessed = PossessionManager.INSTANCE.getHost(player);
        if (possessed != null && !possessed.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER())) {
            this.exhaustionLevel = 0;
            this.foodLevel = 20;
        }
        PLAYER_ENTITY_THREAD_LOCAL.set(player);
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V")
    )
    private float healPossessedEntity(float amount) {
        Player player = PLAYER_ENTITY_THREAD_LOCAL.get();
        if (player != null) {
            Mob possessedEntity = PossessionManager.INSTANCE.getHost(player);
            if (possessedEntity != null && possessedEntity.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER())) {
                possessedEntity.heal(amount);
            }
        }
        return amount;
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 1
    )
    private float damagePossessedEntity(float amount) {
        Player player = PLAYER_ENTITY_THREAD_LOCAL.get();
        if (player != null) {
            Mob possessedEntity = PossessionManager.INSTANCE.getHost(player);
            if (possessedEntity != null && possessedEntity.getType().is(WitcheryTags.INSTANCE.getREGULAR_EATER())) {
                possessedEntity.hurt(possessedEntity.damageSources().starve(), amount);
            }
        }
        return amount;
    }
}