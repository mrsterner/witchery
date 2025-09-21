package dev.sterner.witchery.data_attachment.poppet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

object PoppetLevelAttachment {

    @JvmStatic
    fun getPoppetData(level: ServerLevel): PoppetData {
        return level.getData(WitcheryDataAttachments.POPPET_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        level.setData(WitcheryDataAttachments.POPPET_DATA_ATTACHMENT, data)
    }

    fun handleBlockDestruction(level: ServerLevel, pos: BlockPos) {
        val oldData = getPoppetData(level)
        if (oldData.poppetDataMap.removeIf { it.blockPos == pos }) {
            setPoppetData(level, oldData)
        }
    }

    fun addPoppetData(level: ServerLevel, data: PoppetData.Data) {
        val oldData = getPoppetData(level)
        oldData.poppetDataMap.add(data)
        setPoppetData(level, oldData)
    }

    fun getPoppet(level: ServerLevel, pos: BlockPos): ItemStack? {
        return getPoppetData(level).poppetDataMap.find { it.blockPos == pos }?.poppetItemStack
    }

    fun getPoppets(level: ServerLevel, uuid: UUID): List<ItemStack> {
        val data = getPoppetData(level)

        return data.poppetDataMap.mapNotNull {
            val targetUUID = try {
                UUID.fromString(it.poppetItemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get()))
            } catch (e: IllegalArgumentException) {
                null
            }
            if (it.poppetItemStack.get(WitcheryDataComponents.PLAYER_UUID.get()) == uuid || targetUUID == uuid) {
                it.poppetItemStack
            } else null
        }
    }

    fun getPoppet(level: ServerLevel, uuid: UUID, ofType: Item): ItemStack {
        return getPoppets(level, uuid).find { it.`is`(ofType) } ?: ItemStack.EMPTY
    }

    fun updatePoppetItem(level: ServerLevel, pos: BlockPos, newStack: ItemStack) {
        val oldData = getPoppetData(level)
        val targetData = oldData.poppetDataMap.find { it.blockPos == pos }

        if (targetData != null) {
            targetData.poppetItemStack = newStack.copy()
            setPoppetData(level, oldData)
        }
    }

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
}