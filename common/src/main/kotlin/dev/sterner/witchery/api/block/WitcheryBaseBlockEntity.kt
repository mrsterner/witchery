package dev.sterner.witchery.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState


open class WitcheryBaseBlockEntity(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos, blockState: BlockState
) : BlockEntity(blockEntityType, blockPos, blockState), CustomUpdateTagHandlingBlockEntity,
    CustomDataPacketHandlingBlockEntity {

    private var initialized = false

    open fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        if (!initialized) {
            initialized = true
            init(level, pos, blockState)
        }
    }

    open fun init(level: Level, pos: BlockPos, state: BlockState) {

    }

    open fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        return InteractionResult.PASS
    }

    open fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        return ItemInteractionResult.FAIL
    }

    open fun onBreak(player: Player) {

    }

    open fun onNeighborUpdate(pState: BlockState, pPos: BlockPos, pFromPos: BlockPos) {

    }

    open fun onUse(pPlayer: Player, pHand: InteractionHand): ItemInteractionResult {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    open fun onPlace(pPlacer: LivingEntity?, pStack: ItemStack) {

    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun setChanged() {
        if (level is ServerLevel) {
            level!!.blockEntityChanged(blockPos)
            level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)
        }

        super.setChanged()
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return this.saveWithoutMetadata(registries)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        initialized = false
        super.loadAdditional(pTag, pRegistries)
    }

    override fun onDataPacket(
        connection: Connection?,
        packet: ClientboundBlockEntityDataPacket?,
        registryAccess: RegistryAccess.Frozen
    ) {
        val tag = packet?.tag
        loadAdditional(tag ?: CompoundTag(), registryAccess)
    }
}