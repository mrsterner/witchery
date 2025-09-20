package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.block.SuspiciousGraveyardDirtBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public class BrushItemMixin {

    @Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;)V"))
    private void witchery$onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration, CallbackInfo ci, @Local Player player, @Local BlockPos blockPos, @Local BlockHitResult blockHitResult) {
        if (!level.isClientSide() && level.getBlockEntity(blockPos) instanceof SuspiciousGraveyardDirtBlockEntity brushableBlockEntity) {
            boolean bl2 = brushableBlockEntity.brush(level.getGameTime(), player, blockHitResult.getDirection());
            if (bl2) {
                EquipmentSlot equipmentSlot = stack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.hurtAndBreak(1, livingEntity, equipmentSlot);
            }
        }
    }
}
