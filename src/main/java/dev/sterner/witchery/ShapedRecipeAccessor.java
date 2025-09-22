package dev.sterner.witchery;

import net.minecraft.advancements.Criterion;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public interface ShapedRecipeAccessor {
    Map<String, Criterion<?>> getCriteria();
    Map<Character, Ingredient> getKey();
    List<String> getRows();
    boolean getShowNotification();
}
