package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;doPlayerHarvestCheck(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean witchery$getDestroyProgress(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player, @Local(argsOnly = true) BlockGetter blockView, @Local(argsOnly = true) BlockPos blockPos) {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 6) {
            if (state.is(WitcheryTags.INSTANCE.getSMASH_STONE())) {
                return true;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float witchery$getDestroyProgress3(float original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player) {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 6) {
            if (state.is(WitcheryTags.INSTANCE.getSMASH_STONE())) {
                return original / 2;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
    private float witchery$getDestroyProgress2(float original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player) {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 6) {
            if (state.is(WitcheryTags.INSTANCE.getSMASH_STONE())) {
                return original + 0.1f;
            }
        }
        return original;
    }
}