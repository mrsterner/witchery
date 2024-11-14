package dev.sterner.witchery.mixin;

import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @Unique Player witchery$player;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void witchery$tick(Player player, CallbackInfo ci) {
        if (VampirePlayerAttachment.getData(player).getVampireLevel() > 0) {
            this.witchery$player = player;
            ci.cancel();
        }
    }

    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    public void witchery$getFoodLevel(CallbackInfoReturnable<Integer> cir) {
        if (VampirePlayerAttachment.getData(this.witchery$player).getVampireLevel() > 0) {
            var bloodData = BloodPoolLivingEntityAttachment.getData(this.witchery$player);
            int maxBlood = bloodData.getMaxBlood();
            int blood = bloodData.getBloodPool();
            int scaled = (blood / maxBlood) * 20;
            cir.setReturnValue(scaled);
        }
    }

    @Inject(method = "getSaturationLevel", at = @At("HEAD"), cancellable = true)
    public void witchery$getSaturationLevel(CallbackInfoReturnable<Integer> cir) {
        if (VampirePlayerAttachment.getData(this.witchery$player).getVampireLevel() > 0) {
            cir.setReturnValue(0);
        }
    }
}
