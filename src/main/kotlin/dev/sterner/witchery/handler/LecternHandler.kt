package dev.sterner.witchery.handler

import dev.sterner.witchery.payload.OpenLecternGuidebookS2CPayload
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.LecternBlockEntity
import net.neoforged.neoforge.network.PacketDistributor

object LecternHandler {

    fun registerEvents() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(LecternHandler::tryAccessGuidebook)
    }

    private fun tryAccessGuidebook(player: Player, hand: InteractionHand, pos: BlockPos, face: Direction): EventResult {
        val be = player.level().getBlockEntity(pos)
        if (player is ServerPlayer && be is LecternBlockEntity && be.book.`is`(WitcheryItems.GUIDEBOOK.get())) {
            PacketDistributor.sendToPlayer(player, OpenLecternGuidebookS2CPayload())

            return EventResult.interruptTrue()
        }

        return EventResult.pass()
    }
}