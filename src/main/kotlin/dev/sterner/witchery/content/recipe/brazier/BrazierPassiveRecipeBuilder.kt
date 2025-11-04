package dev.sterner.witchery.content.recipe.brazier

import dev.sterner.witchery.core.api.BrazierPassive
import dev.sterner.witchery.features.brazier.EmptyBrazierPassive
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class BrazierPassiveRecipeBuilder(
    private var passive: BrazierPassive = EmptyBrazierPassive(),
    private var inputItems: MutableList<ItemStack> = mutableListOf(),
    private var altarPower: Int = 0
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): BrazierPassiveRecipeBuilder {
            return BrazierPassiveRecipeBuilder()
        }
    }

    fun setPassive(passive: BrazierPassive): BrazierPassiveRecipeBuilder {
        this.passive = passive
        return this
    }

    fun addInput(itemStack: ItemStack): BrazierPassiveRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInput(itemStack: Item): BrazierPassiveRecipeBuilder {
        inputItems.add(ItemStack(itemStack))
        return this
    }

    fun setAltarPower(power: Int): BrazierPassiveRecipeBuilder {
        this.altarPower = power
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): BrazierPassiveRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): BrazierPassiveRecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item {
        return Items.AIR
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
            BrazierPassiveRecipe(passive, inputItems, altarPower)
        recipeOutput.accept(
            id.withPrefix("brazier_passive/"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/brazier_passive/"))
        )
    }
}