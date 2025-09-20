package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data_attachment.EtherealEntityAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.handler.affliction.AfflictionTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Zoglin.class)
public class ZoglinMixin {

    @Inject(method = "setAttackTarget", at = @At("HEAD"), cancellable = true)
    private void witchery$zoglinNoAttack(LivingEntity target, CallbackInfo ci) {
        var mob = (Zoglin) (Object) this;
        var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
        if (uuid != null) {
            if (target.getUUID() == uuid) {
                ci.cancel();
            }
        }

        if (target instanceof Player player) {
            boolean bl = AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0;
            if (bl) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "findNearestValidAttackTarget", at = @At("HEAD"), cancellable = true)
    private void witchery$filterTargets(CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        var zoglin = (Zoglin) (Object) this;
        var etherealData = EtherealEntityAttachment.getData(zoglin);
        if (etherealData.getOwnerUUID() != null) {
            Optional<LivingEntity> target = zoglin.getBrain()
                    .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                    .flatMap(nearbyEntities -> nearbyEntities.findClosest(entity -> {
                        if (entity instanceof Player player) {

                            if (etherealData.getOwnerUUID().equals(player.getUUID())) {
                                return false;
                            }

                            var owner = zoglin.level().getPlayerByUUID(etherealData.getOwnerUUID());
                            if (owner != null && AfflictionPlayerAttachment.getData(owner).getLevel(AfflictionTypes.LICHDOM) > 0) {
                                if (AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM) > 0) {
                                    return false;
                                }
                            }
                        }
                        return Sensor.isEntityAttackable(zoglin, entity);
                    }));
            cir.setReturnValue(target);
        }
    }
}
