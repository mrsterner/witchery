package dev.sterner.witchery.mixin;

import dev.sterner.witchery.data_attachment.EtherealEntityAttachment;
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    @Shadow
    protected TargetingConditions targetConditions;

    public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "findTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;DDD)Lnet/minecraft/world/entity/player/Player;"), cancellable = true)
    private void witchery$dontAttackSummonerOwner2(CallbackInfo ci) {

        var player = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        if (player != null) {
            if (mob.getType().is(WitcheryTags.INSTANCE.getNECROMANCER_SUMMONABLE())) {
                var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
                if (uuid != null) {
                    if (player.getUUID().equals(uuid)) {
                        ci.cancel();
                    }
                }
            }
            if (mob.getType().is(EntityTypeTags.UNDEAD)) {
                boolean bl = AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0;
                if (bl) {
                    ci.cancel();
                }
            }
        }
    }
}
