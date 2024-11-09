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

class CauldronCraftingRecipeBuilder(
    private var inputItems: MutableList<ItemStackWithColor> = mutableListOf(),
    private var outputStack: MutableList<ItemStack> = mutableListOf(),
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

    fun addInputWithColor(item: Item, color: Int): CauldronCraftingRecipeBuilder {
        inputItems.add(ItemStackWithColor(ItemStack(item), color, order))
        order++
        return this
    }

    fun addInput(itemStackWithColor: ItemStackWithColor): CauldronCraftingRecipeBuilder {
        inputItems += itemStackWithColor
        return this
    }

    fun addOutput(itemStack: ItemStack, count: Int): CauldronCraftingRecipeBuilder {
        itemStack.count = count
        outputStack += itemStack
        return this
    }

    fun addOutput(item: Item, count: Int): CauldronCraftingRecipeBuilder {
        val itemStack = ItemStack(item, count)
        outputStack += itemStack
        return this
    }

    fun addOutput(itemStack: ItemStack): CauldronCraftingRecipeBuilder {
        outputStack += itemStack
        return this
    }

    fun addOutput(item: Item): CauldronCraftingRecipeBuilder {
        outputStack += item.defaultInstance
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

        val cauldronCraftingRecipe = CauldronCraftingRecipe(inputItems, outputStack, altarPower)
        recipeOutput.accept(
            id.withPrefix("cauldron_crafting/"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/cauldron_crafting/"))
        )
    }
}