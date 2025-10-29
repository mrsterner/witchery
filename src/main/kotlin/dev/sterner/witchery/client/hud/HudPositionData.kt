package dev.sterner.witchery.client.hud


import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

data class HudPositionData(
    val infusionCoord: Coord = Coord(10, -1),
    val manifestationCoord: Coord = Coord(28, -1),
    val barkBeltCoord: Coord = Coord(-1, -1)
) {

    data class Coord(val x: Int, val y: Int){
        companion object {
            val CODEC: Codec<Coord> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("x").forGetter { it.x },
                    Codec.INT.fieldOf("y").forGetter { it.y },
                ).apply(instance, ::Coord)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Coord> = StreamCodec.composite(
                StreamCodec.of({ buf, value -> buf.writeInt(value) }, { it.readInt() }),
                Coord::x,
                StreamCodec.of({ buf, value -> buf.writeInt(value) }, { it.readInt() }),
                Coord::y,
                ::Coord
            )
        }
    }

    companion object {
        val CODEC: Codec<HudPositionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Coord.CODEC.fieldOf("infusion").forGetter { it.infusionCoord },
                Coord.CODEC.fieldOf("manifestation").forGetter { it.manifestationCoord },
                Coord.CODEC.fieldOf("barkBelt").forGetter { it.barkBeltCoord }
            ).apply(instance, ::HudPositionData)
        }


        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, HudPositionData> = StreamCodec.composite(
            Coord.STREAM_CODEC,
            HudPositionData::infusionCoord,
            Coord.STREAM_CODEC,
            HudPositionData::manifestationCoord,
            Coord.STREAM_CODEC,
            HudPositionData::barkBeltCoord,
            ::HudPositionData
        )
    }

    fun getInfusionPos(screenHeight: Int): Coord {
        return if (infusionCoord.y == -1) {
            Coord(infusionCoord.x, screenHeight / 2 - (47 / 2))
        } else {
            infusionCoord
        }
    }

    fun getManifestationPos(screenHeight: Int): Coord {
        return if (manifestationCoord.y == -1) {
            Coord(manifestationCoord.x, screenHeight / 2 - (24 / 2))
        } else {
            manifestationCoord
        }
    }

    fun getBarkBeltPos(screenWidth: Int, screenHeight: Int): Coord {
        return if (barkBeltCoord.x == -1 || barkBeltCoord.y == -1) {
            val y = screenHeight - 18 - 18 - 12
            val x = screenWidth / 2 - 36 - 18 * 3
            Coord(x, y)
        } else {
            barkBeltCoord
        }
    }
}