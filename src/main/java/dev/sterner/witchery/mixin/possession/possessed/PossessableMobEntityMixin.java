package dev.sterner.witchery.mixin.possession.possessed;

import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessedDataAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mob.class)
public abstract class PossessableMobEntityMixin extends PossessableLivingEntityMixin implements Possessable {

    @Shadow public abstract void setAggressive(boolean aggressive);
    @Shadow public abstract boolean isAggressive();
    @Shadow
    protected abstract void serverAiStep();

    @Unique
    private int attackingCountdown;

    public PossessableMobEntityMixin(EntityType<? extends Mob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void witchery$aiStep() {
        this.level().getProfiler().push("mob tick");
        this.serverAiStep();
        this.level().getProfiler().pop();
    }

    @Inject(method = "setAggressive", at = @At("RETURN"))
    private void witchery$setAggressive(boolean attacking, CallbackInfo ci) {
        if (attacking && this.isBeingPossessed()) {
            attackingCountdown = 100;
        }
    }

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void witchery$aiStep(CallbackInfo ci) {
        if (this.isAggressive() && !this.isUsingItem() && this.isBeingPossessed()) {
            this.attackingCountdown--;
            if (this.attackingCountdown == 0) {
                this.setAggressive(false);
            }
        }
    }

    @Inject(method = "getArmorSlots", at = @At("HEAD"), cancellable = true)
    private void witchery$getArmorSlots(CallbackInfoReturnable<Iterable<ItemStack>> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getArmorSlots());
        }
    }

    @Inject(method = "getHandSlots", at = @At("HEAD"), cancellable = true)
    private void witchery$getHandSlots(CallbackInfoReturnable<Iterable<ItemStack>> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getHandSlots());
        }
    }

    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    private void witchery$getItemBySlot(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        Player possessor = this.getPossessor();
        if (possessor != null) {
            cir.setReturnValue(possessor.getItemBySlot(slot));
        }
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void witchery$setItemSlot(EquipmentSlot slot, ItemStack item, CallbackInfo ci) {
        Player possessor = this.getPossessor();
        if (possessor != null && !level().isClientSide) {
            possessor.setItemSlot(slot, item);
            ci.cancel();
        }
    }

    @Inject(method = "canPickUpLoot", at = @At("HEAD"), cancellable = true)
    private void witchery$canPickupLoot(CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "wantsToPickUp", at = @At("HEAD"), cancellable = true)
    private void witchery$wantsToPickUp(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.isBeingPossessed()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "convertTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private <T extends Mob> void witchery$convertTo(EntityType<T> type, boolean transferInventory, CallbackInfoReturnable<T> ci, T converted) {
        Mob self = (Mob) (Object) this;
        PossessedDataAttachment.INSTANCE.onMobConverted(self, converted);
    }
}