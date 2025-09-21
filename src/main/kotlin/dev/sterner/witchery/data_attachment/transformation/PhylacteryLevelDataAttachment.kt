package dev.sterner.witchery.data_attachment.transformation

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object PhylacteryLevelDataAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.PHYLACTERY_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.PHYLACTERY_LEVEL_DATA_ATTACHMENT, data)
    }

    fun addPhylactery(level: ServerLevel, record: PhylacteryRecord) {
        val data = getData(level)
        data.phylacteries.removeIf { it.pos == record.pos }
        data.phylacteries.add(record)
        setData(level, data)
    }

    fun removePhylactery(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        data.phylacteries.removeIf { it.pos == pos }
        setData(level, data)
    }

    fun setPhylacteryHasSoul(level: ServerLevel, pos: BlockPos, hasSoul: Boolean) {
        val data = getData(level)
        val replaced = data.phylacteries.find { it.pos == pos }?.let {
            data.phylacteries.remove(it)
            data.phylacteries.add(it.copy(hasSoul = hasSoul))
            true
        } ?: false

        if (!replaced) {
            data.pendingPhylacteryChanges.add(PendingPhylacteryChange(pos, "setHasSoul", Optional.empty(), hasSoul))
        }

        setData(level, data)
    }

    fun setPhylacteryOwner(level: ServerLevel, pos: BlockPos, owner: UUID) {
        val data = getData(level)
        val replaced = data.phylacteries.find { it.pos == pos }?.let {
            data.phylacteries.remove(it)
            data.phylacteries.add(it.copy(owner = owner))
            true
        } ?: false

        if (!replaced) {
            data.pendingPhylacteryChanges.add(PendingPhylacteryChange(pos, "setOwner", Optional.of(owner), false))
        }

        setData(level, data)
    }

    fun queueRemovePhylactery(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val record = data.phylacteries.find { it.pos == pos }

        if (record != null) {
            if (record.hasSoul) {
                addPendingPlayerDelta(level, record.owner, -1)
            }

            data.phylacteries.removeIf { it.pos == pos }
            data.pendingPhylacteryChanges.add(PendingPhylacteryChange(pos, "remove", Optional.empty(), false))

            setData(level, data)
        }
    }


    fun listPhylacteries(level: ServerLevel): Set<PhylacteryRecord> {
        return getData(level).phylacteries
    }

    fun listPhylacteriesForPlayer(level: ServerLevel, playerUuid: UUID): List<PhylacteryRecord> {
        return getData(level).phylacteries.filter { it.owner == playerUuid }
    }

    fun addPendingPlayerDelta(level: ServerLevel, playerUuid: UUID, delta: Int) {
        val data = getData(level)
        val key = playerUuid.toString()
        data.pendingPlayerDeltas[key] = (data.pendingPlayerDeltas[key] ?: 0) + delta
        setData(level, data)
    }

    fun popPendingPlayerDeltas(level: ServerLevel): Map<UUID, Int> {
        val data = getData(level)
        val map = data.pendingPlayerDeltas.mapKeys { UUID.fromString(it.key) }.toMap()
        data.pendingPlayerDeltas.clear()
        setData(level, data)
        return map
    }

    fun applyPendingPhylacteryChangesForPos(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val (matching, rest) = data.pendingPhylacteryChanges.partition { it.pos == pos }
        if (matching.isEmpty()) return

        data.pendingPhylacteryChanges.clear()
        data.pendingPhylacteryChanges.addAll(rest)

        for (change in matching) {
            when (change.action) {
                "remove" -> data.phylacteries.removeIf { it.pos == pos }
                "setHasSoul" -> {
                    val current = data.phylacteries.find { it.pos == pos }
                    if (current != null) {
                        data.phylacteries.remove(current)
                        data.phylacteries.add(current.copy(hasSoul = change.hasSoul))
                    } else {
                        data.phylacteries.add(PhylacteryRecord(pos, change.owner.get(), change.hasSoul))
                    }
                }

                "setOwner" -> {
                    val current = data.phylacteries.find { it.pos == pos }
                    if (current != null) {
                        data.phylacteries.remove(current)
                        data.phylacteries.add(current.copy(owner = change.owner.get()))
                    }
                }

                "set" -> {
                    data.phylacteries.removeIf { it.pos == pos }
                    data.phylacteries.add(PhylacteryRecord(pos, change.owner.get(), change.hasSoul))
                }
            }
        }

        setData(level, data)
    }

    data class PhylacteryRecord(
        val pos: BlockPos,
        val owner: UUID,
        val hasSoul: Boolean
    ) {
        companion object {
            private val UUID_CODEC: Codec<UUID> =
                Codec.STRING.xmap({ UUID.fromString(it) }, { it.toString() })

            val CODEC: Codec<PhylacteryRecord> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    UUID_CODEC.fieldOf("owner").forGetter { it.owner },
                    Codec.BOOL.fieldOf("hasSoul").forGetter { it.hasSoul }
                ).apply(instance, ::PhylacteryRecord)
            }
        }
    }

    data class Data(
        val phylacteries: MutableSet<PhylacteryRecord> = mutableSetOf(),
        val pendingPhylacteryChanges: MutableList<PendingPhylacteryChange> = mutableListOf(),
        val pendingPlayerDeltas: MutableMap<String, Int> = mutableMapOf()
    ) {
        companion object {
            val PENDING_CHANGE_CODEC: Codec<PendingPhylacteryChange> = RecordCodecBuilder.create { inst ->
                inst.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                    Codec.STRING.fieldOf("action").forGetter { it.action },
                    Codecs.UUID.optionalFieldOf("owner").forGetter { it.owner },
                    Codec.BOOL.fieldOf("hasSoul").forGetter { it.hasSoul }
                ).apply(inst) { pos, action, ownerStr, hasSoulOpt ->
                    val owner = ownerStr
                    PendingPhylacteryChange(pos, action, owner, hasSoulOpt)
                }
            }

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(PhylacteryRecord.CODEC).xmap({ it.toMutableSet() }, { it.toList() })
                        .fieldOf("phylacteries").forGetter { it.phylacteries },
                    Codec.list(PENDING_CHANGE_CODEC).xmap({ it.toMutableList() }, { it.toList() })
                        .fieldOf("pendingPhylacteryChanges").forGetter { it.pendingPhylacteryChanges },
                    Codec.unboundedMap(Codec.STRING, Codec.INT)
                        .xmap({ it.toMutableMap() }, { it.toMap() })
                        .fieldOf("pendingPlayerDeltas").forGetter { it.pendingPlayerDeltas }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("phylactery_level_data")
        }
    }

    data class PendingPhylacteryChange(
        val pos: BlockPos,
        val action: String,
        val owner: Optional<UUID> = Optional.empty(),
        val hasSoul: Boolean = false
    )
}