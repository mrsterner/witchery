package dev.sterner.witchery.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.ShapedRecipeAccessor;
import dev.sterner.witchery.content.item.ChalkItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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