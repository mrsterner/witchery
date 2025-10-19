package dev.sterner.witchery.features.misc

import dev.sterner.witchery.network.OpenLecternGuidebookS2CPayload
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.LecternBlockEntity
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.network.PacketDistributor

object LecternHandler {

    fun tryAccessGuidebook(
        event: PlayerInteractEvent.RightClickBlock,
        player: Player,
        hand: InteractionHand,
        pos: BlockPos
    ) {
        val be = player.level().getBlockEntity(pos)
        if (player is ServerPlayer && be is LecternBlockEntity && be.book.`is`(WitcheryItems.GUIDEBOOK.get())) {
            PacketDistributor.sendToPlayer(player, OpenLecternGuidebookS2CPayload())
            event.isCanceled = true
        }
    }
}