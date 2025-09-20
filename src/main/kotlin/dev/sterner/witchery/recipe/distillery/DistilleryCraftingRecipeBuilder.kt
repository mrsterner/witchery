package dev.sterner.witchery.recipe.distillery

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DistilleryCraftingRecipeBuilder(
    private var inputItems: MutableList<ItemStack> = mutableListOf(),
    private var outputStack: MutableList<ItemStack> = mutableListOf(),
    private var altarPower: Int = 0,
    private var cookingTime: Int = 100,
    private var jarConsumption: Int = 1,
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): DistilleryCraftingRecipeBuilder {
            return DistilleryCraftingRecipeBuilder()
        }
    }

    fun addInput(itemStack: ItemStack): DistilleryCraftingRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInput(itemStack: Item): DistilleryCraftingRecipeBuilder {
        inputItems.add(ItemStack(itemStack))
        return this
    }

    fun addOutput(itemStack: ItemStack, count: Int): DistilleryCraftingRecipeBuilder {
        itemStack.count = count
        outputStack += itemStack
        return this
    }

    fun addOutput(item: Item, count: Int): DistilleryCraftingRecipeBuilder {
        val itemStack = ItemStack(item, count)
        outputStack += itemStack
        return this
    }


    fun addOutput(itemStack: ItemStack): DistilleryCraftingRecipeBuilder {
        outputStack += itemStack
        return this
    }

    fun addOutput(item: Item): DistilleryCraftingRecipeBuilder {
        outputStack += item.defaultInstance
        return this
    }

    fun setJarConsumption(count: Int): DistilleryCraftingRecipeBuilder {
        jarConsumption = count
        return this
    }

    fun setAltarPower(power: Int): DistilleryCraftingRecipeBuilder {
        this.altarPower = power
        return this
    }

    fun setCookingTime(cookingTime: Int): DistilleryCraftingRecipeBuilder {
        this.cookingTime = cookingTime
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): DistilleryCraftingRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): DistilleryCraftingRecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item {
        return outputStack[0].item
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
            DistilleryCraftingRecipe(inputItems, outputStack, altarPower, cookingTime, jarConsumption)
        recipeOutput.accept(
            id.withPrefix("distillery_crafting/"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/distillery_crafting/"))
        )
    }
}