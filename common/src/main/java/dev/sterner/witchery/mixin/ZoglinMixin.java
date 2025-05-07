package dev.sterner.witchery.mixin;

import dev.sterner.witchery.platform.EtherealEntityAttachment;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zoglin.class)
public class ZoglinMixin {

    @Inject(method = "setAttackTarget", at = @At("HEAD"), cancellable = true)
    private void witchery$zoglinNoAttack(LivingEntity target, CallbackInfo ci){
        var mob = (Zoglin) (Object) this;
        var uuid = EtherealEntityAttachment.getData(mob).getOwnerUUID();
        if (uuid != null) {
            if (target.getUUID() == uuid) {
                ci.cancel();
            }
        }

        if (target instanceof Player player) {
            boolean bl = VampirePlayerAttachment.getData(player).getVampireLevel() > 0;
            if (bl) {
                ci.cancel();
            }
        }
    }
}
