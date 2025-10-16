package dev.sterner.witchery.mixin.client;


import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @ModifyVariable(
            method = "renderArmWithItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ItemStack witchery$hideItemForAbility(
            ItemStack stack,
            AbstractClientPlayer player,
            float partialTicks,
            float pitch,
            InteractionHand hand,
            float swingProgress
    ) {

        if (hand == InteractionHand.MAIN_HAND && !stack.isEmpty()) {
            if (AfflictionAbilityHandler.INSTANCE.getSelectedAbility(player) != null) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }
}