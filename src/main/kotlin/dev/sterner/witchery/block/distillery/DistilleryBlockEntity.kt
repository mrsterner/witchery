package dev.sterner.witchery.block.distillery

import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import team.lodestar.lodestone.systems.multiblock.MultiBlockCoreEntity
import kotlin.math.min

class DistilleryBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(
        WitcheryBlockEntityTypes.DISTILLERY.get(),
        DistilleryBlock.STRUCTURE.get(),
        blockPos,
        blockState
    ),
    Container, AltarPowerConsumer, RecipeCraftingHolder, WorldlyContainer {

    var items: NonNullList<ItemStack> = NonNullList.withSize(7, ItemStack.EMPTY)
    var cookingProgress: Int = 0
    var cookingTotalTime: Int = 0
    private val quickCheck = RecipeManager.createCheck(WitcheryRecipeTypes.DISTILLERY_RECIPE_TYPE.get())
    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation>()
    private var cachedAltarPos: BlockPos? = null

    val dataAccess: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                DATA_COOKING_PROGRESS -> this@DistilleryBlockEntity.cookingProgress
                DATA_COOKING_TOTAL_TIME -> this@DistilleryBlockEntity.cookingTotalTime
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                DATA_COOKING_PROGRESS -> this@DistilleryBlockEntity.cookingProgress = value
                DATA_COOKING_TOTAL_TIME -> this@DistilleryBlockEntity.cookingTotalTime = value
            }
        }

        override fun getCount(): Int {
            return NUM_DATA_VALUES
        }
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }
        var shouldUpdateBlock = false
        var isProcessing = false

        val jarStack: ItemStack = items[SLOT_JAR]
        val inputStack: ItemStack = items[SLOT_INPUT]
        val inputStack2: ItemStack = items[SLOT_EXTRA_INPUT]
        val hasInput = !inputStack.isEmpty
        val hasJar = !jarStack.isEmpty

        if (hasInput && hasJar) {
            val distillingRecipe =
                quickCheck.getRecipeFor(MultipleItemRecipeInput(listOf(inputStack, inputStack2)), level).orElse(null)

            if (canDistill(distillingRecipe, items, maxStackSize)) {
                cookingProgress++
                isProcessing = true // Mark that the oven is processing a recipe

                if (cookingProgress % 20 == 0) {
                    consumeAltarPower(level, distillingRecipe.value)
                }

                if (cookingProgress == cookingTotalTime) {
                    cookingProgress = 0
                    cookingTotalTime = getTotalCookTime(level)

                    if (distill(distillingRecipe, items, maxStackSize)) {
                        recipeUsed = distillingRecipe
                    }

                    shouldUpdateBlock = true
                }
            } else {
                cookingProgress = 0
            }
        } else if (cookingProgress > 0) {
            cookingProgress = Mth.clamp(cookingProgress - OvenBlockEntity.BURN_COOL_SPEED, 0, cookingTotalTime)
        }

        if (isProcessing && !state.getValue(BlockStateProperties.LIT)) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true))
        } else if (!isProcessing && state.getValue(BlockStateProperties.LIT)) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, false))
        }

        if (shouldUpdateBlock) {
            setChanged(level, pos, state)
        }
    }

    private fun distill(
        recipe: RecipeHolder<DistilleryCraftingRecipe>?,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        if (recipe == null) {
            return false
        }

        val inputItems = recipe.value.inputItems
        val outputItems = recipe.value.outputItems

        consumeInputItems(items, inputItems, recipe.value)

        val success = placeOutputItems(items, outputItems, maxStackSize)
        if (success) {
            consumeAltarPower(level!!, recipe.value)
        }
        return success
    }

    private fun consumeInputItems(
        items: NonNullList<ItemStack>,
        inputItems: List<ItemStack>,
        recipe: DistilleryCraftingRecipe
    ) {
        val inputStack = items[SLOT_INPUT]
        val extraInputStack = items[SLOT_EXTRA_INPUT]
        val jarStack = items[SLOT_JAR]

        if (inputItems.size == 1) {
            val firstInput = inputItems[0]
            if (ItemStack.isSameItemSameComponents(inputStack, firstInput)) {
                inputStack.shrink(firstInput.count)
            }
        } else if (inputItems.size >= 2) {
            if (ItemStack.isSameItemSameComponents(inputStack, inputItems[0]) && ItemStack.isSameItemSameComponents(
                    extraInputStack,
                    inputItems[1]
                )
            ) {
                inputStack.shrink(inputItems[0].count)
                extraInputStack.shrink(inputItems[1].count)
            } else if (ItemStack.isSameItemSameComponents(inputStack, inputItems[1]) && ItemStack.isSameItemSameComponents(
                    extraInputStack,
                    inputItems[0]
                )
            ) {
                inputStack.shrink(inputItems[1].count)
                extraInputStack.shrink(inputItems[0].count)
            }
        }

        jarStack.shrink(recipe.jarConsumption)
    }

    private fun placeOutputItems(
        items: NonNullList<ItemStack>,
        outputItems: List<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        val outputSlots = listOf(SLOT_RESULT_1, SLOT_RESULT_2, SLOT_RESULT_3, SLOT_RESULT_4).toMutableList()

        for (outputItem in outputItems) {
            var placed = false
            for (slot in outputSlots) {
                val resultSlot = items[slot]

                if (canFitInSlot(outputItem, resultSlot, maxStackSize)) {
                    if (resultSlot.isEmpty) {
                        items[slot] = outputItem.copy()
                    } else {
                        resultSlot.grow(outputItem.count)
                    }
                    placed = true
                    break
                }
            }

            if (!placed) {
                return false
            }
        }
        return true
    }


    private fun checkInputs(
        inputItems: List<ItemStack>,
        items: NonNullList<ItemStack>,
        recipe: DistilleryCraftingRecipe
    ): Boolean {

        val jarStack = items[SLOT_JAR]

        if (jarStack.count < recipe.jarConsumption) {
            return false
        }

        if (inputItems.isEmpty()) {
            return false
        }

        val firstInput = inputItems[0]
        val inputStack = items[SLOT_INPUT]
        val extraInputStack = items[SLOT_EXTRA_INPUT]

        if (inputItems.size == 1) {
            return ItemStack.isSameItemSameComponents(inputStack, firstInput) && extraInputStack.isEmpty
        }

        val secondInput = inputItems[1]
        val inputsMatch = (
                (ItemStack.isSameItemSameComponents(inputStack, firstInput) && ItemStack.isSameItemSameComponents(
                    extraInputStack,
                    secondInput
                )) ||
                        (ItemStack.isSameItemSameComponents(
                            inputStack,
                            secondInput
                        ) && ItemStack.isSameItemSameComponents(extraInputStack, firstInput))
                )

        return inputsMatch
    }

    private fun checkOutputs(
        outputItems: List<ItemStack>,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        val outputSlots =
            listOf(SLOT_RESULT_1, SLOT_RESULT_2, SLOT_RESULT_3, SLOT_RESULT_4).map { items[it] }.toMutableList()

        for (outputItem in outputItems) {
            var fits = false
            for (i in outputSlots.indices) {
                val slot = outputSlots[i]

                if (canFitInSlot(outputItem, slot, maxStackSize)) {
                    fits = true
                    outputSlots[i] = slot.copy().apply { grow(outputItem.count) }
                    break
                }
            }

            if (!fits) {
                return false
            }
        }

        return true
    }

    private fun hasEnoughAltarPower(level: Level, recipe: DistilleryCraftingRecipe): Boolean {
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

    private fun canDistill(
        recipe: RecipeHolder<DistilleryCraftingRecipe>?,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        if (recipe == null) return false

        if (cachedAltarPos == null && level is ServerLevel) {

            cachedAltarPos = getAltarPos(level as ServerLevel, blockPos)
            setChanged()
        }

        if (!hasEnoughAltarPower(level!!, recipe.value)) {
            return false
        }

        val inputItems = recipe.value.inputItems
        val outputItems = recipe.value.outputItems

        if (!checkInputs(inputItems, items, recipe.value)) return false

        return checkOutputs(outputItems, items, maxStackSize)
    }

    private fun consumeAltarPower(level: Level, recipe: DistilleryCraftingRecipe): Boolean {
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

    private fun canFitInSlot(resultStack: ItemStack, outputSlot: ItemStack, maxStackSize: Int): Boolean {
        if (outputSlot.isEmpty) {
            return true
        } else if (ItemStack.isSameItemSameComponents(outputSlot, resultStack)) {
            return outputSlot.count + resultStack.count <= min(maxStackSize, outputSlot.maxStackSize)
        }

        return false
    }

    override fun setRecipeUsed(recipe: RecipeHolder<*>?) {
        if (recipe != null) {
            val resourceLocation = recipe.id()
            recipesUsed.addTo(resourceLocation, 1)
        }
    }

    override fun getRecipeUsed(): RecipeHolder<*>? {
        return null
    }

    override fun onUseWithoutItem(player: Player): InteractionResult {
        if (player is ServerPlayer) {
            openMenu(player)
            return InteractionResult.SUCCESS
        }
        return super.onUseWithoutItem(player)
    }

    private fun openMenu(player: ServerPlayer) {
        MenuRegistry.openExtendedMenu(player, object : ExtendedMenuProvider {
            override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                saveExtraData(buf)
                return DistilleryMenu(id, inventory, buf)
            }

            override fun getDisplayName(): Component {
                return Component.translatable("container.witchery.distillery")
            }

            override fun saveExtraData(buf: FriendlyByteBuf) {
                buf.writeBlockPos(blockPos)
            }
        })
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        this.cookingProgress = pTag.getShort("CookTime").toInt()
        this.cookingTotalTime = pTag.getShort("CookTimeTotal").toInt()
        if (pTag.contains("altarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putShort("CookTime", cookingProgress.toShort())
        tag.putShort("CookTimeTotal", cookingTotalTime.toShort())
        ContainerHelper.saveAllItems(tag, this.items, registries)
        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }
    }

    override fun isEmpty(): Boolean {
        for (itemStack in this.items) {
            if (!itemStack.isEmpty) {
                return false
            }
        }

        return true
    }

    override fun getItem(slot: Int): ItemStack {
        return this.items[slot]
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

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun getSlotsForFace(side: Direction) =
        (0..<items.size).toList().toIntArray()

    override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, direction: Direction?): Boolean {
        return (index == SLOT_JAR && stack.`is`(WitcheryItems.JAR.get())) || (index == SLOT_INPUT || index == SLOT_EXTRA_INPUT)
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean {
        return index == SLOT_RESULT_1 || index == SLOT_RESULT_2 || index == SLOT_RESULT_3 || index == SLOT_RESULT_4
    }

    override fun clearContent() {
        this.items.clear()
    }

    override fun getContainerSize(): Int {
        return items.size
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        val itemStack = items[slot]
        val bl = !stack.isEmpty && ItemStack.isSameItemSameComponents(itemStack, stack)
        items[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        if ((slot == SLOT_INPUT || slot == SLOT_EXTRA_INPUT || slot == SLOT_JAR) && !bl) {
            this.cookingTotalTime = getTotalCookTime(this.level!!)
            this.cookingProgress = 0
            this.setChanged()
        }
    }

    private fun getTotalCookTime(level: Level): Int {
        val singleRecipeInput = MultipleItemRecipeInput(listOf(getItem(SLOT_INPUT), getItem(SLOT_EXTRA_INPUT)))

        val cookQuickTime = quickCheck
            .getRecipeFor(singleRecipeInput, level)
            .map { recipeHolder: RecipeHolder<DistilleryCraftingRecipe?> -> (recipeHolder.value() as DistilleryCraftingRecipe).cookingTime }
            .orElse(BURN_TIME_STANDARD)

        return cookQuickTime.coerceAtLeast(1) // Ensure the cooking time is at least 1 tick
    }

    companion object {
        const val SLOT_INPUT: Int = 0
        const val SLOT_EXTRA_INPUT: Int = 1
        const val SLOT_JAR: Int = 2

        const val SLOT_RESULT_1: Int = 3
        const val SLOT_RESULT_2: Int = 4
        const val SLOT_RESULT_3: Int = 5
        const val SLOT_RESULT_4: Int = 6

        val SLOTS_FOR_UP: IntArray = intArrayOf(0)
        val SLOTS_FOR_DOWN: IntArray = intArrayOf(2, 1)
        val SLOTS_FOR_SIDES: IntArray = intArrayOf(1)
        const val DATA_COOKING_PROGRESS: Int = 0
        const val DATA_COOKING_TOTAL_TIME: Int = 1
        const val NUM_DATA_VALUES: Int = 2
        const val BURN_TIME_STANDARD: Int = 200
        const val BURN_COOL_SPEED: Int = 2
    }
}