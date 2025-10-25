package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.features.death.DeathPlayerAttachment;
import dev.sterner.witchery.features.death.DeathTransformationHelper;
import dev.sterner.witchery.features.misc.MiscPlayerAttachment;
import dev.sterner.witchery.features.poppet.VoodooPoppetLivingEntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "updateFluidHeightAndDoFluidPushing*", at = @At("HEAD"), cancellable = true)
    private void witchery$deathBootsFluidWalking(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            if (DeathTransformationHelper.INSTANCE.hasDeathBoots(player)) {
                var data = DeathPlayerAttachment.getData(player);
                if (data.getHasDeathFluidWalking() && !player.isShiftKeyDown()) {
                    AABB aabb = entity.getBoundingBox().deflate(0.001);
                    int minX = Mth.floor(aabb.minX);
                    int maxX = Mth.ceil(aabb.maxX);
                    int minY = Mth.floor(aabb.minY);
                    int maxY = Mth.ceil(aabb.maxY);
                    int minZ = Mth.floor(aabb.minZ);
                    int maxZ = Mth.ceil(aabb.maxZ);

                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    for (int x = minX; x < maxX; x++) {
                        for (int y = minY; y < maxY; y++) {
                            for (int z = minZ; z < maxZ; z++) {
                                mutablePos.set(x, y, z);
                                FluidState fluidState = entity.level().getFluidState(mutablePos);
                                if (!fluidState.isEmpty() && fluidState.getAmount() >= 8) {
                                    double fluidHeight = (double) ((float) y + fluidState.getHeight(entity.level(), mutablePos));
                                    if (fluidHeight >= aabb.minY) {
                                        if (entity.getDeltaMovement().y < 0) {
                                            entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);
                                        }
                                        entity.setOnGround(true);
                                        cir.setReturnValue(false);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "isInWaterRainOrBubble", at = @At("RETURN"), cancellable = true)
    private void witchery$isInWaterRainOrBubble(CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue()) {
            Entity entity = Entity.class.cast(this);
            if (entity instanceof LivingEntity living) {
                VoodooPoppetLivingEntityAttachment.Data data = VoodooPoppetLivingEntityAttachment.getPoppetData(living);
                if (data.isUnderWater()) {
                    info.setReturnValue(true);
                }
            }
        }
    }

    @ModifyReturnValue(method = "isEyeInFluid", at = @At("RETURN"))
    private boolean witchery$isEyeInFluid(boolean original) {
        Entity entity = Entity.class.cast(this);
        if (entity instanceof LivingEntity living) {
            if (VoodooPoppetLivingEntityAttachment.getPoppetData(living).isUnderWater()) {
                return true;
            }
        }

        return original;
    }
}
