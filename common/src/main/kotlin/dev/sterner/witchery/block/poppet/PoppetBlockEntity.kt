package dev.sterner.witchery.block.poppet

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.*

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
            val handItem = player.mainHandItem

            if (!level.isClientSide && handItem.item is PoppetItem && level.getBlockState(targetPos).canBeReplaced()) {
                if (handItem.`is`(WitcheryTags.PLACEABLE_POPPETS)) {

                    if (handItem.has(WitcheryDataComponents.PLAYER_UUID.get()) || handItem.has(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())) {
                        if (handItem.has(WitcheryDataComponents.PLAYER_UUID.get())) {
                            val uuid = handItem.get(WitcheryDataComponents.PLAYER_UUID.get())
                            uuid?.let { level.getPlayerByUUID(it) }?.hurt(level.damageSources().playerAttack(player), 2f)
                        } else {
                            val id = handItem.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
                            id?.let { (level as ServerLevel).getEntity(UUID.fromString(id)) }?.hurt(level.damageSources().playerAttack(player), 2f)
                        }

                        if (handItem.`is`(WitcheryItems.VAMPIRIC_POPPET.get()) || handItem.`is`(WitcheryItems.VOODOO_POPPET.get())) {
                            handItem.damageValue += handItem.maxDamage / 10
                        } else {
                            handItem.damageValue += 1
                        }
                        if (handItem.damageValue >= player.mainHandItem.maxDamage) {
                            handItem.shrink(1)
                            return false
                        }
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

                    if (level is ServerLevel) {
                        PoppetDataAttachment.addPoppetData(level, PoppetData.Data(pos, be.poppetItemStack.copy()))
                    }

                    return true
                }
            }
            return false
        }
    }
}