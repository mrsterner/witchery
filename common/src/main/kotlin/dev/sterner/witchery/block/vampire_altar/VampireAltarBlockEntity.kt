package dev.sterner.witchery.block.vampire_altar

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

class VampireAltarBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.VAMPIRE_ALTAR.get(), blockPos, blockState) {

    private var bloodAmount: Int = 10000
    private val maxBloodStorage: Int = 10000

    fun addBlood(amount: Int) {
        bloodAmount = (bloodAmount + amount).coerceAtMost(maxBloodStorage)
        setChanged()
    }

    fun removeBlood(amount: Int): Int {
        val removed = amount.coerceAtMost(bloodAmount)
        bloodAmount -= removed
        setChanged()
        return removed
    }

    fun getBloodAmount(): Int {
        return bloodAmount
    }

    fun bloodPercent(): Double {
        return bloodAmount.toDouble() / maxBloodStorage.toDouble()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("BloodAmount", bloodAmount)
    }

    override fun loadAdditional(
        pTag: CompoundTag,
        pRegistries: HolderLookup.Provider
    ) {
        super.loadAdditional(pTag, pRegistries)
        bloodAmount = pTag.getInt("BloodAmount")
    }
}