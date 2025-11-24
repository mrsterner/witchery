package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.sterner.witchery.content.block.mirror.MirrorBlock;
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity;
import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.mirror.MirrorStuckPlayerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void witchery$removeCollisionBehindMirror(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (level instanceof Level lvl) {

            if (MirrorStuckPlayerAttachment.INSTANCE.hasWallPos(lvl, pos) ||
                    MirrorStuckPlayerAttachment.INSTANCE.hasWallPos(lvl, pos.below())) {
                cir.setReturnValue(Shapes.empty());
            }
        }
    }

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