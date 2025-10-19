package dev.sterner.witchery.mixin;

import dev.sterner.witchery.core.data_attachment.EtherealEntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Monster {

    protected AbstractSkeletonMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "reassessWeaponGoal", at = @At("HEAD"))
    private void witchery$modifyTargeting(CallbackInfo ci) {
        if (this.getTarget() instanceof Player player) {
            var etherealData = EtherealEntityAttachment.getData(this);
            if (etherealData.getOwnerUUID() != null && etherealData.getOwnerUUID().equals(player.getUUID())) {
                this.setTarget(null);
            }
        }
    }
}