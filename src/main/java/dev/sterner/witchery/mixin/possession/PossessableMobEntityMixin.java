package dev.sterner.witchery.mixin.possession;


import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessionManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mob.class)
public abstract class PossessableMobEntityMixin extends PossessableLivingEntityMixin implements Possessable {

    @Shadow
    public abstract void setAggressive(boolean aggressive);

    @Shadow
    public abstract boolean isAggressive();

    @Shadow
    protected abstract void customServerAiStep();

    @Unique
    private int attackingCountdown;

    public PossessableMobEntityMixin(EntityType<? extends Mob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void requiem$mobTick() {
        this.level().getProfiler().push("mob tick");
        this.customServerAiStep();
        this.level().getProfiler().pop();
    }

    @Inject(method = "setAggressive", at = @At("RETURN"))
    private void resetAttackMode(boolean attacking, CallbackInfo ci) {
        if (attacking && this.isBeingPossessed()) {
            attackingCountdown = 100;
        }
    }

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void resetAttackMode(CallbackInfo ci) {
        if (this.isAggressive() && !this.isUsingItem() && this.isBeingPossessed()) {
            this.attackingCountdown--;
            if (this.attackingCountdown == 0) {
                this.setAggressive(false);
            }
        }
    }

    @Inject(method = "getArmorSlots", at = @At("HEAD"), cancellable = true)
    private void getArmorItems(CallbackInfoReturnable<Iterable<ItemStack>> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getArmorSlots());
        }
    }

    @Inject(method = "getHandSlots", at = @At("HEAD"), cancellable = true)
    private void getItemsHand(CallbackInfoReturnable<Iterable<ItemStack>> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getHandSlots());
        }
    }

    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    private void getEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getItemBySlot(slot));
        }
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void setEquippedStack(EquipmentSlot slot, ItemStack item, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor != null && !level().isClientSide) {
            possessor.setItemSlot(slot, item);
            ci.cancel();
        }
    }

    @Inject(method = "canPickUpLoot", at = @At("HEAD"), cancellable = true)
    private void cannotPickupItem(CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "wantsToPickUp", at = @At("HEAD"), cancellable = true)
    private void cannotGather(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "convertTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private <T extends Mob> void possessConvertedMob(EntityType<T> type, boolean bl, CallbackInfoReturnable<T> cir, T converted) {
        Mob original = (Mob) (Object) this;
        Player possessor = this.getPossessor();

        if (possessor != null) {
            PossessionManager.INSTANCE.stopPossessing(possessor, false);

            converted.setPersistenceRequired();
            PossessionManager.INSTANCE.startPossessing(possessor, converted, false);
        }
    }
}