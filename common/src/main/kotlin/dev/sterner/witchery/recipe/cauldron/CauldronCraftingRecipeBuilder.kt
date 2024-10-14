package dev.sterner.witchery.recipe.cauldron

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class CauldronCraftingRecipeBuilder(
    private var inputItems: MutableList<ItemStackWithColor> = mutableListOf(),
    private var outputStack: MutableList<Ingredient> = mutableListOf(),
    private val altarPower: Int = 0
) : RecipeBuilder {

    var order = 0

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): CauldronCraftingRecipeBuilder {
            return CauldronCraftingRecipeBuilder()
        }
    }

    fun addInputWithColor(itemStack: ItemStack, color: Int): CauldronCraftingRecipeBuilder {
        inputItems.add(ItemStackWithColor(itemStack, color, order))
        order++
        return this
    }

    fun addInput(itemStackWithColor: ItemStackWithColor): CauldronCraftingRecipeBuilder {
        inputItems += itemStackWithColor
        return this
    }

    fun addOutput(ingredient: Ingredient): CauldronCraftingRecipeBuilder {
        outputStack += ingredient
        return this
    }

    fun addOutput(item: Item): CauldronCraftingRecipeBuilder {
        outputStack += Ingredient.of(item.defaultInstance)
        return this
    }

    fun setAltarPower(power: Int): CauldronCraftingRecipeBuilder {
        return CauldronCraftingRecipeBuilder(inputItems, outputStack, power)
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): CauldronCraftingRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): CauldronCraftingRecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item {
        return outputStack[0].items[0].item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach { (name, criterion) ->
            builder.addCriterion(name, criterion)
        }

        val cauldronCraftingRecipe = CauldronCraftingRecipe(inputItems, outputStack, altarPower)
        recipeOutput.accept(
            id.withPrefix("cauldron_crafting/").withSuffix("_from_${inputItems[0].itemStack.item.`arch$registryName`()!!.path}"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/cauldron_crafting/"))
        )
    }
}