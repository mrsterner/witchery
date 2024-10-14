package dev.sterner.witchery.recipe.ritual

import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.recipe.WitcheryRecipeBuilder
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

class RitualRecipeBuilder private constructor() : WitcheryRecipeBuilder() {

    private var inputItems: MutableList<ItemStack> = mutableListOf()
    private var inputEntities: MutableList<EntityType<*>> = mutableListOf()
    private var outputItems: MutableList<ItemStack> = mutableListOf()
    private var outputEntities: MutableList<EntityType<*>> = mutableListOf()
    private var altarPower: Int = 0
    private var commands: MutableSet<CommandType> = mutableSetOf()
    private var isInfinite: Boolean = false
    private var floatingItemOutput: Boolean = false
    private var ticks: Int = 0
    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()

    companion object {
        fun create(): RitualRecipeBuilder {
            return RitualRecipeBuilder()
        }
    }

    fun addInputItem(itemStack: ItemStack): RitualRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInputItems(itemStacks: List<ItemStack>): RitualRecipeBuilder {
        inputItems.addAll(itemStacks)
        return this
    }

    fun addInputEntity(entityType: EntityType<*>): RitualRecipeBuilder {
        inputEntities.add(entityType)
        return this
    }

    fun addInputEntities(entityTypes: List<EntityType<*>>): RitualRecipeBuilder {
        inputEntities.addAll(entityTypes)
        return this
    }

    fun addOutputItem(itemStack: ItemStack): RitualRecipeBuilder {
        outputItems.add(itemStack)
        return this
    }

    fun addOutputItems(itemStacks: List<ItemStack>): RitualRecipeBuilder {
        outputItems.addAll(itemStacks)
        return this
    }

    fun addOutputEntity(entityType: EntityType<*>): RitualRecipeBuilder {
        outputEntities.add(entityType)
        return this
    }

    fun addOutputEntities(entityTypes: List<EntityType<*>>): RitualRecipeBuilder {
        outputEntities.addAll(entityTypes)
        return this
    }

    fun setAltarPower(power: Int): RitualRecipeBuilder {
        this.altarPower = power
        return this
    }

    fun addCommand(commandType: CommandType): RitualRecipeBuilder {
        commands.add(commandType)
        return this
    }

    fun addCommands(commandTypes: Set<CommandType>): RitualRecipeBuilder {
        commands.addAll(commandTypes)
        return this
    }

    fun setInfinite(infinite: Boolean): RitualRecipeBuilder {
        this.isInfinite = infinite
        return this
    }

    fun setFloatingItemOutput(floating: Boolean): RitualRecipeBuilder {
        this.floatingItemOutput = floating
        return this
    }

    fun setTicks(ticks: Int): RitualRecipeBuilder {
        this.ticks = ticks
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): RitualRecipeBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): RitualRecipeBuilder {
        return this
    }

    override fun getResult(): Item {
        return outputItems.firstOrNull()?.item ?: Items.AIR
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach { (name, criterion) -> builder.addCriterion(name, criterion) }

        val recipe = RitualRecipe(
            inputItems = inputItems,
            inputEntities = inputEntities,
            outputItems = outputItems,
            outputEntities = outputEntities,
            altarPower = altarPower,
            commands = commands,
            isInfinite = isInfinite,
            floatingItemOutput = floatingItemOutput,
            ticks = ticks
        )

        recipeOutput.accept(
            suffixHash(id.withPrefix("ritual/"), inputItems),
            recipe,
            builder.build(suffixHash(id.withPrefix("recipes/ritual/"), inputItems))
        )
    }
}