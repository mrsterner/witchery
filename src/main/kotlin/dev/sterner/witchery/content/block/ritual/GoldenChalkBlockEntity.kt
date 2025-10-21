package dev.sterner.witchery.content.block.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.WitcheryBaseBlockEntity

import dev.sterner.witchery.content.block.altar.AltarBlockEntity
import dev.sterner.witchery.content.block.grassper.GrassperBlockEntity
import dev.sterner.witchery.content.entity.CovenWitchEntity
import dev.sterner.witchery.content.recipe.ritual.RitualRecipe
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.api.block.AltarPowerConsumer
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.core.registry.WitcheryRitualRegistry
import dev.sterner.witchery.features.familiar.FamiliarHandler
import dev.sterner.witchery.content.item.SeerStoneItem
import dev.sterner.witchery.content.item.TaglockItem
import dev.sterner.witchery.content.item.WaystoneItem
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.coven.CovenHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.util.*
import kotlin.jvm.optionals.getOrNull


class GoldenChalkBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GOLDEN_CHALK.get(), blockPos, blockState), Container,
    AltarPowerConsumer {

    companion object {
        private const val INVENTORY_SIZE = 16
        private const val RITUAL_AREA_RADIUS = 4.0
        private const val VERTICAL_RITUAL_AREA = 1.0
        private const val COVEN_SEARCH_RADIUS = 16.0
        private const val COVEN_SEARCH_HEIGHT = 8.0
        private const val ITEM_SEARCH_RADIUS = 3.0
        private const val TICK_INTERVAL = 20
        private const val ATTUNED_STONE_POWER_BONUS = 2000

        // NBT Tag constants
        private const val TAG_SHOULD_RUN = "shouldRun"
        private const val TAG_SHOULD_CONSUME_SACRIFICES = "shouldStartConsuming"
        private const val TAG_SHOULD_CONSUME_ITEMS = "shouldStartConsumingItems"
        private const val TAG_IS_RITUAL_ACTIVE = "isRitualActive"
        private const val TAG_TICK_COUNTER = "tickCounter"
        private const val TAG_RITUAL_TICK_COUNTER = "ritualTickCounter"
        private const val TAG_HAS_RITUAL_STARTED = "hasRitualStarted"
        private const val TAG_ALTAR_POS = "altarPos"
        private const val TAG_RITUAL_ID = "ritualId"
        private const val TAG_OWNER_NAME = "ownerName"
        private const val TAG_TARGET_PLAYER = "targetPlayer"
        private const val TAG_TARGET_ENTITY = "targetEntity"
        private const val TAG_GLOBAL_POS = "globalPos"
        private const val TAG_TARGET_POS = "targetPos"
        private const val TAG_CONSUMED_SACRIFICES = "ConsumedSacrifices"
        private const val TAG_DIMENSION = "Dimension"
    }

    /**
     * Represents the current state of the ritual process
     */
    private enum class RitualState {
        IDLE,
        CONSUMING_ITEMS,
        CONSUMING_SACRIFICES,
        ACTIVE
    }

    private var cachedAltarPos: BlockPos? = null
    var targetPlayer: UUID? = null
    var targetEntity: Int? = null
    var targetPos: GlobalPos? = null
    var ownerName: String? = null

    var items: NonNullList<ItemStack> = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY)
    private var consumedSacrifices = mutableListOf<EntityType<*>>()
    private var hasRitualStarted = false
    var ritualRecipe: RitualRecipe? = null
    private var currentState: RitualState = RitualState.IDLE
    private var isRitualActive = false
    private var tickCounter = 0
    private var ritualTickCounter = 0

    /**
     * Main tick method that handles the ritual lifecycle based on current state
     */
    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)

        if (level.isClientSide || currentState == RitualState.IDLE) {
            return
        }

        tickCounter++

        when (currentState) {
            RitualState.CONSUMING_ITEMS -> processItemConsumption(level)
            RitualState.CONSUMING_SACRIFICES -> processSacrificeConsumption(level)
            RitualState.ACTIVE -> processActiveRitual(level)
            else -> {}
        }
    }

    /**
     * Processes the active ritual, consuming altar power and handling ritual ticks
     */
    private fun processActiveRitual(level: Level) {
        ritualTickCounter++

        if (ritualRecipe != null && ritualRecipe!!.altarPowerPerSecond > 0) {
            if (!consumeAltarPower(level, ritualRecipe!!)) {
                Witchery.logDebugRitual("Insufficient altar power per tick, ritual ending")
                resetRitual()
                return
            }
        }

        onTickRitual(level)

        if (ritualRecipe != null && tickCounter >= ritualRecipe!!.ticks && !ritualRecipe!!.isInfinite) {
            onEndRitual(level)
            resetRitual()
        }

        setChanged()
    }

    /**
     * Called when the ritual is successfully started
     */
    private fun onStartRitual(level: Level) {
        if (!hasRitualStarted) {
            level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f)

            if (ritualRecipe != null && ritualRecipe!!.altarPower > 0) {
                val attunedStoneBonus = getAttunedStoneBonus(level)
                val maybeAttunedItem = findAttunedStone(level)
                val requiredAltarPower = Mth.clamp(ritualRecipe!!.altarPower - attunedStoneBonus, 0, Int.MAX_VALUE)

                if (requiredAltarPower > 0 && cachedAltarPos != null) {
                    val success = tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, false)

                    if (!success) {
                        Witchery.logDebugRitual("Failed to consume initial altar power, ritual aborted")
                        resetRitual()
                        return
                    }

                    if (success && maybeAttunedItem.isNotEmpty()) {
                        maybeAttunedItem[0].item.remove(WitcheryDataComponents.ATTUNED.get())
                    }
                }
            }

            val ritualType = ritualRecipe?.ritualType?.id ?: return
            WitcheryRitualRegistry.RITUAL_REGISTRY.get(ritualType)?.onStartRitual(level, blockPos, this)

            RitualHelper.runCommand(level, blockPos, this, CommandType.START)
            isRitualActive = true
            currentState = RitualState.ACTIVE
            hasRitualStarted = true
            setChanged()
        }
    }


    /**
     * Called on each tick of an active ritual
     */
    private fun onTickRitual(level: Level) {
        val ritualType = ritualRecipe?.ritualType?.id ?: return
        WitcheryRitualRegistry.RITUAL_REGISTRY.get(ritualType)?.onTickRitual(level, blockPos, this)
        RitualHelper.runCommand(level, blockPos, this, CommandType.TICK)
    }

    /**
     * Called when a ritual ends normally (not when reset)
     */
    private fun onEndRitual(level: Level) {
        val ritualType = ritualRecipe?.ritualType?.id ?: return
        WitcheryRitualRegistry.RITUAL_REGISTRY.get(ritualType)?.onEndRitual(level, blockPos, this)

        level.playSound(null, blockPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f)
        RitualHelper.runCommand(level, blockPos, this, CommandType.END)
        RitualHelper.summonItems(level, blockPos, this)
        RitualHelper.summonSummons(level, blockPos, this)

        items.clear()
        setChanged()
    }

    /**
     * Process entity sacrifices for the ritual
     */
    private fun processSacrificeConsumption(level: Level) {
        val recipeEntities: List<EntityType<*>> = ritualRecipe?.inputEntities ?: emptyList()

        if (recipeEntities.isEmpty()) {
            onStartRitual(level)
            return
        }

        if (consumedSacrifices.size == recipeEntities.size) {
            return
        }

        if (tickCounter % TICK_INTERVAL == 0) {
            val entities: List<LivingEntity> = getSacrificeEntities(level)

            val matchingEntity = findNextRequiredSacrifice(entities, recipeEntities)

            if (matchingEntity != null) {
                consumeSacrifice(matchingEntity)

                if (consumedSacrifices.containsAll(recipeEntities)) {
                    onStartRitual(level)
                }
            } else {
                resetRitual()
            }
        }
    }

    /**
     * Find entities that could be sacrificed within the ritual area
     */
    private fun getSacrificeEntities(level: Level): List<LivingEntity> {
        return level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(blockPos).inflate(RITUAL_AREA_RADIUS, VERTICAL_RITUAL_AREA, RITUAL_AREA_RADIUS)
        ) { true }
    }

    /**
     * Find next entity that matches an unconsumed sacrifice requirement
     */
    private fun findNextRequiredSacrifice(
        entities: List<LivingEntity>,
        requiredTypes: List<EntityType<*>>
    ): LivingEntity? {
        return entities.find { entity ->
            requiredTypes.any { requiredType ->
                entity.type == requiredType && !consumedSacrifices.contains(entity.type)
            }
        }
    }

    /**
     * Consume a sacrifice entity
     */
    private fun consumeSacrifice(entity: LivingEntity) {
        entity.kill()
        consumedSacrifices.add(entity.type)
        setChanged()
    }

    /**
     * Process item consumption for the ritual
     */
    private fun processItemConsumption(level: Level) {
        val recipeItems = ritualRecipe?.inputItems ?: return

        if (tickCounter % TICK_INTERVAL == 0) {
            val consumedItems = mutableListOf<ItemStack>()
            var consumedFromEntity = false

            val itemEntities = getItemEntities(level)
            for (itemEntity in itemEntities) {
                if (tryConsumeFromItemEntity(itemEntity, recipeItems, consumedItems)) {
                    consumedFromEntity = true
                    break
                }
            }

            if (!consumedFromEntity) {
                val grasspers = findNearbyGrasspers(level)
                for ((_, grassper) in grasspers) {
                    if (tryConsumeFromGrassper(grassper, recipeItems, consumedItems)) {
                        break
                    }
                }
            }

            consumedItems.forEach { addItemToInventory(items, it) }

            if (itemsMatchRecipe(items, recipeItems)) {
                currentState = RitualState.CONSUMING_SACRIFICES
                setChanged()
            } else if (itemEntities.isEmpty() && findNearbyGrasspers(level).isEmpty()) {
                resetRitual()
            }
        }
    }

    /**
     * Get item entities within ritual area
     */
    private fun getItemEntities(level: Level): List<ItemEntity> {
        return level.getEntities(
            EntityType.ITEM,
            AABB(blockPos).inflate(ITEM_SEARCH_RADIUS, 0.0, ITEM_SEARCH_RADIUS)
        ) { true }
    }

    /**
     * Find nearby grassper block entities that could provide items
     */
    private fun findNearbyGrasspers(level: Level): List<Pair<BlockPos, GrassperBlockEntity>> {
        return BlockPos.betweenClosedStream(
            AABB.ofSize(
                blockPos.center,
                RITUAL_AREA_RADIUS,
                RITUAL_AREA_RADIUS,
                RITUAL_AREA_RADIUS
            )
        )
            .filter { level.getBlockEntity(it) is GrassperBlockEntity }
            .map { it to (level.getBlockEntity(it) as GrassperBlockEntity) }
            .filter { (_, grassper) -> !grassper.isEmpty }
            .toList()
    }

    /**
     * Try to consume a matching item from an item entity
     */
    private fun tryConsumeFromItemEntity(
        itemEntity: ItemEntity,
        recipeItems: List<ItemStack>,
        consumedItems: MutableList<ItemStack>
    ): Boolean {
        val stack = itemEntity.item

        recipeItems.find { recipeItem ->
            ItemStack.isSameItem(stack, recipeItem)
        } ?: return false

        consumedItems.add(stack.copy())
        addWaystoneOrTaglockToContext(stack)

        stack.shrink(1)
        if (stack.isEmpty) {
            itemEntity.remove(Entity.RemovalReason.DISCARDED)
        }

        level?.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
        return true
    }

    /**
     * Try to consume a matching item from a grassper block entity
     */
    private fun tryConsumeFromGrassper(
        grassper: GrassperBlockEntity,
        recipeItems: List<ItemStack>,
        consumedItems: MutableList<ItemStack>
    ): Boolean {
        val grassperItem = grassper.item[0].copy()

        recipeItems.find { recipeItem ->
            ItemStack.isSameItem(grassperItem, recipeItem)
        } ?: return false

        consumedItems.add(grassperItem)
        grassper.item[0].shrink(1)
        grassper.setChanged()
        addWaystoneOrTaglockToContext(grassperItem)

        level?.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
        return true
    }

    /**
     * Extract waystone or taglock data from items to use in ritual context
     */
    private fun addWaystoneOrTaglockToContext(stack: ItemStack) {
        if (stack.`is`(WitcheryItems.WAYSTONE.get())) {
            targetPos = WaystoneItem.getGlobalPos(stack)
        }

        if (stack.`is`(WitcheryItems.TAGLOCK.get())) {
            level?.let { safeLevel ->
                targetPlayer = TaglockItem.getPlayer(safeLevel, stack)?.uuid
                targetEntity = TaglockItem.getLivingEntity(safeLevel, stack)?.id
            }
        }
    }

    /**
     * Reset the ritual to its initial state
     */
    private fun resetRitual() {
        if (!hasRitualStarted) {
            level?.let { Containers.dropContents(it, blockPos, this) }
        }

        items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY)
        consumedSacrifices = mutableListOf()

        ritualRecipe = null
        currentState = RitualState.IDLE
        isRitualActive = false
        hasRitualStarted = false
        tickCounter = 0
        ritualTickCounter = 0

        targetPlayer = null
        targetEntity = null
        targetPos = null
        ownerName = null

        setChanged()
    }

    /**
     * Check if the current inventory matches all required recipe items
     */
    private fun itemsMatchRecipe(items: NonNullList<ItemStack>, recipeItems: List<ItemStack>): Boolean {
        return recipeItems.all { recipeItem ->
            items.any { inventoryItem ->
                ItemStack.isSameItem(inventoryItem, recipeItem) && inventoryItem.count >= recipeItem.count
            }
        }
    }

    /**
     * Add an item to the ritual inventory
     */
    private fun addItemToInventory(items: NonNullList<ItemStack>, stack: ItemStack) {

        for (i in items.indices) {
            if (ItemStack.isSameItem(items[i], stack)) {
                items[i].grow(1)
                stack.shrink(1)
                if (stack.isEmpty) return
            }
        }

        for (i in items.indices) {
            if (items[i].isEmpty) {
                items[i] = stack.copy()
                return
            }
        }
    }

    /**
     * Handle when a player uses the block without an item
     */
    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (WitcheryApi.isInSpiritWorld(pPlayer)) {
            return InteractionResult.PASS
        }

        if (ritualRecipe != null && pPlayer.isShiftKeyDown) {
            items.clear()
            resetRitual()
            Witchery.logDebugRitual("Ritual reset by player ${pPlayer.name.string}.")
            return InteractionResult.SUCCESS
        }

        updateAltarCache(pPlayer.level())

        if (ritualRecipe == null && level is ServerLevel) {
            tryStartRitual(pPlayer)
        }

        return super.onUseWithoutItem(pPlayer)
    }

    /**
     * Update the cached altar position if needed
     */
    private fun updateAltarCache(level: Level) {
        if (cachedAltarPos == null && level is ServerLevel) {
            cachedAltarPos = getAltarPos(level, blockPos)
            setChanged()
            Witchery.logDebugRitual("Cached altar position updated: $cachedAltarPos.")
        }
    }

    /**
     * Try to start a ritual based on nearby items and entities
     */
    private fun tryStartRitual(player: Player) {
        Witchery.logDebugRitual("No current ritual recipe found. Searching for valid recipes.")

        val items = collectNearbyItems(player.level())
        val entities = collectNearbyEntities(player.level())

        Witchery.logDebugRitual("Found ${items.size} items and ${entities.size} entities near block position $blockPos.")

        val recipes = level?.recipeManager?.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())
        val selectedRecipe = findBestMatchingRecipe(recipes, items, entities)

        if (selectedRecipe != null) {
            Witchery.logDebugRitual("Selected recipe: ${selectedRecipe.value} with inputs: ${selectedRecipe.value.inputItems.size} items and ${selectedRecipe.value.inputEntities.size} entities.")

            if (validateRitualRequirements(player, level!!, selectedRecipe.value)) {
                startRitual(player, selectedRecipe.value)
            } else {
                Witchery.logDebugRitual("Ritual failed due to unmet conditions.")
                playRitualFailureSound(player)
            }
        } else {
            Witchery.logDebugRitual("No valid rituals found. Extinguishing ritual.")
            playRitualFailureSound(player)
        }
    }

    /**
     * Start the ritual with the selected recipe
     */
    private fun startRitual(player: Player, recipe: RitualRecipe) {
        ownerName = player.gameProfile.name.replaceFirstChar(Char::uppercase)
        ritualRecipe = recipe
        currentState = RitualState.CONSUMING_ITEMS
        setChanged()

        Witchery.logDebugRitual("Ritual started by ${player.name.string} with recipe ${ritualRecipe}.")
        playRitualStartSound(player)
    }

    /**
     * Play sound when a ritual starts successfully
     */
    private fun playRitualStartSound(player: Player) {
        level?.playSound(player, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
        level?.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
    }

    /**
     * Play sound when a ritual fails to start
     */
    private fun playRitualFailureSound(player: Player) {
        level?.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
        level?.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
    }

    /**
     * Collect all items from dropped items and grasspers
     */
    private fun collectNearbyItems(level: Level): List<ItemStack> {
        val result = mutableListOf<ItemStack>()

        result.addAll(
            level.getEntities(
                EntityType.ITEM,
                AABB(blockPos).inflate(ITEM_SEARCH_RADIUS, 0.0, ITEM_SEARCH_RADIUS)
            ) { true }.map { it.item })

        BlockPos.betweenClosedStream(
            AABB.ofSize(
                blockPos.center,
                RITUAL_AREA_RADIUS,
                RITUAL_AREA_RADIUS,
                RITUAL_AREA_RADIUS
            )
        )
            .filter { level.getBlockEntity(it) is GrassperBlockEntity }
            .map { level.getBlockEntity(it) as GrassperBlockEntity }
            .filter { !it.isEmpty }
            .forEach { result.add(it.item[0]) }

        return result
    }

    /**
     * Collect nearby entities that could be used as sacrifices
     */
    private fun collectNearbyEntities(level: Level): List<LivingEntity> {
        return level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(blockPos).inflate(RITUAL_AREA_RADIUS, VERTICAL_RITUAL_AREA, RITUAL_AREA_RADIUS)
        ) { true }
    }

    /**
     * Find the best matching ritual recipe based on available items and entities
     */
    private fun findBestMatchingRecipe(
        recipes: MutableList<RecipeHolder<RitualRecipe>>?,
        items: List<ItemStack>,
        entities: List<LivingEntity>
    ): RecipeHolder<RitualRecipe>? {
        val validItemRecipes = recipes?.filter { recipe ->
            val recipeItems = recipe.value.inputItems
            recipeItems.all { recipeItem ->
                items.any { itemStack -> ItemStack.isSameItem(itemStack, recipeItem) }
            }
        } ?: return null

        Witchery.logDebugRitual("Filtered valid item recipes: ${validItemRecipes.size} found.")

        val validSacrificesAndItemsRecipe = validItemRecipes.filter { recipe ->
            val requiredSacrifices = recipe.value.inputEntities
            requiredSacrifices.isEmpty() || requiredSacrifices.all { requiredEntity ->
                entities.any { entity -> entity.type == requiredEntity }
            }
        }

        Witchery.logDebugRitual("Filtered valid item and entity recipes: ${validSacrificesAndItemsRecipe.size} found.")

        return validSacrificesAndItemsRecipe.maxByOrNull { recipe ->
            recipe.value.inputItems.size + recipe.value.inputEntities.size
        }
    }

    /**
     * Validate all requirements for a ritual to start
     */
    private fun validateRitualRequirements(player: Player, level: Level, recipe: RitualRecipe): Boolean {
        val hasValidCircle = validateRitualCircle(level, recipe)
        val hasEnoughPower = hasEnoughAltarPower(level, recipe)
        val meetsCelestialCondition = hasCelestialCondition(level, recipe)
        val meetsWeatherRequirement = hasWeatherCondition(level, recipe)
        val requiresCat = requiresCat(player, recipe)
        val hasCovenCount = hasCovenCondition(player, recipe)

        Witchery.logDebugRitual(
            "Ritual conditions - Valid Circle: $hasValidCircle, " +
                    "Enough Power: $hasEnoughPower : ${recipe.altarPower}, " +
                    "Celestial Condition: $meetsCelestialCondition, " +
                    "Coven Condition: $hasCovenCount, " +
                    "Cat: $requiresCat, " +
                    "Weather Condition: $meetsWeatherRequirement"
        )

        return hasValidCircle && hasEnoughPower && meetsCelestialCondition && hasCovenCount && meetsWeatherRequirement && requiresCat
    }

    private fun requiresCat(player: Player, recipe: RitualRecipe): Boolean {
        if (!recipe.requireCat) {
            return true
        }

        if (player.level() is ServerLevel) {
            return FamiliarHandler.getFamiliarEntityType(
                playerUUID = player.uuid,
                player.level() as ServerLevel
            ) == EntityType.CAT
        }

        return false
    }

    private fun hasWeatherCondition(level: Level, recipe: RitualRecipe): Boolean {
        if (recipe.weather.isEmpty()) {
            return true
        }
        val rain = level.isRaining
        val thunder = level.isThundering
        val clear = !rain && !thunder
        return when {
            recipe.weather.contains(RitualRecipe.Weather.CLEAR) ->
                clear

            recipe.weather.contains(RitualRecipe.Weather.STORM) ->
                thunder

            recipe.weather.contains(RitualRecipe.Weather.RAIN) ->
                rain || thunder

            else -> false
        }
    }

    /**
     * Check if celestial conditions are met (time of day, moon phase)
     */
    private fun hasCelestialCondition(level: Level, recipe: RitualRecipe): Boolean {
        if (recipe.celestialConditions.isEmpty()) {
            return true
        }

        return when {
            recipe.celestialConditions.contains(RitualRecipe.Celestial.DAY) ->
                RitualHelper.isDaytime(level)

            recipe.celestialConditions.contains(RitualRecipe.Celestial.FULL_MOON) ->
                RitualHelper.isFullMoon(level)

            recipe.celestialConditions.contains(RitualRecipe.Celestial.NEW_MOON) ->
                RitualHelper.isNewMoon(level)

            recipe.celestialConditions.contains(RitualRecipe.Celestial.NIGHT) ->
                RitualHelper.isNighttime(level)

            recipe.celestialConditions.contains(RitualRecipe.Celestial.WAXING) ->
                RitualHelper.isWaxing(level)

            recipe.celestialConditions.contains(RitualRecipe.Celestial.WANING) ->
                RitualHelper.isWaning(level)

            else -> false
        }
    }

    /**
     * Checks if there are enough coven witches nearby for the ritual
     * This function counts actual witches in the area first
     * If there aren't enough, it checks if the player has a seer stone and belongs to a coven
     */
    private fun hasCovenCondition(player: Player, recipe: RitualRecipe): Boolean {
        if (recipe.covenCount == 0) {
            return true
        }

        val witches = player.level().getEntities(
            WitcheryEntityTypes.COVEN_WITCH.get(),
            AABB(blockPos).inflate(COVEN_SEARCH_RADIUS, COVEN_SEARCH_HEIGHT, COVEN_SEARCH_RADIUS)
        ) {
            it.isAlive && it is CovenWitchEntity && it.getIsCoven()
        }

        witches.forEach {
            if (it is CovenWitchEntity) {
                it.setLastRitualPos(Optional.of(this.blockPos))
            }
        }

        if (witches.size >= recipe.covenCount) {
            return true
        }

        if (player.inventory.contains { it.`is`(WitcheryItems.SEER_STONE.get()) }) {
            val activeWitches = CovenHandler.getActiveCovenSize(player, blockPos)

            if (activeWitches >= recipe.covenCount) {
                level?.let { safeLevel ->
                    SeerStoneItem.summonWitchesAroundCircle(player, safeLevel, activeWitches)
                }
                return true
            }
        }

        player.displayClientMessage(Component.translatable("witchery.too_few_in_coven"), true)
        return false
    }

    /**
     * Check if there's enough altar power for the ritual
     */
    private fun hasEnoughAltarPower(level: Level, recipe: RitualRecipe): Boolean {
        val attunedStoneBonus = getAttunedStoneBonus(level)

        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }

        val requiredAltarPower = Mth.clamp(recipe.altarPower - attunedStoneBonus, 0, Int.MAX_VALUE)

        if (requiredAltarPower <= 0) {
            return true
        }

        return cachedAltarPos != null &&
                tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, true)
    }

    /**
     * Check if the ritual pattern matches the required pattern
     */
    private fun validateRitualCircle(level: Level, recipe: RitualRecipe): Boolean {
        return RitualPatternUtil.matchesPattern(level, blockPos, recipe)
    }

    /**
     * Consume altar power for an active ritual
     */
    private fun consumeAltarPower(level: Level, recipe: RitualRecipe): Boolean {
        if (recipe.altarPowerPerSecond <= 0) {
            return true
        }

        val attunedStoneBonus = getAttunedStoneBonus(level)
        val maybeAttunedItem = findAttunedStone(level)

        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }

        val requiredAltarPower = recipe.altarPowerPerSecond - attunedStoneBonus

        if (requiredAltarPower <= 0) {
            return true
        }

        val success = cachedAltarPos != null &&
                tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, false)

        if (success && maybeAttunedItem.isNotEmpty()) {
            maybeAttunedItem[0].item.remove(WitcheryDataComponents.ATTUNED.get())
        }

        return success
    }

    /**
     * Calculate bonus from attuned stone
     */
    private fun getAttunedStoneBonus(level: Level): Int {
        val maybeAttunedItem = findAttunedStone(level)
        return if (maybeAttunedItem.isNotEmpty()) ATTUNED_STONE_POWER_BONUS else 0
    }

    /**
     * Find attuned stone near the ritual
     */
    private fun findAttunedStone(level: Level): List<ItemEntity> {
        return level.getEntities(
            EntityType.ITEM,
            AABB(blockPos).inflate(ITEM_SEARCH_RADIUS, 0.0, ITEM_SEARCH_RADIUS)
        ) {
            it.item.`is`(WitcheryItems.ATTUNED_STONE.get()) &&
                    it.item.get(WitcheryDataComponents.ATTUNED.get()) == true
        }
    }


    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)

        currentState = when {
            pTag.getBoolean(TAG_SHOULD_RUN) -> {
                when {
                    pTag.getBoolean(TAG_IS_RITUAL_ACTIVE) -> RitualState.ACTIVE
                    pTag.getBoolean(TAG_SHOULD_CONSUME_SACRIFICES) -> RitualState.CONSUMING_SACRIFICES
                    pTag.getBoolean(TAG_SHOULD_CONSUME_ITEMS) -> RitualState.CONSUMING_ITEMS
                    else -> RitualState.IDLE
                }
            }
            else -> RitualState.IDLE
        }

        isRitualActive = pTag.getBoolean(TAG_IS_RITUAL_ACTIVE)
        tickCounter = pTag.getInt(TAG_TICK_COUNTER)
        ritualTickCounter = pTag.getInt(TAG_RITUAL_TICK_COUNTER)
        hasRitualStarted = pTag.getBoolean(TAG_HAS_RITUAL_STARTED)

        if (pTag.contains(TAG_ALTAR_POS)) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, TAG_ALTAR_POS).getOrNull()
        }

        if (pTag.contains(TAG_RITUAL_ID)) {
            val ritualTypeId = ResourceLocation.parse(pTag.getString(TAG_RITUAL_ID))
            ritualRecipe = findRitualRecipeByType(ritualTypeId)

            if (ritualRecipe == null) {
                Witchery.logDebugRitual("Failed to find ritual recipe with type: $ritualTypeId")
            }
        }

        if (pTag.contains(TAG_OWNER_NAME)) {
            ownerName = pTag.getString(TAG_OWNER_NAME)
        }

        if (pTag.contains(TAG_TARGET_PLAYER)) {
            targetPlayer = pTag.getUUID(TAG_TARGET_PLAYER)
        }

        if (pTag.contains(TAG_TARGET_ENTITY)) {
            targetEntity = pTag.getInt(TAG_TARGET_ENTITY)
        }

        if (pTag.contains(TAG_GLOBAL_POS)) {
            loadGlobalPosition(pTag)
        }

        consumedSacrifices = pTag.getList(TAG_CONSUMED_SACRIFICES, 8)
            .mapNotNull { EntityType.byString(it.asString).getOrNull() }
            .toMutableList()
    }

    /**
     * Find a ritual recipe by its ritual type ID
     */
    private fun findRitualRecipeByType(ritualTypeId: ResourceLocation): RitualRecipe? {
        val allRecipes = level?.recipeManager?.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())
        return allRecipes?.firstOrNull { it.value.ritualType?.id == ritualTypeId }?.value
    }

    /**
     * Load global position data from NBT
     */
    private fun loadGlobalPosition(tag: CompoundTag) {
        val optionalDimension = getLodestoneDimension(tag)
        if (optionalDimension.isPresent) {
            val blockPosOpt = NbtUtils.readBlockPos(tag, TAG_TARGET_POS)
            blockPosOpt.ifPresent { pos ->
                targetPos = GlobalPos.of(optionalDimension.get(), pos)
            }
        }
    }

    /**
     * Get dimension from NBT data
     */
    private fun getLodestoneDimension(tag: CompoundTag): Optional<ResourceKey<Level>> {
        val dimensionTag = tag[TAG_DIMENSION] ?: return Optional.empty()
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, dimensionTag).result()
    }

    /**
     * Save NBT data
     */
    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.items, registries)

        tag.putBoolean(TAG_SHOULD_RUN, currentState != RitualState.IDLE)
        tag.putBoolean(TAG_SHOULD_CONSUME_SACRIFICES, currentState == RitualState.CONSUMING_SACRIFICES)
        tag.putBoolean(TAG_SHOULD_CONSUME_ITEMS, currentState == RitualState.CONSUMING_ITEMS)
        tag.putBoolean(TAG_IS_RITUAL_ACTIVE, isRitualActive)
        tag.putBoolean(TAG_HAS_RITUAL_STARTED, hasRitualStarted)
        tag.putInt(TAG_TICK_COUNTER, tickCounter)
        tag.putInt(TAG_RITUAL_TICK_COUNTER, ritualTickCounter)

        if (cachedAltarPos != null) {
            tag.put(TAG_ALTAR_POS, NbtUtils.writeBlockPos(cachedAltarPos!!))
        }

        if (ritualRecipe != null) {
            tag.putString(TAG_RITUAL_ID, ritualRecipe!!.ritualType?.id.toString())
        }

        ownerName?.let { tag.putString(TAG_OWNER_NAME, it) }
        targetPlayer?.let { tag.putUUID(TAG_TARGET_PLAYER, it) }
        targetEntity?.let { tag.putInt(TAG_TARGET_ENTITY, it) }

        if (targetPos != null) {
            saveGlobalPosition(tag)
        }

        val sacrificesList = consumedSacrifices.map { EntityType.getKey(it).toString() }
        tag.put(TAG_CONSUMED_SACRIFICES, sacrificesList.fold(ListTag()) { list, entity ->
            list.add(StringTag.valueOf(entity))
            list
        })
    }

    /**
     * Save global position data to NBT
     */
    private fun saveGlobalPosition(tag: CompoundTag) {
        targetPos?.let { pos ->
            val posTag = CompoundTag()
            posTag.put(TAG_TARGET_POS, NbtUtils.writeBlockPos(pos.pos()))
            addGlobalPosTag(pos.dimension(), posTag)
            tag.put(TAG_GLOBAL_POS, posTag)
        }
    }

    /**
     * Add dimension data to NBT
     */
    private fun addGlobalPosTag(lodestoneDimension: ResourceKey<Level>, tag: CompoundTag) {
        Level.RESOURCE_KEY_CODEC
            .encodeStart(NbtOps.INSTANCE, lodestoneDimension)
            .resultOrPartial { error -> Witchery.logDebugRitual("Failed to encode dimension: $error") }
            .ifPresent { dimensionTag -> tag.put(TAG_DIMENSION, dimensionTag) }
    }


    override fun clearContent() {
        items.clear()
    }

    override fun getContainerSize(): Int {
        return items.size
    }

    override fun isEmpty(): Boolean {
        return items.all { it.isEmpty }
    }

    override fun getItem(slot: Int): ItemStack {
        return if (slot in items.indices) items[slot] else ItemStack.EMPTY
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(this.items, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }
        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(this.items, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot in items.indices) {
            items[slot] = stack
            this.setChanged()
        }
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }
}