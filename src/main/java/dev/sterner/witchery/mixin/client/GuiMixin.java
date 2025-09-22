package dev.sterner.witchery.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler;
import dev.sterner.witchery.handler.affliction.VampireClientSpecificEventHandler;
import dev.sterner.witchery.mixin_logic.GuiMixinLogic;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @WrapWithCondition(method = "renderItemHotbar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private boolean onRenderHotbar(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height) {
        int index = AfflictionAbilityHandler.INSTANCE.getAbilityIndex();

        return index == -1;
    }

    @WrapWithCondition(method = "renderFoodLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean witchery$renderBlood(Gui instance, GuiGraphics guiGraphics, Player player, int y, int x) {
        return GuiMixinLogic.INSTANCE.renderFoodLevel(player, guiGraphics, y, x);
    }

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "renderChat",
            at = @At("RETURN")
    )
    private void witchery$renderSunAfterChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        VampireClientSpecificEventHandler.renderSunOverlay(guiGraphics, minecraft);
    }
}
