package dev.sterner.witchery.mixin;

import dev.sterner.witchery.api.EntityChainInterface;
import dev.sterner.witchery.entity.ChainEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class EntityChainMixin implements EntityChainInterface {

    @Unique
    private final List<ChainEntity> witchery$restrainingChains = new ArrayList<>();
    @Unique
    private boolean witchery$restrained = false;

    @Override
    public void witchery$restrainMovement(@NotNull ChainEntity chainEntity) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!witchery$restrainingChains.contains(chainEntity)) {
            witchery$restrainingChains.add(chainEntity);
        }

        witchery$restrained = true;

        // Get the position we're chained to
        Vec3 anchorPos = chainEntity.getLockedPosition() != null ?
                chainEntity.getLockedPosition() : chainEntity.position();

        // Calculate the direction and distance
        Vec3 currentPos = self.position();
        Vec3 direction = currentPos.subtract(anchorPos).normalize();
        double distance = currentPos.distanceTo(anchorPos);

        // Maximum allowed distance
        double maxDistance = 10.0; // Adjust as needed

        // If we're beyond the max distance, pull back
        if (distance > maxDistance) {
            double pullStrength = 0.3; // Adjust for desired strength
            Vec3 pullVector = direction.scale(-pullStrength);

            // Apply velocity change to pull entity back
            self.setDeltaMovement(self.getDeltaMovement().add(pullVector));

            // Optionally add a visual effect to show the pull
            if (self.level().isClientSide()) {
                // Add particle effects or other visual feedback
                self.level().addParticle(
                        ParticleTypes.CRIT,
                        self.getX(), self.getY() + 1.0, self.getZ(),
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    public boolean witchery$isRestrained() {
        // Clean up any discarded chains
        witchery$restrainingChains.removeIf(Entity::isRemoved);

        witchery$restrained = !witchery$restrainingChains.isEmpty();
        return witchery$restrained;
    }

    @Override
    public @NotNull List<ChainEntity> witchery$getRestrainingChains() {
        // Clean up any discarded chains
        witchery$restrainingChains.removeIf(Entity::isRemoved);

        return new ArrayList<>(witchery$restrainingChains);
    }

    // Intercepting the travel method to restrict movement
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(CallbackInfo callbackInfo) {
        if (witchery$isRestrained()) {
            LivingEntity self = (LivingEntity) (Object) this;

            // We can optionally reduce movement speed while chained
            if (self.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                self.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.02); // Severely reduced movement
            }

            callbackInfo.cancel();
        }
    }
}