package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.TRANSFORMATION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import net.minecraft.world.entity.player.Player

object TransformationPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): TransformationPlayerAttachment.Data {
        return player.getData(TRANSFORMATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: TransformationPlayerAttachment.Data) {
        player.setData(TRANSFORMATION_PLAYER_DATA_ATTACHMENT, data)
        TransformationPlayerAttachment.sync(player, data)
    }
}