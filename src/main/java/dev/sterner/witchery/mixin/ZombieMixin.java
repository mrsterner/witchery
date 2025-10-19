package dev.sterner.witchery.mixin;

import dev.sterner.witchery.core.data_attachment.EtherealEntityAttachment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void witchery$preventOwnerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player) {
            var etherealData = EtherealEntityAttachment.getData(this);
            if (etherealData.getOwnerUUID() != null && etherealData.getOwnerUUID().equals(player.getUUID())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player player) {
            var etherealData = EtherealEntityAttachment.getData(this);
            if (etherealData.getOwnerUUID() != null && etherealData.getOwnerUUID().equals(player.getUUID())) {
                return false;
            }
        }
        return super.canAttack(target);
    }
}