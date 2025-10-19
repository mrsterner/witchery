package dev.sterner.witchery.content.recipe.brazier

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class BrazierSummoningRecipeBuilder(
    private var inputItems: MutableList<ItemStack> = mutableListOf(),
    private var outputEntities: MutableList<EntityType<*>> = mutableListOf(),
    private var altarPower: Int = 0
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    companion object {
        fun create(): BrazierSummoningRecipeBuilder {
            return BrazierSummoningRecipeBuilder()
        }
    }

    fun addInput(itemStack: ItemStack): BrazierSummoningRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInput(itemStack: Item): BrazierSummoningRecipeBuilder {
        inputItems.add(ItemStack(itemStack))
        return this
    }

    fun addSummon(entityType: EntityType<*>): BrazierSummoningRecipeBuilder {
        outputEntities.add(entityType)
        return this
    }

    fun setAltarPower(power: Int): BrazierSummoningRecipeBuilder {
        this.altarPower = power
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): BrazierSummoningRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): BrazierSummoningRecipeBuilder {
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
            BrazierSummoningRecipe(inputItems, outputEntities, altarPower)
        recipeOutput.accept(
            id.withPrefix("brazier_summoning/"),
            cauldronCraftingRecipe,
            builder.build(id.withPrefix("recipes/brazier_summoning/"))
        )
    }
}