package dev.sterner.witchery.block.ritual

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.item.TaglockItem
import dev.sterner.witchery.item.WaystoneItem
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.ritual.PushMobsRitual
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.*
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
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
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.util.*


class GoldenChalkBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GOLDEN_CHALK.get(), blockPos, blockState), Container, AltarPowerConsumer {

    private var cachedAltarPos: BlockPos? = null
    var targetPlayer: UUID? = null
    var targetEntity: Int? = null
    var targetPos: GlobalPos? = null
    var ownerName: String? = null

    var items: NonNullList<ItemStack> = NonNullList.withSize(16, ItemStack.EMPTY)
    private var consumedSacrifices = mutableListOf<EntityType<*>>()

    var ritualRecipe: RitualRecipe? = null
    private var shouldRun: Boolean = false
    private var shouldStartConsumingItems: Boolean = false
    private var shouldStartConsumingSacrifices: Boolean = false
    private var isRitualActive = false
    private var tickCounter = 0
    private var ritualTickCounter = 0

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }

        if (!shouldRun) {
            return
        }

        if (ritualRecipe != null) {
            if (shouldStartConsumingSacrifices) {
                startConsumingSacrifices(level)
            } else if (shouldStartConsumingItems) {
                startConsumingItems(level)
            }

            tickCounter++

            if (isRitualActive) {

                if (ritualRecipe != null && !consumeAltarPower(level, ritualRecipe!!)) {
                    resetRitual()
                }

                ritualTickCounter++
                onTickRitual(level)

                if (ritualRecipe != null && tickCounter >= ritualRecipe!!.ticks && !ritualRecipe!!.isInfinite) {
                    onEndRitual(level)
                    resetRitual()
                }
                setChanged()
            } else {
                ritualTickCounter = 0
            }
        }
    }

    private fun onStartRitual(level: Level) {
        level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f)
        ritualRecipe?.ritualType?.onStartRitual(level, blockPos, this)
        RitualHelper.runCommand(level, blockPos, this, CommandType.START)
        isRitualActive = true
        shouldStartConsumingSacrifices = false
        setChanged()
    }

    private fun onTickRitual(level: Level) {
        if (ritualRecipe?.ritualType?.id == Witchery.id("push_mobs")) { // :(
            PushMobsRitual.onTickRitual(level, blockPos, this)
        }

        RitualHelper.runCommand(level, blockPos, this, CommandType.TICK)
    }

    private fun onEndRitual(level: Level) {
        ritualRecipe?.ritualType?.onEndRitual(level, blockPos, this)
        level.playSound(null, blockPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f)
        RitualHelper.runCommand(level, blockPos, this, CommandType.END)
        RitualHelper.summonItems(level, blockPos, this)
        RitualHelper.summonSummons(level, blockPos, this)
        items.clear()
        setChanged()
    }

    private fun startConsumingSacrifices(level: Level) {
        val entities: MutableList<LivingEntity> =
            level.getEntitiesOfClass(LivingEntity::class.java, AABB(blockPos).inflate(4.0, 1.0, 4.0)) { true }
        val recipeEntities: List<EntityType<*>> = ritualRecipe!!.inputEntities
        if (recipeEntities.isEmpty()) {
            onStartRitual(level)
            return
        }

        if (consumedSacrifices.size == recipeEntities.size) {
            return
        }

        if (tickCounter % 20 == 0) {
            val matchingEntity = entities.find { entity ->
                recipeEntities.any { recipeEntityType ->
                    entity.type == recipeEntityType && !consumedSacrifices.contains(entity.type) // Exclude already consumed entities
                }
            }

            if (matchingEntity != null) {
                matchingEntity.kill()

                consumedSacrifices.add(matchingEntity.type)
                setChanged()

                if (consumedSacrifices.containsAll(recipeEntities)) {
                    onStartRitual(level)
                }
            } else {
                resetRitual()
            }
        }
    }

    private fun startConsumingItems(level: Level) {
        val itemEntities: List<ItemEntity> = level.getEntities(
            EntityType.ITEM,
            AABB(blockPos).inflate(3.0, 0.0, 3.0)
        ) { true }

        val recipeItems = ritualRecipe?.inputItems ?: return

        if (tickCounter % 20 == 0) {
            for (itemEntity in itemEntities) {
                val stack = itemEntity.item

                val matchingRecipeItem = recipeItems.find { recipeItem ->
                    ItemStack.isSameItem(stack, recipeItem)
                }

                if (matchingRecipeItem != null) {
                    addWaystoneOrTaglockToContext(stack)

                    addItemToInventory(items, stack)
                    level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
                    stack.shrink(1)
                    if (stack.isEmpty) {
                        itemEntity.remove(Entity.RemovalReason.DISCARDED)
                    }
                    setChanged()
                    break
                }
            }
        }

        if (itemsMatchRecipe(items, recipeItems)) {
            shouldStartConsumingSacrifices = true
            setChanged()
        } else {
            if (itemEntities.isEmpty()) {
                resetRitual()
            }
        }
    }

    private fun addWaystoneOrTaglockToContext(stack: ItemStack) {
        if (stack.`is`(WitcheryItems.WAYSTONE.get())) {
            targetPos = WaystoneItem.getGlobalPos(stack)
        } else if (stack.`is`(WitcheryItems.TAGLOCK.get())
            && !(stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get())
                    && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) == true)
        ) {

            targetPlayer = TaglockItem.getPlayer(level!!, stack)?.uuid
            targetEntity = TaglockItem.getLivingEntity(level!!, stack)?.id
        }
    }

    private fun resetRitual() {
        Containers.dropContents(level, blockPos, this)
        items = NonNullList.withSize(16, ItemStack.EMPTY)
        consumedSacrifices = mutableListOf()

        ritualRecipe = null
        shouldRun = false
        shouldStartConsumingItems = false
        shouldStartConsumingSacrifices = false
        isRitualActive = false
        tickCounter = 0
        ritualTickCounter = 0
        targetPlayer = null
        targetEntity = null
        targetPos = null
        ownerName = null
        setChanged()
    }

    private fun itemsMatchRecipe(items: NonNullList<ItemStack>, recipeItems: List<ItemStack>): Boolean {
        return recipeItems.all { recipeItem ->
            items.any { inventoryItem ->
                ItemStack.isSameItem(inventoryItem, recipeItem) && inventoryItem.count >= recipeItem.count
            }
        }
    }

    private fun addItemToInventory(items: NonNullList<ItemStack>, stack: ItemStack) {
        for (i in items.indices) {
            if (items[i].isEmpty) {
                items[i] = stack.copy()
                return
            } else if (ItemStack.isSameItem(items[i], stack)) {
                items[i].grow(1)
                stack.shrink(1)
                if (stack.isEmpty) return
            }
        }
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (ritualRecipe != null && pPlayer.isShiftKeyDown) {
            items.clear()
            resetRitual()
            return InteractionResult.SUCCESS
        }

        if (cachedAltarPos == null && level is ServerLevel) {
            cachedAltarPos = getAltarPos(level as ServerLevel, blockPos)
            setChanged()
        }

        if (ritualRecipe == null && level != null) {
            val items: List<ItemEntity> =
                pPlayer.level().getEntities(EntityType.ITEM, AABB(blockPos).inflate(3.0, 0.0, 3.0)) { true }
            val entities: List<LivingEntity> = pPlayer.level()
                .getEntitiesOfClass(LivingEntity::class.java, AABB(blockPos).inflate(4.0, 1.0, 4.0)) { true }

            val recipes = level?.recipeManager?.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())

            // Filter valid recipes based on items
            val validItemRecipes = recipes?.filter { recipe ->
                val recipeItems = recipe.value.inputItems
                recipeItems.all { recipeItem ->
                    items.any { itemEntity -> ItemStack.isSameItem(itemEntity.item, recipeItem) }
                }
            }

            // Further filter by entities
            val validSacrificesAndItemsRecipe = validItemRecipes?.filter { recipe ->
                val requiredSacrifices = recipe.value.inputEntities
                requiredSacrifices.isEmpty() || requiredSacrifices.all { requiredEntity ->
                    entities.any { entity -> entity.type == requiredEntity }
                }
            }

            // Sort by number of inputs (items + entities), picking the longest recipe
            val sortedRecipes = validSacrificesAndItemsRecipe?.sortedByDescending { recipe ->
                recipe.value.inputItems.size + recipe.value.inputEntities.size
            }

            // Ensure that there are valid recipes and check conditions
            if (!sortedRecipes.isNullOrEmpty()) {
                val selectedRecipe = sortedRecipes[0] // Pick the longest recipe
                val hasValidCircle = validateRitualCircle(level!!, selectedRecipe.value)
                val hasEnoughPower = hasEnoughAltarPower(level!!, selectedRecipe.value)
                val meetsCelestialCondition = hasCelestialCondition(level!!, selectedRecipe.value)

                if (hasValidCircle && hasEnoughPower && meetsCelestialCondition) {

                    ownerName = pPlayer.gameProfile.name.replaceFirstChar(Char::uppercase)
                    ritualRecipe = selectedRecipe.value
                    shouldRun = true
                    shouldStartConsumingItems = true
                    setChanged()
                    level?.playSound(pPlayer, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
                    level?.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
                } else {
                    level?.playSound(pPlayer, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                    level?.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                }
            } else {
                level?.playSound(pPlayer, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
            }
        }

        return super.onUseWithoutItem(pPlayer)
    }

    private fun hasCelestialCondition(level: Level, recipeHolder: RitualRecipe): Boolean {
        if (recipeHolder.celestialConditions.isEmpty()) {
            return true
        }
        if (recipeHolder.celestialConditions.contains(RitualRecipe.Celestial.DAY)) {
            return RitualHelper.isDaytime(level)
        }
        if (recipeHolder.celestialConditions.contains(RitualRecipe.Celestial.FULL_MOON)) {
            return RitualHelper.isFullMoon(level)
        }
        if (recipeHolder.celestialConditions.contains(RitualRecipe.Celestial.NEW_MOON)) {
            return RitualHelper.isNewMoon(level)
        }
        if (recipeHolder.celestialConditions.contains(RitualRecipe.Celestial.NIGHT)) {
            return RitualHelper.isNighttime(level)
        }
        return false
    }

    private fun hasEnoughAltarPower(level: Level, recipe: RitualRecipe): Boolean {
        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }
        val requiredAltarPower = recipe.altarPower
        if (requiredAltarPower > 0 && cachedAltarPos != null) {
            return tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, true)
        }
        return requiredAltarPower == 0
    }

    private fun validateRitualCircle(level: Level, recipe: RitualRecipe): Boolean {
        return RitualPatternUtil.matchesPattern(level, blockPos, recipe)
    }

    private fun consumeAltarPower(level: Level, recipe: RitualRecipe): Boolean {
        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }

        val requiredAltarPower = recipe.altarPower
        if (requiredAltarPower > 0 && cachedAltarPos != null) {
            return tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, false)
        }
        return requiredAltarPower == 0
    }


    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        shouldRun = pTag.getBoolean("shouldRun")
        shouldStartConsumingSacrifices = pTag.getBoolean("shouldStartConsuming")
        shouldStartConsumingItems = pTag.getBoolean("shouldStartConsumingItems")
        isRitualActive = pTag.getBoolean("isRitualActive")
        tickCounter = pTag.getInt("tickCounter")
        ritualTickCounter = pTag.getInt("ritualTickCounter")

        if(pTag.contains("altarPos")){
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }

        if (pTag.contains("ritualId")) {
            ritualRecipe = RitualRecipe.fromNbt(pTag.getCompound("ritualId"), pRegistries)
        }

        if (pTag.contains("ownerName")) {
            ownerName = pTag.getString("ownerName")
        }
        if (pTag.contains("targetPlayer")) {
            targetPlayer = pTag.getUUID("targetPlayer")
        }
        if (pTag.contains("targetEntity")) {
            targetEntity = pTag.getInt("targetEntity")
        }
        if (pTag.contains("globalPos")) {
            val optionalDimension = getLodestoneDimension(pTag)
            if (optionalDimension.isPresent) {
                val blockPos = NbtUtils.readBlockPos(pTag, "targetPos")
                targetPos = GlobalPos.of(optionalDimension.get(), blockPos.get())
            }
        }

        consumedSacrifices = pTag.getList("ConsumedSacrifices", 8)
            .map { EntityType.byString(it.asString).get() }
            .toMutableList()
    }

    private fun getLodestoneDimension(compoundTag: CompoundTag): Optional<ResourceKey<Level>> {
        val dimensionTag = compoundTag["Dimension"]
        return if (dimensionTag != null) {
            Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, dimensionTag).result()
        } else {
            Optional.empty()
        }
    }

    private fun addGlobalPosTag(
        lodestoneDimension: ResourceKey<Level>,
        compoundTag: CompoundTag
    ) {
        Level.RESOURCE_KEY_CODEC
            .encodeStart(NbtOps.INSTANCE, lodestoneDimension)
            .resultOrPartial { _: String? -> }
            .ifPresent { tag: Tag ->
                compoundTag.put("Dimension", tag)
            }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.items, registries)
        tag.putBoolean("shouldRun", shouldRun)
        tag.putBoolean("shouldStartConsuming", shouldStartConsumingSacrifices)
        tag.putBoolean("shouldStartConsumingItems", shouldStartConsumingItems)

        tag.putBoolean("isRitualActive", isRitualActive)
        tag.putInt("tickCounter", tickCounter)
        tag.putInt("ritualTickCounter", ritualTickCounter)

        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }

        if (ritualRecipe != null) {
            tag.put("ritualId", ritualRecipe!!.toNbt(registries))
        }

        if (ownerName != null) {
            tag.putString("ownerName", ownerName!!)
        }
        if (targetPlayer != null) {
            tag.putUUID("targetPlayer", targetPlayer!!)
        }
        if (targetEntity != null) {
            tag.putInt("targetEntity", targetEntity!!)
        }
        if (targetPos != null) {
            val posTag = CompoundTag()
            NbtUtils.writeBlockPos(targetPos!!.pos()).let { blockPosTag -> posTag.put("targetPos", blockPosTag) }
            addGlobalPosTag(targetPos!!.dimension(), posTag)
            tag.put("globalPos", posTag)
        }

        val sacrificesList = consumedSacrifices.map { EntityType.getKey(it).toString() }
        tag.put("ConsumedSacrifices", sacrificesList.fold(ListTag()) { list, entity ->
            list.add(StringTag.valueOf(entity))
            list
        })
    }

    override fun clearContent() {
        items.clear()
    }

    override fun getContainerSize(): Int {
        return items.size
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun getItem(slot: Int): ItemStack {
        return items[slot]
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
        items[slot] = stack
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun receiveAltarPosition(blockPos: BlockPos) {

    }
}