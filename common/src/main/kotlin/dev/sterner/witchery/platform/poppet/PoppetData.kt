package dev.sterner.witchery.platform.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.*


data class PoppetData(
    val poppetDataMap: MutableList<Data>,
    val cleanupMap: MutableMap<UUID, MutableList<BlockPos>> = mutableMapOf()
) {
    companion object {
        val CODEC: Codec<PoppetData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.list(Data.CODEC).fieldOf("poppetData").forGetter { it.poppetDataMap },
                Codec.unboundedMap(
                    UUID_CODEC,
                    Codec.list(BlockPos.CODEC)
                ).fieldOf("cleanupMap").forGetter { it.cleanupMap }
            ).apply(instance) { poppetData, cleanupMap ->
                PoppetData(poppetData.toMutableList(), cleanupMap.toMutableMap())
            }
        }

        private val UUID_CODEC: Codec<UUID> = Codec.STRING.xmap(
            { UUID.fromString(it) },
            { it.toString() }
        )

        val ID: ResourceLocation = Witchery.id("poppet_data")
    }

    data class Data(val uuid: UUID, val blockPos: BlockPos, val poppetItemStack: ItemStack) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUID_CODEC.fieldOf("uuid").forGetter { it.uuid },
                    BlockPos.CODEC.fieldOf("blockPos").forGetter { it.blockPos },
                    ItemStack.CODEC.fieldOf("poppetItemStack").forGetter { it.poppetItemStack },
                ).apply(instance, ::Data)
            }
        }
    }
}