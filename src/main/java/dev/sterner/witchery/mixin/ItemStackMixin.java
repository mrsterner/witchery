package dev.sterner.witchery.mixin;

import dev.sterner.witchery.mixin_logic.ItemStackMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void preventArmorBreak(int amount, ServerLevel level, LivingEntity entity, Consumer<Item> breakCallback, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;

        ItemStack self = (ItemStack) (Object) this;
        int maxDamage = self.getMaxDamage();
        int newDamage = self.getDamageValue() + amount;

        if (newDamage >= maxDamage) {
            if (ItemStackMixinLogic.INSTANCE.armorProtection(player)) {
                self.setDamageValue(0);

                ci.cancel();
            }
        }
    }
}
