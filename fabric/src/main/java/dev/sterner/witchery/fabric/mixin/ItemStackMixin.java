package dev.sterner.witchery.fabric.mixin;

import dev.sterner.witchery.handler.poppet.PoppetHandler;
import dev.sterner.witchery.handler.poppet.PoppetType;
import dev.sterner.witchery.mixin_logic.ItemStackMixinLogic;
import dev.sterner.witchery.registry.WitcheryPoppetRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
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

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"),
            cancellable = true)
    private void witchery$armorPortectionPoppet(int damage, ServerLevel level, @Nullable ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
        var bl = ItemStackMixinLogic.INSTANCE.armorProtection(player);
        if (bl) {
            setDamageValue(0);
            ci.cancel();
        }
    }
}
