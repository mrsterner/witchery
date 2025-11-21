package dev.sterner.witchery.features.mirror

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.mirror.MirrorBlockEntity
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.resources.ResourceLocation
import java.util.*

object MirrorRegistryAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.MIRROR_REGISTRY_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.MIRROR_REGISTRY_ATTACHMENT, data)
    }

    @JvmStatic
    fun registerMirror(level: ServerLevel, pairId: UUID, pos: GlobalPos) {
        val data = getData(level)
        val updatedEntries = data.entries.toMutableList()

        updatedEntries.removeIf { it.pos == pos }

        updatedEntries.add(MirrorEntry(pairId, pos))

        setData(level, Data(updatedEntries))

        updateLoadedMirrorsForPair(level, pairId, updatedEntries)
    }

    @JvmStatic
    fun unregisterMirror(level: ServerLevel, pos: GlobalPos) {
        val data = getData(level)

        val removedEntry = data.entries.find { it.pos == pos }
        val updatedEntries = data.entries.filter { it.pos != pos }

        setData(level, Data(updatedEntries))

        if (removedEntry != null) {
            updateLoadedMirrorsForPair(level, removedEntry.pairId, updatedEntries)
        }
    }

    private fun updateLoadedMirrorsForPair(level: ServerLevel, pairId: UUID, allEntries: List<MirrorEntry>) {
        val pairEntries = allEntries.filter { it.pairId == pairId }

        for (entry in pairEntries) {
            val pairedPos: GlobalPos? = pairEntries.firstOrNull { it.pos != entry.pos }?.pos

            val targetLevel = level.server.getLevel(entry.pos.dimension())
            if (targetLevel != null) {
                val be = targetLevel.getBlockEntity(entry.pos.pos())
                if (be is MirrorBlockEntity) {
                    be.cachedLinkedMirror = pairedPos
                    be.setChanged()
                }
            }
        }
    }

    @JvmStatic
    fun findPairedMirror(level: ServerLevel, pairId: UUID, currentPos: GlobalPos): GlobalPos? {
        val data = getData(level)

        val pairedMirror = data.entries.find {
            it.pairId == pairId && it.pos != currentPos
        }

        return pairedMirror?.pos
    }

    data class Data(val entries: List<MirrorEntry> = listOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("mirror_registry")
            val DATA_CODEC: Codec<Data> =
                MirrorEntry.CODEC.listOf().fieldOf("entries").xmap(::Data, Data::entries).codec()
        }
    }

    data class MirrorEntry(val pairId: UUID, val pos: GlobalPos) {
        companion object {
            val CODEC: Codec<MirrorEntry> = RecordCodecBuilder.create { inst ->
                inst.group(
                    UUIDUtil.CODEC.fieldOf("pairId").forGetter(MirrorEntry::pairId),
                    GlobalPos.CODEC.fieldOf("pos").forGetter(MirrorEntry::pos)
                ).apply(inst, ::MirrorEntry)
            }
        }
    }
}