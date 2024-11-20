package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.witchery.api.SleepingEvent;
import dev.sterner.witchery.mixin_logic.PlayerMixinLogic;
import dev.sterner.witchery.platform.infusion.InfernalInfusionData;
import dev.sterner.witchery.platform.infusion.InfernalInfusionDataAttachment;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import dev.sterner.witchery.registry.WitcheryAttributes;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow private int sleepCounter;

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void witchery$stopSleepInBed(boolean wakeImmediately, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        Player player = Player.class.cast(this);
        SleepingEvent.Companion.getPOST().invoker().invoke(player, this.sleepCounter, wakeImmediately);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder lodestone$CreateLivingAttributes(AttributeSupplier.Builder original) {
        return original
                .add(WitcheryAttributes.INSTANCE.getVAMPIRE_DRINK_SPEED())
                .add(WitcheryAttributes.INSTANCE.getVAMPIRE_BAT_FORM_DURATION())
                .add(WitcheryAttributes.INSTANCE.getVAMPIRE_SUN_RESISTANCE());
    }

    @ModifyReturnValue(method = "wantsToStopRiding", at = @At("RETURN"))
    private boolean witchery$neoStopDismount(boolean original){
        return PlayerMixinLogic.INSTANCE.wantsStopRiding(original);
    }

    @ModifyReturnValue(method = "causeFallDamage", at = @At("RETURN"))
    private boolean witchery$stopCauseFallDamage(boolean original){
        Player player = Player.class.cast(this);
        var data = InfernalInfusionDataAttachment.getData(player).getCurrentCreature();
        if (data == InfernalInfusionData.CreatureType.SLIME || data == InfernalInfusionData.CreatureType.MAGMA_CUBE) {
            return false;
        }

        return original;
    }

    @ModifyReturnValue(method = "hasCorrectToolForDrops", at = @At("RETURN"))
    private boolean witchery$vampSmashStone(boolean original, @Local(argsOnly = true) BlockState state){
        Player player = Player.class.cast(this);
        if (VampirePlayerAttachment.getData(player).getVampireLevel() >= 6) {
            if (state.is(WitcheryTags.INSTANCE.getSMASH_STONE())) {
                return true;
            }
        }
        return original;
    }
}
