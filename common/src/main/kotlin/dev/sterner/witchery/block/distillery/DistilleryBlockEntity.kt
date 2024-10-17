package dev.sterner.witchery.block.distillery

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity
import dev.sterner.witchery.block.oven.OvenBlockEntity.Companion
import dev.sterner.witchery.menu.DistilleryMenu
import dev.sterner.witchery.menu.OvenMenu
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class DistilleryBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.DISTILLERY.get(), DistilleryBlock.STRUCTURE.get(), blockPos, blockState),
    Container, AltarPowerConsumer, RecipeCraftingHolder {

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
        val jarStack: ItemStack = items[SLOT_JAR]
        val inputStack: ItemStack = items[SLOT_INPUT]
        val inputStack2: ItemStack = items[SLOT_EXTRA_INPUT]
        val hasInput = !inputStack.isEmpty
        val hasJar = !jarStack.isEmpty

        if (hasInput && hasJar) {
            val distillingRecipe = quickCheck.getRecipeFor(MultipleItemRecipeInput(listOf(inputStack, inputStack2, jarStack)), level).orElse(null)
            if (distillingRecipe != null) {
                println("${distillingRecipe.id}")
            }

            if (canDistill(distillingRecipe, items, maxStackSize)) {
                cookingProgress++
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
        } else if (!hasAltarPower() && cookingProgress > 0) {
            cookingProgress = Mth.clamp(cookingProgress - OvenBlockEntity.BURN_COOL_SPEED, 0, cookingTotalTime)
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
            println("Recipe is null, cannot distill.")
            return false
        }

        val inputItems = recipe.value.inputItems
        val outputItems = recipe.value.outputItems
        println("Attempting to distill with recipe: ${recipe.id} with inputs: $inputItems and outputs: $outputItems")

        consumeInputItems(items, inputItems)

        return placeOutputItems(items, outputItems, maxStackSize)
    }

    private fun consumeInputItems(items: NonNullList<ItemStack>, inputItems: List<ItemStack>) {
        val inputStack = items[SLOT_INPUT]
        val extraInputStack = items[SLOT_EXTRA_INPUT]
        val jarStack = items[SLOT_JAR]

        println("Consuming inputs: Input1 = ${inputStack.count}, Input2 = ${extraInputStack.count}, Jar = ${jarStack.count}")

        if (ItemStack.isSameItemSameComponents(inputStack, inputItems[0]) && ItemStack.isSameItemSameComponents(extraInputStack, inputItems[1])) {
            println("Matching input slots with inputs.")
            inputStack.shrink(inputItems[0].count)
            extraInputStack.shrink(inputItems[1].count)
        } else if (ItemStack.isSameItemSameComponents(inputStack, inputItems[1]) && ItemStack.isSameItemSameComponents(extraInputStack, inputItems[0])) {
            println("Matching input slots reversed.")
            inputStack.shrink(inputItems[1].count)
            extraInputStack.shrink(inputItems[0].count)
        } else {
            println("Inputs do not match recipe requirements.")
        }

        jarStack.shrink(inputItems[2].count)
        println("Jar consumed, remaining jar count: ${jarStack.count}")
    }

    private fun placeOutputItems(
        items: NonNullList<ItemStack>,
        outputItems: List<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        val outputSlots = listOf(SLOT_RESULT_1, SLOT_RESULT_2, SLOT_RESULT_3, SLOT_RESULT_4).toMutableList()

        println("Attempting to place output items: $outputItems into result slots.")

        for (outputItem in outputItems) {
            var placed = false
            for (slot in outputSlots) {
                val resultSlot = items[slot]
                println("Checking if output item ${outputItem.item} can fit into slot $slot with existing stack ${resultSlot.item} (Count: ${resultSlot.count})")

                if (canFitInSlot(outputItem, resultSlot, maxStackSize)) {
                    if (resultSlot.isEmpty) {
                        println("Slot $slot is empty. Placing output item ${outputItem.item}.")
                        items[slot] = outputItem.copy()
                    } else {
                        println("Growing stack in slot $slot with ${outputItem.count} items.")
                        resultSlot.grow(outputItem.count)
                    }
                    placed = true
                    break
                }
            }

            if (!placed) {
                println("Failed to place output item ${outputItem.item}.")
                return false
            }
        }

        return true
    }

    private fun checkInputs(inputItems: List<ItemStack>, items: NonNullList<ItemStack>): Boolean {
        if (inputItems.size != 3) {
            println("Input size mismatch. Expected 3, got ${inputItems.size}.")
            return false
        }

        val jarStack = items[SLOT_JAR]
        val firstInput = inputItems[0]
        val secondInput = inputItems[1]

        if (!ItemStack.isSameItemSameComponents(jarStack, inputItems[2])) {
            println("Jar mismatch: Expected ${inputItems[2].item}, got ${jarStack.item}.")
            return false
        }

        val inputStack = items[SLOT_INPUT]
        val extraInputStack = items[SLOT_EXTRA_INPUT]
        val inputsMatch = (
                (ItemStack.isSameItemSameComponents(inputStack, firstInput) && ItemStack.isSameItemSameComponents(extraInputStack, secondInput)) ||
                        (ItemStack.isSameItemSameComponents(inputStack, secondInput) && ItemStack.isSameItemSameComponents(extraInputStack, firstInput))
                )

        println("Input items ${if (inputsMatch) "match" else "do not match"} the recipe.")
        return inputsMatch
    }

    private fun checkOutputs(
        outputItems: List<ItemStack>,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        val outputSlots = listOf(SLOT_RESULT_1, SLOT_RESULT_2, SLOT_RESULT_3, SLOT_RESULT_4).map { items[it] }.toMutableList()

        println("Checking if output items can fit into slots.")

        for (outputItem in outputItems) {
            var fits = false
            for (i in outputSlots.indices) {
                val slot = outputSlots[i]
                println("Checking if ${outputItem.item} (Count: ${outputItem.count}) fits in slot with ${slot.item} (Count: ${slot.count}).")

                if (canFitInSlot(outputItem, slot, maxStackSize)) {
                    fits = true
                    outputSlots[i] = slot.copy().apply { grow(outputItem.count) }
                    println("Output item ${outputItem.item} fits in slot.")
                    break
                }
            }

            if (!fits) {
                println("Output item ${outputItem.item} does not fit in any available slots.")
                return false
            }
        }

        return true
    }

    private fun hasAltarPower(): Boolean {
        return true //TODO
    }

    private fun canDistill(
        recipe: RecipeHolder<DistilleryCraftingRecipe>?,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        if (recipe == null) return false

        val inputItems = recipe.value.inputItems
        val outputItems = recipe.value.outputItems

        if (!checkInputs(inputItems, items)) return false

        return checkOutputs(outputItems, items, maxStackSize)
    }

    private fun canFitInSlot(resultStack: ItemStack, outputSlot: ItemStack, maxStackSize: Int): Boolean {
        if (outputSlot.isEmpty) {
            return true
        } else if (ItemStack.isSameItemSameComponents(outputSlot, resultStack)) {
            return outputSlot.count + resultStack.count <= Math.min(maxStackSize, outputSlot.maxStackSize)
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
                return Component.translatable("container.witchery.oven_menu")
            }

            override fun saveExtraData(buf: FriendlyByteBuf) {
                buf.writeBlockPos(blockPos)
            }
        })
    }

    override fun receiveAltarPosition(blockPos: BlockPos) {

    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        this.cookingProgress = pTag.getShort("CookTime").toInt()
        this.cookingTotalTime = pTag.getShort("CookTimeTotal").toInt()
        if(pTag.contains("altarPos")){
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
        if ((slot == SLOT_INPUT || slot == SLOT_EXTRA_INPUT || slot ==  SLOT_JAR) && !bl) {
            this.cookingTotalTime = getTotalCookTime(this.level!!)
            this.cookingProgress = 0
            this.setChanged()
        }
    }

    private fun getTotalCookTime(level: Level): Int {
        val singleRecipeInput = MultipleItemRecipeInput(listOf(getItem(SLOT_INPUT), getItem(SLOT_EXTRA_INPUT), getItem(SLOT_JAR)))

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
        const val DATA_COOKING_PROGRESS: Int = 2
        const val DATA_COOKING_TOTAL_TIME: Int = 3
        const val NUM_DATA_VALUES: Int = 2
        const val BURN_TIME_STANDARD: Int = 200
        const val BURN_COOL_SPEED: Int = 2
    }
}