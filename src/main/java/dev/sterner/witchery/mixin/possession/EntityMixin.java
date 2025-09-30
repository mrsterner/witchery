package dev.sterner.witchery.mixin.possession;

import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    public float fallDistance;

    @Inject(method = "checkFallDamage", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/Entity;fallDistance:F", ordinal = 0))
    private void preventFallEffects(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (self instanceof Player player && AfflictionPlayerAttachment.getData(player).isSoulForm()) {
            this.fallDistance = 0;
        }
    }
}