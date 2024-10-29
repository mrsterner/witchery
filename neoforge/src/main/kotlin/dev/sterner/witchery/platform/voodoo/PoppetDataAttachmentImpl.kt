package dev.sterner.witchery.platform.voodoo

import dev.sterner.witchery.neoforge.WitcheryNeoForge.POPPET_DATA_ATTACHMENT
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetData {
        return level.getData(POPPET_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        level.setData(POPPET_DATA_ATTACHMENT, data)
    }
}