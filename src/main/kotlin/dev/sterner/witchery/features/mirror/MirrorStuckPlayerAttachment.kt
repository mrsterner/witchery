package dev.sterner.witchery.features.mirror

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncMirrorStuckS2CPayload
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor
import java.util.Optional
import java.util.UUID

object MirrorStuckPlayerAttachment {

    @JvmStatic
    fun getData(serverLevel: Level): Data {
        return serverLevel.getData(WitcheryDataAttachments.MIRROR_STUCK_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: Level, data: Data) {
        level.setData(WitcheryDataAttachments.MIRROR_STUCK_ATTACHMENT, data)
        if (level is ServerLevel) {
            PacketDistributor.sendToPlayersInDimension(level, SyncMirrorStuckS2CPayload(data))
        }
    }

    fun hasWallPos(level: Level, pos: BlockPos): Boolean {
        return getData(level).entries.any { it.wallPos == pos }
    }

    fun setWallPos(level: Level, uuid: UUID, wallPos: BlockPos) {
        if (level !is ServerLevel) return

        val data = getData(level)
        val existing = data.entries.firstOrNull { it.uuid == uuid }

        if (existing != null) {
            existing.wallPos = wallPos
        } else {
            data.entries.toMutableList() += DataEntry(uuid, wallPos)
        }

        setData(level, data)
    }

    fun removeEntry(level: Level, uuid: UUID) {
        if (level !is ServerLevel) return

        val data = getData(level)
        data.entries.removeIf { it.uuid == uuid }
        setData(level, data)
    }

    data class Data(val entries: MutableList<DataEntry> = mutableListOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("mirror_stuck")
            val DATA_CODEC: Codec<Data> =
                DataEntry.CODEC.listOf().fieldOf("entries").xmap(::Data, Data::entries).codec()
        }
    }

    data class DataEntry(var uuid: UUID, var wallPos: BlockPos) {
        companion object {
            val CODEC: Codec<DataEntry> = RecordCodecBuilder.create { inst ->
                inst.group(
                    Codecs.UUID.fieldOf("uuid").forGetter(DataEntry::uuid),
                    BlockPos.CODEC.fieldOf("wallPos").forGetter(DataEntry::wallPos)
                ).apply(inst, ::DataEntry)
            }
        }
    }
}