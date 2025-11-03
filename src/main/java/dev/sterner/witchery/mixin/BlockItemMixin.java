package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.content.item.ChalkItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"
            )
    )
    private void witchery$preventChalkConsumption(ItemStack stack, int amount, LivingEntity entity, Operation<Void> original) {
        if (stack.getItem() instanceof ChalkItem) {
            stack.hurtAndBreak(1, entity, entity.getEquipmentSlotForItem(stack));
        } else {
            original.call(stack, amount, entity);
        }
    }
}