package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import net.minecraft.world.entity.player.Player

object TransformationPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): TransformationPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.TRANSFORMATION_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: TransformationPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.TRANSFORMATION_PLAYER_DATA_TYPE, data)
        TransformationPlayerAttachment.sync(player, data)
    }
}