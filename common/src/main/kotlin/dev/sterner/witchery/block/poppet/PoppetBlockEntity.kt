package dev.sterner.witchery.block.poppet

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class PoppetBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.POPPET.get(), blockPos, blockState) {

    var poppetItemStack = ItemStack.EMPTY

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        if (!poppetItemStack.isEmpty) {
            val itemTag = poppetItemStack.save(registries, CompoundTag())
            tag.put("Item", itemTag)
        }
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        ItemStack.parse(pRegistries, pTag.getCompound("Item")).ifPresent {
            poppetItemStack = it
        }
    }

    companion object {
        fun placePoppet(level: Level, pos: BlockPos, player: Player, direction: Direction): Boolean {
            val targetPos = if (direction.axis.isVertical) pos.relative(player.direction) else pos.relative(direction)

            if (player.mainHandItem.item is PoppetItem && level.getBlockState(targetPos).canBeReplaced()) {
                if (player.mainHandItem.`is`(WitcheryTags.PLACEABLE_POPPETS)) {

                    if (player.mainHandItem.has(WitcheryDataComponents.PLAYER_UUID.get())) {
                        val uuid = player.mainHandItem.get(WitcheryDataComponents.PLAYER_UUID.get())
                        uuid?.let { level.getPlayerByUUID(it) }?.hurt(level.damageSources().playerAttack(player), 2f)
                    }

                    val horizontalDirection = if (direction.axis.isVertical) {
                        player.direction
                    } else {
                        direction
                    }

                    val state = WitcheryBlocks.POPPET.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, horizontalDirection.opposite)
                    level.setBlockAndUpdate(targetPos, state)

                    val be = PoppetBlockEntity(targetPos, state)
                    be.poppetItemStack = player.mainHandItem.copy()
                    player.mainHandItem.shrink(1)
                    level.setBlockEntity(be)
                    return true
                }
            }
            return false
        }
    }
}