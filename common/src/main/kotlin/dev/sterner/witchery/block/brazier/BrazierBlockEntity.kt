package dev.sterner.witchery.block.brazier

import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_EXTRA_INPUT
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_INPUT
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_JAR
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_RESULT_1
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_RESULT_2
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_RESULT_3
import dev.sterner.witchery.block.distillery.DistilleryBlockEntity.Companion.SLOT_RESULT_4
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class BrazierBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BRAZIER.get(), blockPos, blockState),
    Container, AltarPowerConsumer, RecipeCraftingHolder, WorldlyContainer {

    var items: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)
    private val quickCheck = RecipeManager.createCheck(WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get())
    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation>()
    private var cachedAltarPos: BlockPos? = null



    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }

        if (items.isNotEmpty()) {
            val brazierSummonRecipe =
                quickCheck.getRecipeFor(MultipleItemRecipeInput(items), level).orElse(null)

            if (brazierSummonRecipe != null) {

            }
        }
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun receiveAltarPosition(blockPos: BlockPos) {

    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        if (pTag.contains("altarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
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


    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        this.setChanged()
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

    override fun setRecipeUsed(recipe: RecipeHolder<*>?) {
        if (recipe != null) {
            val resourceLocation = recipe.id()
            recipesUsed.addTo(resourceLocation, 1)
        }
    }

    override fun getRecipeUsed(): RecipeHolder<*>? {
        return null
    }
}