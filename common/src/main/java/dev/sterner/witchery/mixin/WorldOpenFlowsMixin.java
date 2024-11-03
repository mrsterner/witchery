package dev.sterner.witchery.mixin;


import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldOpenFlows.class)
public class WorldOpenFlowsMixin {

    @ModifyVariable(method = "confirmWorldCreation", at = @At("HEAD"), argsOnly = true)
    private static Lifecycle witchery$acceptNewDim(Lifecycle cycle) {
        return Lifecycle.stable();
    }

    @ModifyVariable(
            method = "openWorldCheckWorldStemCompatibility",
            at = @At("STORE"),
            ordinal = 1
    )
    public boolean witchery$dontCheckStability(boolean a) {
        return false;
    }
}