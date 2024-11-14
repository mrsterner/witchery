package dev.sterner.witchery.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.witchery.entity.BroomEntity;
import dev.sterner.witchery.mixin_logic.PlayerMixinLogic;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @ModifyReturnValue(method = "wantsToStopRiding", at = @At("RETURN"))
    private boolean witchery$neoStopDismount(boolean original){
        return PlayerMixinLogic.INSTANCE.wantsStopRiding(original);
    }
}
