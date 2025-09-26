package dev.sterner.witchery.mixin.possession.client;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.api.interfaces.Possessable;
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment;
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment;
import dev.sterner.witchery.registry.WitcheryTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Unique
    private boolean skippedFood;

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderArmorLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean preventArmorRender(Gui instance, GuiGraphics guiGraphics) {
        return Minecraft.getInstance().player == null || !AfflictionPlayerAttachment.getData(Minecraft.getInstance().player).isSoulForm();
    }

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHealthLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean preventHealthRender(Gui instance, GuiGraphics flag) {
        return Minecraft.getInstance().player == null || !AfflictionPlayerAttachment.getData(Minecraft.getInstance().player).isSoulForm();
    }

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFoodLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean preventFoodRender(Gui instance, GuiGraphics j1) {
        var player= Minecraft.getInstance().player;
        if (player != null && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            Possessable possessed = (Possessable) PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (possessed == null || !possessed.isRegularEater()) {
                skippedFood = true;
                return false;
            }
        }

        skippedFood = false;
        return true;
    }

    @ModifyVariable(
            method = "renderAirLevel",
            at = @At(value = "CONSTANT", args = "stringValue=air"),
            index = 18
    )
    private int fixAirRender(int mountHeartCount) {
        if (skippedFood) return 0;
        return mountHeartCount;
    }

    @ModifyArg(
            method = "renderAirLevel",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=air")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")
    )
    private TagKey<Fluid> preventAirRender(TagKey<Fluid> fluid) {
        Player playerEntity = this.getCameraPlayer();

        if (playerEntity != null && AfflictionPlayerAttachment.getData(playerEntity).isVagrant()) {
            LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(playerEntity).getHost();
            if (possessed == null) {
                return WitcheryTags.INSTANCE.getEMPTY_FLUID();  // will cause isSubmergedIn to return false
            } else if (possessed.canBreatheUnderwater()) {
                return WitcheryTags.INSTANCE.getEMPTY_FLUID();   // same as above
            }
        }

        return fluid;
    }

    @ModifyVariable(method = "renderHealthLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"), ordinal = 0)
    private int substituteHealth(int health) {
        Minecraft client = Minecraft.getInstance();
        LivingEntity entity = null;
        if (client.player != null) {
            entity = PossessionComponentAttachment.INSTANCE.get(client.player).getHost();
        }
        if (entity != null) {
            return Mth.ceil(entity.getHealth());
        }
        return health;
    }
}
