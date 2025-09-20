package dev.sterner.witchery.recipe.spinning_wheel

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class SpinningWheelRecipeBuilder(
    private var inputItems: MutableList<ItemStack> = mutableListOf(),
    private var outputStack: ItemStack = ItemStack.EMPTY,
    private var altarPower: Int = 0,
    private var cookingTime: Int = 100,
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): SpinningWheelRecipeBuilder {
            return SpinningWheelRecipeBuilder()
        }
    }

    fun addInput(itemStack: ItemStack): SpinningWheelRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInput(item: Item): SpinningWheelRecipeBuilder {
        inputItems.add(ItemStack(item))
        return this
    }

    fun addOutput(itemStack: ItemStack, count: Int): SpinningWheelRecipeBuilder {
        itemStack.count = count
        outputStack = itemStack
        return this
    }

    fun addOutput(item: Item, count: Int): SpinningWheelRecipeBuilder {
        outputStack = ItemStack(item, count)
        return this
    }

    fun addOutput(itemStack: ItemStack): SpinningWheelRecipeBuilder {
        outputStack = itemStack
        return this
    }

    fun addOutput(item: Item): SpinningWheelRecipeBuilder {
        outputStack = ItemStack(item)
        return this
    }

    fun setAltarPower(power: Int): SpinningWheelRecipeBuilder {
        this.altarPower = power
        return this
    }

    fun setCookingTime(cookingTime: Int): SpinningWheelRecipeBuilder {
        this.cookingTime = cookingTime
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): SpinningWheelRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): SpinningWheelRecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item {
        return outputStack.item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach { (name, criterion) ->
            builder.addCriterion(name, criterion)
        }

        val cauldronCraftingRecipe =
            SpinningWheelRecipe(inputItems, outputStack, altarPower, cookingTime)
        recipeOutput.accept(
            id.withPrefix("spinning_wheel/"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/spinning_wheel/"))
        )
    }
}