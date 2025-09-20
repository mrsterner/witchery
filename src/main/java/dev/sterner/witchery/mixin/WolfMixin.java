package dev.sterner.witchery.mixin;

import dev.sterner.witchery.mixin_logic.SummonedWolf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public class WolfMixin implements SummonedWolf {

    @Unique
    private int witchery$summonedTime = -1;

    @Unique
    private boolean witchery$isSummoned = false;

    @Unique
    private int witchery$summonDuration = 20 * 30;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        if (witchery$isSummoned && !wolf.level().isClientSide) {
            witchery$summonedTime++;

            if (witchery$summonedTime >= witchery$summonDuration) {
                if (wolf.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            wolf.getX(),
                            wolf.getY() + 0.5,
                            wolf.getZ(),
                            20,
                            0.3,
                            0.3,
                            0.3,
                            0.05
                    );
                }

                wolf.discard();
                return;
            }

            if (witchery$summonedTime >= witchery$summonDuration - 100 &&
                    witchery$summonedTime % 20 == 0) {

                if (wolf.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.PORTAL,
                            wolf.getX(),
                            wolf.getY() + 0.5,
                            wolf.getZ(),
                            5,
                            0.2,
                            0.2,
                            0.2,
                            0.0
                    );
                }
            }
        }
    }

    @Override
    public void witchery$setSummoned(boolean summoned) {
        this.witchery$isSummoned = summoned;
        if (summoned) {
            this.witchery$summonedTime = 0;
        }
    }

    @Override
    public boolean witchery$isSummoned() {
        return this.witchery$isSummoned;
    }

    @Override
    public void witchery$setSummonDuration(int ticks) {
        this.witchery$summonDuration = ticks;
    }

    @Override
    public int witchery$getRemainingTime() {
        if (witchery$isSummoned) {
            return Math.max(0, witchery$summonDuration - witchery$summonedTime);
        }
        return 0;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void witchery$saveData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("WitcheryIsSummoned", this.witchery$isSummoned);
        tag.putInt("WitcherySummonedTime", this.witchery$summonedTime);
        tag.putInt("WitcherySummonDuration", this.witchery$summonDuration);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void witchery$loadData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("WitcheryIsSummoned")) {
            this.witchery$isSummoned = tag.getBoolean("WitcheryIsSummoned");
        }
        if (tag.contains("WitcherySummonedTime")) {
            this.witchery$summonedTime = tag.getInt("WitcherySummonedTime");
        }
        if (tag.contains("WitcherySummonDuration")) {
            this.witchery$summonDuration = tag.getInt("WitcherySummonDuration");
        }
    }
}
