package dev.sterner.witchery.mixin;

import dev.sterner.witchery.api.EntityChainInterface;
import dev.sterner.witchery.entity.ChainEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
        if (!witchery$restrainingChains.contains(chainEntity)) {
            witchery$restrainingChains.add(chainEntity);
        }

        witchery$restrained = true;
    }

    @Override
    public boolean witchery$isRestrained() {
        witchery$restrainingChains.removeIf(Entity::isRemoved);

        witchery$restrained = !witchery$restrainingChains.isEmpty();
        return witchery$restrained;
    }

    @Override
    public @NotNull List<ChainEntity> witchery$getRestrainingChains() {
        witchery$restrainingChains.removeIf(Entity::isRemoved);

        return new ArrayList<>(witchery$restrainingChains);
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(CallbackInfo callbackInfo) {
        if (witchery$isRestrained()) {
            LivingEntity self = (LivingEntity) (Object) this;
            self.walkAnimation.setSpeed(0);
            self.walkAnimation.update(0, 0.0F);
            callbackInfo.cancel();
        }
    }
}