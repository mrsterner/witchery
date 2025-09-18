package dev.sterner.witchery.mixin;

import dev.sterner.witchery.handler.affliction.AfflictionTypes;
import dev.sterner.witchery.handler.affliction.LichdomSoulPoolHandler;
import dev.sterner.witchery.mixin_logic.FoodDataMixinLogic;
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment;
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

    @Unique
    Player witchery$player;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void witchery$tick(Player player, CallbackInfo ci) {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0) {
            this.witchery$player = player;
            ci.cancel();
        }
        int lichLevel = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM);

        if (lichLevel >= 2) {
            LichdomSoulPoolHandler.INSTANCE.updateLichHunger(player);
            ci.cancel();
        }
    }

    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    public void witchery$getFoodLevel(CallbackInfoReturnable<Integer> cir) {
        if (witchery$player != null) {
            FoodDataMixinLogic.INSTANCE.getFood(witchery$player, cir);
        }
    }

    @Inject(method = "getSaturationLevel", at = @At("HEAD"), cancellable = true)
    public void witchery$getSaturationLevel(CallbackInfoReturnable<Float> cir) {
        if (witchery$player != null) {
            FoodDataMixinLogic.INSTANCE.getSaturation(witchery$player, cir);
        }
    }

    @Inject(method = "add", at = @At("TAIL"))
    public void witchery$add(int foodLevel, float saturationLevel, CallbackInfo ci) {
        if (witchery$player != null) {
            FoodDataMixinLogic.INSTANCE.onAdd(witchery$player, foodLevel, saturationLevel);
        }
    }
}
