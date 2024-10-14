package dev.sterner.witchery.block.ritual

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity.Companion.SLOT_INPUT
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom.pos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB


class GoldenChalkBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GOLDEN_CHALK.get(), blockPos, blockState), Container {

    var items: NonNullList<ItemStack> = NonNullList.withSize(16, ItemStack.EMPTY)
    private var consumedSacrifices = mutableListOf<EntityType<*>>()

    var ritualRecipe: RitualRecipe? = null
    private var ritualManager = RitualManager()
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
                shouldStartConsumingSacrifices = false
                ritualTickCounter++

                onTickRitual(level)

                if (tickCounter >= ritualRecipe!!.ticks && !ritualRecipe!!.isInfinite) {
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
        ritualManager.runCommand(level, blockPos, this, RitualManager.CommandType.START)
    }

    private fun onTickRitual(level: Level) {
        if (tickCounter % 20 == 0) {
            level.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.BLOCKS)
        }
    }

    private fun onEndRitual(level: Level) {
        level.playSound(null, blockPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f)
        ritualManager.runCommand(level, blockPos, this, RitualManager.CommandType.END)
        ritualManager.summonItems(level, blockPos, this)
        ritualManager.summonSummons(level,blockPos, this)
        items.clear()
        setChanged()
    }

    private fun startConsumingSacrifices(level: Level) {
        val entities: MutableList<LivingEntity> = level.getEntitiesOfClass(LivingEntity::class.java, AABB(blockPos).inflate(4.0, 1.0, 4.0)) { true }
        val recipeEntities: List<EntityType<*>> = ritualRecipe!!.inputEntities

        tickCounter++

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
                    isRitualActive = true
                    shouldStartConsumingSacrifices = false
                    setChanged()
                }
            } else {
                resetRitual()
            }
        }
    }

    private fun startConsumingItems(level: Level) {
        val itemEntities: List<ItemEntity> = level.getEntities(
            EntityType.ITEM,
            AABB(blockPos).inflate(3.0, 1.0, 3.0)
        ) { true }

        val recipeItems = ritualRecipe?.inputItems ?: return
        if (tickCounter % 20 == 0) {
            for (itemEntity in itemEntities) {
                val stack = itemEntity.item

                val matchingRecipeItem = recipeItems.find { recipeItem ->
                    ItemStack.isSameItemSameComponents(stack, recipeItem)
                }

                if (matchingRecipeItem != null) {
                    addItemToInventory(items, stack)
                    level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
                    stack.shrink(1)
                    if (stack.isEmpty) {
                        itemEntity.remove(Entity.RemovalReason.DISCARDED)
                    }

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

    private fun resetRitual() {
        Containers.dropContents(level, blockPos, this)
        items = NonNullList.withSize(16, ItemStack.EMPTY)
        consumedSacrifices = mutableListOf()

        ritualRecipe = null
        ritualManager = RitualManager()
        shouldRun = false
        shouldStartConsumingItems = false
        shouldStartConsumingSacrifices = false
        isRitualActive = false
        tickCounter = 0
        ritualTickCounter = 0
        setChanged()
    }

    private fun itemsMatchRecipe(items: NonNullList<ItemStack>, recipeItems: List<ItemStack>): Boolean {
        return recipeItems.all { recipeItem ->
            items.any { inventoryItem ->
                ItemStack.isSameItemSameComponents(inventoryItem, recipeItem) && inventoryItem.count >= recipeItem.count
            }
        }
    }

    private fun addItemToInventory(items: NonNullList<ItemStack>, stack: ItemStack) {
        for (i in items.indices) {
            if (items[i].isEmpty) {
                items[i] = stack.copy()
                return
            } else if (ItemStack.isSameItemSameComponents(items[i], stack)) {
                items[i].grow(1)
                stack.shrink(1)
                if (stack.isEmpty) return
            }
        }
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (ritualRecipe == null) {
            val items: List<ItemEntity> = pPlayer.level().getEntities(EntityType.ITEM, AABB(blockPos).inflate(3.0, 0.0, 3.0)) { true }
            val entities: List<LivingEntity> = pPlayer.level().getEntitiesOfClass(LivingEntity::class.java, AABB(blockPos).inflate(4.0, 1.0, 4.0)) { true }

            val recipes = level?.recipeManager?.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())

            val validItemRecipes = recipes?.filter { recipe ->
                val recipeItems = recipe.value.inputItems
                recipeItems.all { recipeItem ->
                    items.any { itemEntity -> ItemStack.isSameItemSameComponents(itemEntity.item, recipeItem) }
                }
            }

            val validSacrifices = validItemRecipes?.filter { recipe ->
                val requiredSacrifices = recipe.value.inputEntities
                requiredSacrifices.all { requiredEntity ->
                    entities.any { entity -> entity.type == requiredEntity }
                }
            }

            if (validSacrifices != null && validSacrifices.isNotEmpty()) {
                ritualRecipe = validSacrifices[0].value
                shouldRun = true
                shouldStartConsumingItems = true
                setChanged()
                level?.playSound(pPlayer, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
                level?.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)

                ritualManager.runCommand(level!!, blockPos, this, RitualManager.CommandType.START)
            } else {
                level?.playSound(pPlayer, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                level?.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
            }
        }

        return super.onUseWithoutItem(pPlayer)
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

        consumedSacrifices = pTag.getList("ConsumedSacrifices", 8)
            .map { EntityType.byString(it.asString).get() }
            .toMutableList()
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
}