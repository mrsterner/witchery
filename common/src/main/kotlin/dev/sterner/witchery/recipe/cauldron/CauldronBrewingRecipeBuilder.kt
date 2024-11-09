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

class CauldronBrewingRecipeBuilder(
    private val inputItems: MutableList<ItemStackWithColor> = mutableListOf(),
    private var outputStack: ItemStack = ItemStack.EMPTY,
    private var altarPower: Int = 0,
    private var dimensionKey: Set<String> = setOf("")
) : RecipeBuilder {

    var order = 0

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): CauldronBrewingRecipeBuilder {
            return CauldronBrewingRecipeBuilder()
        }
    }

    fun addInput(itemStackWithColor: ItemStackWithColor): CauldronBrewingRecipeBuilder {
        inputItems.add(itemStackWithColor)
        return this
    }

    fun addInputWithColor(itemStack: ItemStack, color: Int): CauldronBrewingRecipeBuilder {
        inputItems.add(ItemStackWithColor(itemStack, color, order))
        order++
        return this
    }

    fun addInputWithColor(item: Item, color: Int): CauldronBrewingRecipeBuilder {
        inputItems.add(ItemStackWithColor(ItemStack(item), color, order))
        order++
        return this
    }

    fun setOutput(outputStack: ItemStack): CauldronBrewingRecipeBuilder {
        this.outputStack = outputStack
        return this
    }

    fun setOutput(outputStack: Item): CauldronBrewingRecipeBuilder {
        this.outputStack = ItemStack(outputStack)
        return this
    }

    fun setAltarPower(power: Int): CauldronBrewingRecipeBuilder {
        altarPower = power
        return this
    }

    fun setDimensionKey(dimensionKey: String): CauldronBrewingRecipeBuilder {
        this.dimensionKey = setOf(dimensionKey)
        return this
    }

    fun setDimensionKey(dimensionKey: Set<String>): CauldronBrewingRecipeBuilder {
        this.dimensionKey = dimensionKey
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): RecipeBuilder {
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

        val cauldronBrewingRecipe = CauldronBrewingRecipe(inputItems, outputStack, altarPower, dimensionKey)

        recipeOutput.accept(
            id.withPrefix("cauldron_brewing/"),
            cauldronBrewingRecipe,
            builder.build(id.withPrefix("recipes/cauldron_brewing/"))
        )
    }
}