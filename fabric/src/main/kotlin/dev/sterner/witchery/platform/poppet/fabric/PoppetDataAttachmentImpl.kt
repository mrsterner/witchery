package dev.sterner.witchery.platform.poppet.fabric

import dev.sterner.witchery.fabric.WitcheryFabric.Companion.POPPET_DATA_TYPE
import dev.sterner.witchery.fabric.WitcheryFabric.Companion.VOODOO_POPPET_DATA_TYPE
import dev.sterner.witchery.platform.poppet.PoppetData
import dev.sterner.witchery.platform.poppet.PoppetDataAttachment
import dev.sterner.witchery.platform.poppet.VoodooPoppetData
import dev.sterner.witchery.platform.poppet.VoodooPoppetDataAttachment
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object PoppetDataAttachmentImpl {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetData {
        return level.getAttachedOrCreate(POPPET_DATA_TYPE)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        level.setAttached(POPPET_DATA_TYPE, data)
    }
}