package dev.sterner.witchery.block.phylactery

import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.data_attachment.PhylacteryLevelDataAttachment
import dev.sterner.witchery.data_attachment.SoulPoolPlayerAttachment
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractCandleBlock
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class PhylacteryBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState
) : WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.PHYLACTERY.get(), blockPos, blockState) {

    var ownerUUID: UUID? = null
        private set
    var ownerName: String? = null
        private set
    var hasSoul: Boolean = false
        private set

    override fun onUseWithItem(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult {
        if (pPlayer is ServerPlayer) {
            if (pStack.`is`(WitcheryItems.SPECTRAL_DUST.get())) {
                val serverLevel = pPlayer.serverLevel()

                val current = PhylacteryLevelDataAttachment.listPhylacteries(serverLevel)
                    .find { it.pos == blockPos }

                if (current != null && !current.hasSoul) {
                    PhylacteryLevelDataAttachment.setPhylacteryHasSoul(serverLevel, blockPos, true)
                    this.hasSoul = true
                    setChanged()

                    level?.setBlockAndUpdate(blockPos, blockState.setValue(AbstractCandleBlock.LIT, true))
                    serverLevel.sendBlockUpdated(blockPos, blockState, blockState, 3)

                    val pool = SoulPoolPlayerAttachment.getData(pPlayer)
                    SoulPoolPlayerAttachment.setData(
                        pPlayer,
                        pool.copy(soulPool = pool.soulPool + 1)
                    )

                    pPlayer.displayClientMessage(
                        Component.literal("Soul bound to phylactery."),
                        true
                    )
                }
            }
        }
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun setLevel(level: Level) {
        super.setLevel(level)
        if (level is ServerLevel) {
            PhylacteryLevelDataAttachment.applyPendingPhylacteryChangesForPos(
                level,
                blockPos
            )

            val record = PhylacteryLevelDataAttachment.listPhylacteries(level)
                .find { it.pos == blockPos }
            if (record != null) {
                this.hasSoul = record.hasSoul
                this.ownerUUID = record.owner
            }
        }
    }

    override fun onPlace(pPlacer: LivingEntity?, pStack: ItemStack) {
        super.onPlace(pPlacer, pStack)

        if (pPlacer is ServerPlayer) {
            val level = pPlacer.serverLevel()
            val lichLevel = AfflictionPlayerAttachment.getData(pPlacer).getLevel(AfflictionTypes.LICHDOM)

            val maxSouls = when {
                lichLevel in 2..3 -> 1
                lichLevel in 4..5 -> 2
                lichLevel >= 6 -> 3
                else -> 0
            }

            val existing = PhylacteryLevelDataAttachment.listPhylacteriesForPlayer(level, pPlacer.uuid)

            if (existing.size >= maxSouls) {
                level.destroyBlock(blockPos, true, pPlacer)
                pPlacer.displayClientMessage(
                    Component.literal("You cannot bind more phylacteries."),
                    true
                )
                return
            }

            this.ownerUUID = pPlacer.uuid
            this.ownerName = pPlacer.name.string
            this.hasSoul = false
            setChanged()

            PhylacteryLevelDataAttachment.addPhylactery(
                level,
                PhylacteryLevelDataAttachment.PhylacteryRecord(
                    pos = this.blockPos,
                    owner = pPlacer.uuid,
                    hasSoul = false
                )
            )
        }
    }

    override fun onBreak(player: Player) {
        super.onBreak(player)

        if (player is ServerPlayer) {
            val level = player.serverLevel()
            val record = PhylacteryLevelDataAttachment.listPhylacteries(level)
                .find { it.pos == this.blockPos }

            if (record != null) {
                if (record.hasSoul) {
                    val pool = SoulPoolPlayerAttachment.getData(player)
                    SoulPoolPlayerAttachment.setData(
                        player,
                        pool.copy(soulPool = (pool.soulPool - 1).coerceAtLeast(0))
                    )
                }

                PhylacteryLevelDataAttachment.queueRemovePhylactery(level, this.blockPos)
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ownerUUID?.let {
            tag.putUUID("OwnerUUID", it)
        }
        ownerName?.let {
            tag.putString("OwnerName", it)
        }
        tag.putBoolean("HasSoul", hasSoul)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.hasUUID("OwnerUUID")) {
            ownerUUID = tag.getUUID("OwnerUUID")
        }
        if (tag.contains("OwnerName")) {
            ownerName = tag.getString("OwnerName")
        }
        hasSoul = tag.getBoolean("HasSoul")
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        saveAdditional(tag, registries)
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun onDataPacket(
        net: Connection,
        packet: ClientboundBlockEntityDataPacket,
        registries: HolderLookup.Provider
    ) {
        super.onDataPacket(net, packet, registries)
        val tag = packet.tag
        if (tag != null) {
            loadAdditional(tag, registries)
        }
    }

    companion object {
        fun onPlayerLoad(player: Player) {
            if (player is ServerPlayer) {
                val level = player.serverLevel()
                val deltas = PhylacteryLevelDataAttachment.popPendingPlayerDeltas(level)
                val delta = deltas[player.uuid] ?: 0
                val pool = SoulPoolPlayerAttachment.getData(player)
                SoulPoolPlayerAttachment.setData(
                    player,
                    pool.copy(soulPool = (pool.soulPool + delta).coerceAtLeast(0), maxSouls = pool.maxSouls)
                )
            }
        }
    }
}
