package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.attachment.MutandisData
import dev.sterner.witchery.api.attachment.MutandisLevelAttachment
import dev.sterner.witchery.payload.MutandisRemenantParticleS2CPacket
import dev.sterner.witchery.payload.SyncCauldronS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object MutandisLevelDataAttachmentPlatform {

    val CACHE_LIFETIME = 20 * 3

    @JvmStatic
    @ExpectPlatform
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisData> {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>)  {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos)  {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos)  {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    fun tick(serverLevel: ServerLevel?) {
        if (serverLevel == null) return

        val toRemove = mutableListOf<BlockPos>()

        val iterator = getMap(serverLevel).iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val (pos, mutandisData) = entry
            val (tag, time) = mutandisData
            if (time <= 1) {
                toRemove.add(pos)
            } else {
                WitcheryPayloads.sendToPlayers(serverLevel, pos, MutandisRemenantParticleS2CPacket(pos))
                updateTimeForTagBlockPos(serverLevel, pos)
            }
        }

        for (pos in toRemove) {
            removeTagForBlockPos(serverLevel, pos)
        }
    }

    val ID: ResourceLocation = Witchery.id("level_data")

    val MUTANDIS_DATA_CODEC: Codec<MutandisData> = RecordCodecBuilder.create { inst ->
        inst.group(
            TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(MutandisData::tag),
            Codec.INT.fieldOf("time").forGetter(MutandisData::time)
        ).apply(inst, ::MutandisData)
    }

    val CODEC: Codec<MutandisLevelAttachment> = RecordCodecBuilder.create { inst ->
        inst.group(
            Codec.unboundedMap(
                BlockPos.CODEC,
                MUTANDIS_DATA_CODEC
            ).fieldOf("mutandisCacheMap")
                .forGetter(MutandisLevelAttachment::mutandisCacheMap)
        ).apply(inst, ::MutandisLevelAttachment)
    }
}