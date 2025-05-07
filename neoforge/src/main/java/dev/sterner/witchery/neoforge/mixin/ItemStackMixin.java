package dev.sterner.witchery.neoforge.mixin;

import dev.sterner.witchery.handler.PoppetHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract void setDamageValue(int damage);

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), cancellable = true)
    private void witchery$armorPortectionPoppet(int j, ServerLevel arg, LivingEntity arg2, Consumer<Item> consumer, CallbackInfo ci) {
        if (arg2 instanceof ServerPlayer player && PoppetHandler.INSTANCE.hasArmorProtectionPoppet(arg, player)) {
            setDamageValue(0);
            ci.cancel();
        }
    }
}
