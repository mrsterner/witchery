package dev.sterner.witchery.neoforge.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.api.RenderUtils;
import dev.sterner.witchery.mixin_logic.GuiMixinLogic;
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @WrapWithCondition(method = "renderFoodLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean witchery$renderBlood(Gui instance, GuiGraphics guiGraphics, Player player, int y, int x){
        return GuiMixinLogic.INSTANCE.renderFoodLevel(player, guiGraphics, y, x);
    }
}
