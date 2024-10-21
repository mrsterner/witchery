package dev.sterner.witchery.block.spining_wheel

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.menu.DistilleryMenu
import dev.sterner.witchery.menu.SpinningWheelMenu
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
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
import kotlin.math.min

class SpinningWheelBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.SPINNING_WHEEL.get(), blockPos, blockState), Container,
    AltarPowerConsumer, RecipeCraftingHolder {

    var items: NonNullList<ItemStack> = NonNullList.withSize(7, ItemStack.EMPTY)
    var cookingProgress: Int = 0
    var cookingTotalTime: Int = 0
    private val quickCheck = RecipeManager.createCheck(WitcheryRecipeTypes.SPINNING_WHEEL_RECIPE_TYPE.get())
    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation>()
    private var cachedAltarPos: BlockPos? = null

    val dataAccess: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                DATA_COOKING_PROGRESS -> this@SpinningWheelBlockEntity.cookingProgress
                DATA_COOKING_TOTAL_TIME -> this@SpinningWheelBlockEntity.cookingTotalTime
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                DATA_COOKING_PROGRESS -> this@SpinningWheelBlockEntity.cookingProgress = value
                DATA_COOKING_TOTAL_TIME -> this@SpinningWheelBlockEntity.cookingTotalTime = value
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
        val inputStack: ItemStack = items[SLOT_INPUT]
        val inputStack2: ItemStack = items[SLOT_EXTRA_INPUT_1]
        val inputStack3: ItemStack = items[SLOT_EXTRA_INPUT_2]
        val inputStack4: ItemStack = items[SLOT_EXTRA_INPUT_3]
        val hasInput = !inputStack.isEmpty

        if (hasInput) {
            val spinningRecipe = quickCheck.getRecipeFor(MultipleItemRecipeInput(listOf(inputStack, inputStack2, inputStack3, inputStack4).filter { !it.isEmpty }), level).orElse(null)

            if (canSpin(spinningRecipe, items, maxStackSize)) {
                cookingProgress++
                if (cookingProgress % 20 == 0) {
                    consumeAltarPower(level, spinningRecipe.value)
                }
                if (cookingProgress == cookingTotalTime) {
                    cookingProgress = 0
                    cookingTotalTime = getTotalCookTime(level)

                    if (spin(spinningRecipe, items, maxStackSize)) {
                        recipeUsed = spinningRecipe
                    }

                    shouldUpdateBlock = true
                }
            } else {
                cookingProgress = 0
            }

        } else if (cookingProgress > 0) {
            cookingProgress = Mth.clamp(cookingProgress - BURN_COOL_SPEED, 0, cookingTotalTime)
        }

        if (shouldUpdateBlock) {
            setChanged(level, pos, state)
        }
    }

    private fun spin(spinningRecipe: RecipeHolder<SpinningWheelRecipe>?, items: NonNullList<ItemStack>, maxStackSize: Int): Boolean {
        if (spinningRecipe == null) {
            return false
        }

        val inputItems = spinningRecipe.value.inputItems
        val outputItems = spinningRecipe.value.outputItem

        consumeInputItems(items, inputItems)

        val success = placeOutputItems(items, outputItems, maxStackSize)
        if (success) {
            consumeAltarPower(level!!, spinningRecipe.value)
        }
        return success
    }

    private fun placeOutputItems(
        items: NonNullList<ItemStack>,
        outputItem: ItemStack,
        maxStackSize: Int
    ): Boolean {
        val outputSlots = listOf(SLOT_RESULT).toMutableList()

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
        return true
    }

    private fun consumeInputItems(
        items: NonNullList<ItemStack>,
        inputItems: List<ItemStack>
    ) {
        val inputStacks = listOf(
            items[SLOT_INPUT],
            items[SLOT_EXTRA_INPUT_1],
            items[SLOT_EXTRA_INPUT_2],
            items[SLOT_EXTRA_INPUT_3]
        ).filter { !it.isEmpty }

        val sortedInputItems = inputItems.sortedBy { it.item.`arch$registryName`().toString() }
        val sortedInputStacks = inputStacks.sortedBy { it.item.`arch$registryName`().toString() }

        sortedInputItems.zip(sortedInputStacks).forEach { (inputItem, inputStack) ->
            inputStack.shrink(inputItem.count)
        }
    }


    private fun canSpin(spinningRecipe: RecipeHolder<SpinningWheelRecipe>?, items: NonNullList<ItemStack>, maxStackSize: Int): Boolean {
        if (spinningRecipe == null) return false

        if (cachedAltarPos == null && level is ServerLevel) {

            cachedAltarPos = getAltarPos(level as ServerLevel, blockPos)
            setChanged()
        }

        if (!hasEnoughAltarPower(level!!, spinningRecipe.value)) {
            return false
        }

        val inputItems = spinningRecipe.value.inputItems
        val outputItems = spinningRecipe.value.outputItem

        if (!checkInputs(inputItems, items)) return false

        return checkOutputs(outputItems, items, maxStackSize)
    }

    private fun checkInputs(
        inputItems: List<ItemStack>,
        items: NonNullList<ItemStack>,
    ): Boolean {
        val inputStacks = listOf(
            items[SLOT_INPUT],
            items[SLOT_EXTRA_INPUT_1],
            items[SLOT_EXTRA_INPUT_2],
            items[SLOT_EXTRA_INPUT_3]
        ).filter { !it.isEmpty }

        val sortedInputItems = inputItems.sortedBy { it.item.`arch$registryName`().toString() }
        val sortedInputStacks = inputStacks.sortedBy { it.item.`arch$registryName`().toString() }

        return sortedInputItems.zip(sortedInputStacks).all { (inputItem, inputStack) ->
            ItemStack.isSameItem(inputItem, inputStack)
        }
    }

    private fun checkOutputs(
        outputItem: ItemStack,
        items: NonNullList<ItemStack>,
        maxStackSize: Int
    ): Boolean {
        val outputSlots =
            listOf(SLOT_RESULT).map { items[it] }.toMutableList()

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

        return true
    }

    private fun canFitInSlot(resultStack: ItemStack, outputSlot: ItemStack, maxStackSize: Int): Boolean {
        if (outputSlot.isEmpty) {
            return true
        } else if (ItemStack.isSameItemSameComponents(outputSlot, resultStack)) {
            return outputSlot.count + resultStack.count <= min(maxStackSize, outputSlot.maxStackSize)
        }

        return false
    }

    private fun hasEnoughAltarPower(level: Level, recipe: SpinningWheelRecipe): Boolean {
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

    private fun consumeAltarPower(level: Level, recipe: SpinningWheelRecipe): Boolean {
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
                return SpinningWheelMenu(id, inventory, buf)
            }

            override fun getDisplayName(): Component {
                return Component.translatable("container.witchery.spinning_wheel")
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
        if ((slot == SLOT_INPUT || slot == SLOT_EXTRA_INPUT_1 || slot == SLOT_EXTRA_INPUT_2 || slot == SLOT_EXTRA_INPUT_3) && !bl) {
            this.cookingTotalTime = getTotalCookTime(this.level!!)
            this.cookingProgress = 0
            this.setChanged()
        }
    }

    private fun getTotalCookTime(level: Level): Int {
        val singleRecipeInput = MultipleItemRecipeInput(listOf(getItem(SLOT_INPUT), getItem(SLOT_EXTRA_INPUT_1), getItem(SLOT_EXTRA_INPUT_2), getItem(SLOT_EXTRA_INPUT_3)).filter { !it.isEmpty })

        val cookQuickTime = quickCheck
            .getRecipeFor(singleRecipeInput, level)
            .map { recipeHolder: RecipeHolder<SpinningWheelRecipe?> -> (recipeHolder.value() as SpinningWheelRecipe).cookingTime }
            .orElse(BURN_TIME_STANDARD)

        return cookQuickTime.coerceAtLeast(1) // Ensure the cooking time is at least 1 tick
    }

    override fun receiveAltarPosition(blockPos: BlockPos) {

    }

    companion object {
        const val SLOT_INPUT: Int = 0
        const val SLOT_EXTRA_INPUT_1: Int = 1
        const val SLOT_EXTRA_INPUT_2: Int = 2
        const val SLOT_EXTRA_INPUT_3: Int = 3

        const val SLOT_RESULT: Int = 4

        const val DATA_COOKING_PROGRESS: Int = 0
        const val DATA_COOKING_TOTAL_TIME: Int = 1
        const val NUM_DATA_VALUES: Int = 2
        const val BURN_TIME_STANDARD: Int = 200
        const val BURN_COOL_SPEED: Int = 2
    }
}