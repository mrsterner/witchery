package dev.sterner.witchery.platform.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.*


data class PoppetData(
    val poppetDataMap: MutableList<Data>
) {
    companion object {
        val CODEC: Codec<PoppetData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.list(Data.CODEC).fieldOf("poppetData").forGetter { it.poppetDataMap }
            ).apply(instance) { poppetData ->
                PoppetData(poppetData.toMutableList())
            }
        }

        private val UUID_CODEC: Codec<UUID> = Codec.STRING.xmap(
            { UUID.fromString(it) },
            { it.toString() }
        )

        val ID: ResourceLocation = Witchery.id("poppet_data")
    }

    data class Data(val blockPos: BlockPos, var poppetItemStack: ItemStack) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("blockPos").forGetter { it.blockPos },
                    ItemStack.CODEC.fieldOf("poppetItemStack").forGetter { it.poppetItemStack },
                ).apply(instance, ::Data)
            }
        }
    }
}