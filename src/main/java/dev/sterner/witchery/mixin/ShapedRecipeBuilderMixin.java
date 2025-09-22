package dev.sterner.witchery.mixin;

import dev.sterner.witchery.ShapedRecipeAccessor;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public class ShapedRecipeBuilderMixin implements ShapedRecipeAccessor {

    @Shadow
    private Map<String, Criterion<?>> criteria;

    @Shadow
    private Map<Character, Ingredient> key;

    @Shadow
    private List<String> rows;

    @Shadow
    private boolean showNotification;

    @Override
    public Map<String, Criterion<?>> getCriteria() {
        return this.criteria;
    }

    @Override
    public Map<Character, Ingredient> getKey() {
        return this.key;
    }

    @Override
    public List<String> getRows() {
        return this.rows;
    }

    @Override
    public boolean getShowNotification() {
        return this.showNotification;
    }
}