package dev.sterner.witchery.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.mixin_logic.GuiMixinLogic;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {


    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean witchery$renderBlood(Gui instance, GuiGraphics guiGraphics, Player player, int y, int x){
        return GuiMixinLogic.INSTANCE.renderFoodLevel(player, guiGraphics, y, x);
    }
}
