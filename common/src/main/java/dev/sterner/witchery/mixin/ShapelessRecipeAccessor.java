package dev.sterner.witchery.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShapelessRecipeBuilder.class)
public interface ShapelessRecipeAccessor {

    @Accessor("category")
    RecipeCategory getCategory();

    @Accessor("result")
    Item getResult();

    @Accessor("count")
    int getCount();

    @Accessor("criteria")
    Map<String, Criterion<?>> getCriteria();

    @Accessor("group")
    String getGroup();
}