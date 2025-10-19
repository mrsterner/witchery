package dev.sterner.witchery.mixin;

import dev.sterner.witchery.core.registry.WitcheryFlammability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.neoforged.neoforge.common.extensions.IBlockStateExtension")
public interface IBlockStateExtensionMixin {

    @Inject(method = "getFlammability(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I",
            at = @At("HEAD"),
            cancellable = true)
    default void getFlammability(BlockGetter level, BlockPos pos, Direction face, CallbackInfoReturnable<Integer> cir) {
        BlockState state = (BlockState) (Object) this;
        Block block = state.getBlock();

        WitcheryFlammability.INSTANCE.getFlammableBlocks().stream()
                .filter(flame -> flame.getBlock().get() == block)
                .findFirst()
                .ifPresent(flame -> {
                    cir.setReturnValue(flame.getBurnOdds());
                    cir.cancel();
                });
    }

    @Inject(method = "getFireSpreadSpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I",
            at = @At("HEAD"),
            cancellable = true)
    default void getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face, CallbackInfoReturnable<Integer> cir) {
        BlockState state = (BlockState) (Object) this;
        Block block = state.getBlock();

        WitcheryFlammability.INSTANCE.getFlammableBlocks().stream()
                .filter(flame -> flame.getBlock().get() == block)
                .findFirst()
                .ifPresent(flame -> {
                    cir.setReturnValue(flame.getIgniteOdds());
                    cir.cancel();
                });
    }
}

