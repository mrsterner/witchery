package dev.sterner.witchery.content.recipe.cauldron

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

class CauldronInfusionRecipeBuilder(
    private var brewInput: ItemStack = ItemStack.EMPTY,
    private var infusionItem: ItemStack = ItemStack.EMPTY,
    private var outputItem: ItemStack = ItemStack.EMPTY,
    private var altarPower: Int = 0
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): CauldronInfusionRecipeBuilder {
            return CauldronInfusionRecipeBuilder()
        }
    }

    fun setBrewInput(brew: ItemStack): CauldronInfusionRecipeBuilder {
        this.brewInput = brew
        return this
    }

    fun setBrewInput(brew: Item): CauldronInfusionRecipeBuilder {
        this.brewInput = ItemStack(brew)
        return this
    }

    fun setInfusionItem(item: ItemStack): CauldronInfusionRecipeBuilder {
        this.infusionItem = item
        return this
    }

    fun setInfusionItem(item: Item): CauldronInfusionRecipeBuilder {
        this.infusionItem = ItemStack(item)
        return this
    }

    fun setOutput(output: ItemStack): CauldronInfusionRecipeBuilder {
        this.outputItem = output
        return this
    }

    fun setOutput(output: Item): CauldronInfusionRecipeBuilder {
        this.outputItem = ItemStack(output)
        return this
    }

    fun setAltarPower(power: Int): CauldronInfusionRecipeBuilder {
        this.altarPower = power
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
        return outputItem.item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach { (name, criterion) ->
            builder.addCriterion(name, criterion)
        }

        val infusionRecipe = CauldronInfusionRecipe(brewInput, infusionItem, outputItem, altarPower)

        recipeOutput.accept(
            id.withPrefix("cauldron_infusion/"),
            infusionRecipe,
            builder.build(id.withPrefix("recipes/cauldron_infusion/"))
        )
    }
}