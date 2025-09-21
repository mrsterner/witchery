package dev.sterner.witchery.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShapelessRecipeBuilder.class)
public interface ShapelessRecipeAccessor {
    @Accessor("criteria")
    Map<String, Criterion<?>> getCriteria();
}
