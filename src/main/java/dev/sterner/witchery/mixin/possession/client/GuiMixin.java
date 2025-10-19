package dev.sterner.witchery.mixin.possession.client;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.sterner.witchery.core.api.interfaces.Possessable;
import dev.sterner.witchery.core.registry.WitcheryTags;
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment;
import dev.sterner.witchery.features.possession.PossessionComponentAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Unique
    private boolean witchery$skippedFood;

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderArmorLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean witchery$renderPlayerHealth(Gui instance, GuiGraphics guiGraphics) {
        return Minecraft.getInstance().player == null || !AfflictionPlayerAttachment.getData(Minecraft.getInstance().player).isSoulForm();
    }

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHealthLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean witchery$renderPlayerHealth2(Gui instance, GuiGraphics flag) {
        return Minecraft.getInstance().player == null || !AfflictionPlayerAttachment.getData(Minecraft.getInstance().player).isSoulForm();
    }

    @WrapWithCondition(
            method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFoodLevel(Lnet/minecraft/client/gui/GuiGraphics;)V")
    )
    private boolean witchery$renderPlayerHealth3(Gui instance, GuiGraphics j1) {
        var player= Minecraft.getInstance().player;
        if (player != null && AfflictionPlayerAttachment.getData(player).isVagrant()) {
            Possessable possessed = (Possessable) PossessionComponentAttachment.INSTANCE.get(player).getHost();
            if (possessed == null || !possessed.isRegularEater()) {
                witchery$skippedFood = true;
                return false;
            }
        }

        witchery$skippedFood = false;
        return true;
    }

    @ModifyVariable(
            method = "renderAirLevel",
            at = @At(value = "CONSTANT", args = "stringValue=air"),
            index = 3
    )
    private int witchery$renderAirLevel(int mountHeartCount) {
        if (witchery$skippedFood) return 0;
        return mountHeartCount;
    }

    @ModifyArg(
            method = "renderAirLevel",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=air")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")
    )
    private TagKey<Fluid> witchery$renderAirLevel(TagKey<Fluid> fluid) {
        Player playerEntity = this.getCameraPlayer();

        if (playerEntity != null && AfflictionPlayerAttachment.getData(playerEntity).isVagrant()) {
            LivingEntity possessed = PossessionComponentAttachment.INSTANCE.get(playerEntity).getHost();
            if (possessed == null) {
                return WitcheryTags.INSTANCE.getEMPTY_FLUID();
            } else if (possessed.canBreatheUnderwater()) {
                return WitcheryTags.INSTANCE.getEMPTY_FLUID();
            }
        }

        return fluid;
    }

    @ModifyVariable(method = "renderHealthLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"), ordinal = 0)
    private int witchery$renderHealthLevel(int health) {
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
