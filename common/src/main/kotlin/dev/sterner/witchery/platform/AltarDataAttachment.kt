package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

object AltarDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        throw AssertionError()
    }

    data class AltarDataCodec(val altarSet: MutableSet<BlockPos> = mutableSetOf()) {
        companion object {
            val CODEC: Codec<AltarDataCodec> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(BlockPos.CODEC).xmap({ it.toMutableSet() }, { it.toList() })
                        .fieldOf("altarSet").forGetter { it.altarSet }
                ).apply(instance, ::AltarDataCodec)
            }
            val ID: ResourceLocation = Witchery.id("altar_level_data")
        }
    }
}