package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForge.ATTACHMENT_TYPES
import dev.sterner.witchery.platform.AltarDataAttachment
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.attachment.AttachmentType
import java.util.function.Supplier

object AltarDataAttachmentImpl {

    @JvmStatic
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        if (!level.getData(LEVEL_DATA_ATTACHMENT).altarSet.contains(pos)) {
            val data = level.getData(LEVEL_DATA_ATTACHMENT)
            data.altarSet.add(pos)
            level.setData(LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(LEVEL_DATA_ATTACHMENT)
        data.altarSet.remove(pos)
        level.setData(LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        return level.getData(LEVEL_DATA_ATTACHMENT).altarSet
    }

    @JvmStatic
    val LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<AltarDataAttachment.AltarDataCodec>> = ATTACHMENT_TYPES.register(
        "altar_level_data",
        Supplier {
            AttachmentType.builder(Supplier { AltarDataAttachment.AltarDataCodec() })
                .serialize(AltarDataAttachment.AltarDataCodec.CODEC)
                .build()
        }
    )
}