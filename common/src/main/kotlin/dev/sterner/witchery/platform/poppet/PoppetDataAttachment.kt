package dev.sterner.witchery.platform.poppet

import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.payload.SyncVoodooDataS2CPacket
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

object PoppetDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPoppetData(level: ServerLevel): PoppetData {
        throw AssertionError()
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
}