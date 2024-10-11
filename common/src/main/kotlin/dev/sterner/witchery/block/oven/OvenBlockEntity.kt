package dev.sterner.witchery.block.oven

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.menu.OvenMenu
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryMenuTypes
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState


class OvenBlockEntity(blockPos: BlockPos, blockState: BlockState
) : WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.OVEN.get(), blockPos, blockState) {

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
}