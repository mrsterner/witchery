package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.poppet.CorruptPoppetPlayerAttachment.Data
import net.minecraft.world.entity.player.Player

object CorruptPoppetPlayerAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.CORRUPT_POPPET_DATA_TYPE, data)
    }

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.CORRUPT_POPPET_DATA_TYPE)
    }

}