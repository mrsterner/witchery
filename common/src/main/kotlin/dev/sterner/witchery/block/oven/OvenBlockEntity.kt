package dev.sterner.witchery.block.oven

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.menu.OvenMenu
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.state.BlockState


class OvenBlockEntity(blockPos: BlockPos, blockState: BlockState
) : WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.OVEN.get(), blockPos, blockState), Container, WorldlyContainer {

    var litTime: Int = 0
    var litDuration: Int = 0
    var cookingProgress: Int = 0
    var cookingTotalTime: Int = 0
    var items: NonNullList<ItemStack> = NonNullList.withSize(5, ItemStack.EMPTY)

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)
        if (!level.isClientSide) {
            val bl: Boolean = isLit()
            var bl2 = false
            if (isLit()) {
                litTime--
            }

            val itemStack: ItemStack = items.get(1)
            val itemStack2: ItemStack = items.get(0)
            val bl3 = !itemStack2.isEmpty
            val bl4 = !itemStack.isEmpty
            if (isLit() || bl4 && bl3) {
                val recipeHolder = if (bl3) {
                    quickCheck.getRecipeFor(SingleRecipeInput(itemStack2), level).orElse(null)
                } else {
                    null
                }

                val i: Int = getMaxStackSize()
                if (!isLit() && AbstractFurnaceBlockEntity.canBurn(
                        level.registryAccess(),
                        recipeHolder,
                        items,
                        i
                    )
                ) {
                    litTime = getBurnDuration(itemStack)
                    litDuration = litTime
                    if (isLit()) {
                        bl2 = true
                        if (bl4) {
                            val item = itemStack.item
                            itemStack.shrink(1)
                            if (itemStack.isEmpty) {
                                val item2 = item.craftingRemainingItem
                                items.set(1, if (item2 == null) ItemStack.EMPTY else ItemStack(item2))
                            }
                        }
                    }
                }

                if (isLit() && AbstractFurnaceBlockEntity.canBurn(
                        level.registryAccess(),
                        recipeHolder,
                        items,
                        i
                    )
                ) {
                    cookingProgress++
                    if (cookingProgress == cookingTotalTime) {
                        cookingProgress = 0
                        cookingTotalTime = AbstractFurnaceBlockEntity.getTotalCookTime(level, this)
                        if (AbstractFurnaceBlockEntity.burn(
                                level.registryAccess(),
                                recipeHolder,
                                items,
                                i
                            )
                        ) {
                            setRecipeUsed(recipeHolder)
                        }

                        bl2 = true
                    }
                } else {
                    cookingProgress = 0
                }
            } else if (!isLit() && cookingProgress > 0) {
                cookingProgress =
                    Mth.clamp(cookingProgress - 2, 0, cookingTotalTime)
            }

            if (bl != isLit()) {
                bl2 = true
                state = state.setValue(AbstractFurnaceBlock.LIT, isLit())
                level.setBlock(pos, state, 3)
            }

            if (bl2) {
                setChanged(level, pos, state)
            }
        }
    }

    private fun isLit(): Boolean {
        return this.litTime > 0
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {

        if (pPlayer is ServerPlayer) {
            openMenu(pPlayer)
            return InteractionResult.SUCCESS
        }

        return super.onUseWithoutItem(pPlayer)
    }

    private fun openMenu(player: ServerPlayer){
        MenuRegistry.openExtendedMenu(player, object : ExtendedMenuProvider {
            override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                saveExtraData(buf)
                return OvenMenu(id, inventory, buf)
            }

            override fun getDisplayName(): Component {
                return Component.translatable("container.witchery.oven_menu")
            }

            override fun saveExtraData(buf: FriendlyByteBuf?) {

            }
        })
    }

    override fun loadAdditional(tag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(tag, pRegistries)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, this.items, pRegistries)
        this.litTime = tag.getShort("BurnTime").toInt()
        this.cookingProgress = tag.getShort("CookTime").toInt()
        this.cookingTotalTime = tag.getShort("CookTimeTotal").toInt()
        this.litDuration = this.getBurnDuration(items[SLOT_FUEL])
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putShort("BurnTime", litTime.toShort())
        tag.putShort("CookTime", cookingProgress.toShort())
        tag.putShort("CookTimeTotal", cookingTotalTime.toShort())
        ContainerHelper.saveAllItems(tag, this.items, registries)
    }

    private fun getBurnDuration(fuel: ItemStack): Int {
        if (fuel.isEmpty) {
            return 0
        } else {
            val item = fuel.item
            return AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0) as Int
        }
    }

    companion object {
        const val SLOT_INPUT: Int = 0
        const val SLOT_EXTRA_INPUT: Int = 1
        const val SLOT_RESULT: Int = 2
        const val SLOT_EXTRA_RESULT: Int = 3
        const val SLOT_FUEL: Int = 4

        val SLOTS_FOR_UP: IntArray = intArrayOf(SLOT_INPUT, SLOT_EXTRA_INPUT)
        val SLOTS_FOR_DOWN: IntArray = intArrayOf(SLOT_RESULT, SLOT_EXTRA_RESULT)
        val SLOTS_FOR_SIDES: IntArray = intArrayOf(SLOT_FUEL)
    }

    override fun clearContent() {
        items.clear()
    }

    override fun getContainerSize(): Int {
        return items.size
    }

    override fun isEmpty(): Boolean {
        for (itemStack in items) {
            if (!itemStack.isEmpty) {
                return false
            }
        }

        return true
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
        val itemStack = items[slot]
        val bl = !stack.isEmpty && ItemStack.isSameItemSameComponents(itemStack, stack)
        items[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        if (slot == SLOT_INPUT && !bl) {
            this.cookingProgress = 0
            this.setChanged()
        }

        if (slot == SLOT_EXTRA_INPUT) {
            this.setChanged()
        }
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun getSlotsForFace(side: Direction): IntArray {
        return if (side == Direction.DOWN) {
            SLOTS_FOR_DOWN
        } else {
            if (side == Direction.UP) SLOTS_FOR_UP else SLOTS_FOR_SIDES
        }
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean {
        return this.canPlaceItem(index, itemStack)
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean {
        return if (direction == Direction.DOWN && index == 4) stack.`is`(Items.WATER_BUCKET) || stack.`is`(Items.BUCKET) else true
    }
}