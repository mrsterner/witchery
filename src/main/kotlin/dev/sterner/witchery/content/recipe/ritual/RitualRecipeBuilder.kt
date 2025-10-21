package dev.sterner.witchery.content.recipe.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.content.block.ritual.CommandType
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.ritual.EmptyRitual
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
class RitualRecipeBuilder private constructor() : RecipeBuilder {

    private var ritual: Ritual = EmptyRitual()
    private var inputItems: MutableList<ItemStack> = mutableListOf()
    private var inputEntities: MutableList<EntityType<*>> = mutableListOf()
    private var outputItems: MutableList<ItemStack> = mutableListOf()
    private var outputEntities: MutableList<EntityType<*>> = mutableListOf()
    private var altarPower: Int = 0
    private var altarPowerPerTick: Int = 0
    private var covenCount: Int = 0
    private var commands: MutableSet<CommandType> = mutableSetOf()
    private var isInfinite: Boolean = false
    private var floatingItemOutput: Boolean = false
    private var ticks: Int = 0
    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var pattern: List<String> = listOf()
    private val blockMapping: MutableMap<Char, Block> = mutableMapOf<Char, Block>().apply {
        put('G', WitcheryBlocks.GOLDEN_CHALK_BLOCK.get())
    }
    private var celestialConditions: Set<RitualRecipe.Celestial> = setOf()
    private var weather: MutableSet<RitualRecipe.Weather> = mutableSetOf()
    private var requireCat = false
    private val ritualDataTag = CompoundTag()

    companion object {
        fun create(): RitualRecipeBuilder {
            return RitualRecipeBuilder()
        }
    }

    fun addInputItem(itemStack: ItemStack): RitualRecipeBuilder {
        inputItems.add(itemStack)
        return this
    }

    fun addInputItem(itemStack: Item): RitualRecipeBuilder {
        inputItems.add(ItemStack(itemStack))
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

    fun addOutputItem(itemStack: Item): RitualRecipeBuilder {
        outputItems.add(ItemStack(itemStack))
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

    fun setAltarPowerPerTick(power: Int): RitualRecipeBuilder {
        this.altarPowerPerTick = power
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

    fun setCelestialConditions(celestialConditions: Set<RitualRecipe.Celestial>): RitualRecipeBuilder {
        this.celestialConditions = celestialConditions
        return this
    }

    fun requireCat(): RitualRecipeBuilder {
        this.requireCat = true
        return this
    }

    fun setWeather(weather: RitualRecipe.Weather): RitualRecipeBuilder {
        this.weather.add(weather)
        return this
    }

    fun setRequireNight(): RitualRecipeBuilder {
        this.celestialConditions = setOf(RitualRecipe.Celestial.NIGHT)
        return this
    }

    fun setRequireDay(): RitualRecipeBuilder {
        this.celestialConditions = setOf(RitualRecipe.Celestial.DAY)
        return this
    }

    fun setRequireFullMoon(): RitualRecipeBuilder {
        this.celestialConditions = setOf(RitualRecipe.Celestial.NIGHT, RitualRecipe.Celestial.FULL_MOON)
        return this
    }

    fun setRequireNewMoon(): RitualRecipeBuilder {
        this.celestialConditions = setOf(RitualRecipe.Celestial.NIGHT, RitualRecipe.Celestial.NEW_MOON)
        return this
    }

    fun setCovenCount(count: Int): RitualRecipeBuilder {
        this.covenCount = count
        return this
    }

    fun pattern(vararg lines: String): RitualRecipeBuilder {
        pattern = lines.toList()
        return this
    }

    fun addSmallPattern(small: Block): RitualRecipeBuilder {
        pattern = listOf(
            "__SSS__",
            "_S___S_",
            "S_____S",
            "S__G__S",
            "S_____S",
            "_S___S_",
            "__SSS__"
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('S', small)
        return this
    }

    fun addSmallAndMediumPattern(small: Block, medium: Block): RitualRecipeBuilder {
        pattern = listOf(
            "___MMMMM___",
            "__M_____M__",
            "_M__SSS__M_",
            "M__S___S__M",
            "M_S_____S_M",
            "M_S__G__S_M",
            "M_S_____S_M",
            "M__S___S__M",
            "_M__SSS__M_",
            "__M_____M__",
            "___MMMMM___"
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('M', medium)
        define('S', small)
        return this
    }

    fun addMediumPattern(medium: Block): RitualRecipeBuilder {
        pattern = listOf(
            "___MMMMM___",
            "__M_____M__",
            "_M_______M_",
            "M_________M",
            "M_________M",
            "M____G____M",
            "M_________M",
            "M_________M",
            "_M_______M_",
            "__M_____M__",
            "___MMMMM___"
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('M', medium)
        return this
    }

    fun addSmallAndMediumAndLargePattern(small: Block, medium: Block, large: Block): RitualRecipeBuilder {
        pattern = listOf(
            "_____LLLLL_____",
            "___LL_____LL___",
            "__L__MMMMM__L__",
            "_L__M_____M__L_",
            "_L_M__SSS__M_L_",
            "L_M__S___S__M_L",
            "L_M_S_____S_M_L",
            "L_M_S__G__S_M_L",
            "L_M_S_____S_M_L",
            "L_M__S___S__M_L",
            "_L_M__SSS__M_L_",
            "_L__M_____M__L_",
            "__L__MMMMM__L__",
            "___LL_____LL___",
            "_____LLLLL_____",
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('L', large)
        define('M', medium)
        define('S', small)
        return this
    }

    fun addSmallAndLargePattern(small: Block, large: Block): RitualRecipeBuilder {
        pattern = listOf(
            "_____LLLLL_____",
            "___LL_____LL___",
            "__L_________L__",
            "_L___________L_",
            "_L____SSS____L_",
            "L____S___S____L",
            "L___S_____S___L",
            "L___S__G__S___L",
            "L___S_____S___L",
            "L____S___S____L",
            "_L____SSS____L_",
            "_L___________L_",
            "__L_________L__",
            "___LL_____LL___",
            "_____LLLLL_____",
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('L', large)
        define('S', small)
        return this
    }

    fun addMediumAndLargePattern(medium: Block, large: Block): RitualRecipeBuilder {
        pattern = listOf(
            "_____LLLLL_____",
            "___LL_____LL___",
            "__L__MMMMM__L__",
            "_L__M_____M__L_",
            "_L_M_______M_L_",
            "L_M_________M_L",
            "L_M_________M_L",
            "L_M____G____M_L",
            "L_M_________M_L",
            "L_M_________M_L",
            "_L_M_______M_L_",
            "_L__M_____M__L_",
            "__L__MMMMM__L__",
            "___LL_____LL___",
            "_____LLLLL_____",
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('L', large)
        define('M', medium)
        return this
    }

    fun addLargePattern(large: Block): RitualRecipeBuilder {
        pattern = listOf(
            "_____LLLLL_____",
            "___LL_____LL___",
            "__L_________L__",
            "_L___________L_",
            "_L___________L_",
            "L_____________L",
            "L_____________L",
            "L______G______L",
            "L_____________L",
            "L_____________L",
            "_L___________L_",
            "_L___________L_",
            "__L_________L__",
            "___LL_____LL___",
            "_____LLLLL_____",
        )
        define('G', WitcheryItems.GOLDEN_CHALK.get().block)
        define('L', large)
        return this
    }

    fun define(letter: Char, block: Block): RitualRecipeBuilder {
        blockMapping[letter] = block
        return this
    }

    fun setCustomRitual(ritual: Ritual): RitualRecipeBuilder {
        this.ritual = ritual
        return this
    }

    fun addRitualData(key: String, value: String): RitualRecipeBuilder {
        ritualDataTag.putString(key, value)
        return this
    }

    fun addRitualData(key: String, value: Int): RitualRecipeBuilder {
        ritualDataTag.putInt(key, value)
        return this
    }

    fun addRitualData(key: String, value: Boolean): RitualRecipeBuilder {
        ritualDataTag.putBoolean(key, value)
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

        val conditions = RitualConditions(
            celestialConditions = celestialConditions,
            weather = weather.toSet(),
            requireCat = requireCat,
            ritualData = ritualDataTag
        )

        val recipe = RitualRecipe(
            ritualType = ritual,
            inputItems = inputItems,
            inputEntities = inputEntities,
            outputItems = outputItems,
            outputEntities = outputEntities,
            altarPower = altarPower,
            altarPowerPerSecond = altarPowerPerTick,
            covenCount = covenCount,
            commands = commands,
            isInfinite = isInfinite,
            floatingItemOutput = floatingItemOutput,
            ticks = ticks,
            pattern = pattern,
            blockMapping = blockMapping,
            conditions = conditions
        )

        recipeOutput.accept(
            id.withPrefix("ritual/"),
            recipe,
            builder.build(id.withPrefix("recipes/ritual/"))
        )
    }
}