package dev.sterner.witchery.api.fluid

import dev.architectury.fluid.FluidStack
import dev.sterner.witchery.block.cauldron.CauldronBlockEntity
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import java.util.function.Predicate
import kotlin.math.min


class WitcheryFluidTank(
    var blockEntity: CauldronBlockEntity,
    var fluid: FluidStack = FluidStack.empty(),
    val capacity: Long = FluidStack.bucketAmount(),
    var predicate: Predicate<FluidStack?> = Predicate { true }

): WitcheryFluidHandler {

    override fun getFluidAmount(): Long {
        return fluid.amount
    }

    override fun isFluidValid(fluidStack: FluidStack?): Boolean {
        return predicate.test(fluidStack)
    }

    override fun getTanks(): Int {
        return 1
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        return fluid
    }

    override fun getTankCapacity(tank: Int): Long {
        return capacity
    }

    override fun isFluidValid(tank: Int, fluidStack: FluidStack): Boolean {
        return isFluidValid(fluidStack)
    }

    override fun fill(fluidStack: FluidStack, simulate: Boolean): Long {
        if (fluidStack.isEmpty || !isFluidValid(fluidStack)) {
            return 0
        }
        if (simulate) {
            return if (fluid.isEmpty) {
                min(capacity.toDouble(), fluidStack.amount.toDouble()).toLong()
            } else if (fluid.isFluidEqual(fluidStack)) {
                min((capacity - fluid.amount).toDouble(), fluidStack.amount.toDouble()).toLong()
            } else {
                0
            }
        }

        if (fluid.isEmpty) {
            fluid = FluidStack.create(fluidStack, min(capacity, fluidStack.amount))
            setChanged()
            return fluid.amount
        }
        if (!fluid.isFluidEqual(fluidStack)) {
            return 0
        }
        val availableSpace = capacity - fluid.amount
        if (fluidStack.amount < availableSpace) {
            fluid.grow(fluidStack.amount)
        } else {
            fluid.amount = capacity
        }
        if (availableSpace > 0) {
            setChanged()
        }
        return availableSpace
    }

    override fun drain(maxDrain: Long, simulate: Boolean): FluidStack {
        val amountToDrain = min(maxDrain, fluid.amount)
        val drainedFluid = FluidStack.create(fluid, amountToDrain)
        if (!simulate && amountToDrain > 0) {
            fluid.shrink(amountToDrain)
            setChanged()
        }
        return drainedFluid
    }

    override fun drain(fluidStack: FluidStack, simulate: Boolean): FluidStack {
        if (fluidStack.isEmpty || !fluidStack.isFluidEqual(fluid)) {
            return FluidStack.empty()
        }
        return drain(fluidStack.amount, simulate)
    }

    fun setChanged() {
        blockEntity.setChanged()
    }

    fun isEmpty(): Boolean {
        return fluid.isEmpty
    }

    fun loadFluidAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        if (pTag.contains("Fluid")) {
            val fluidTag = pTag.getCompound("Fluid")
            fluid = FluidStack.read(pRegistries, fluidTag).orElse(FluidStack.empty())
        }
    }

    fun saveFluidAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider): CompoundTag {
        if (!fluid.isEmpty) {
            pTag.put("Fluid", fluid.write(pRegistries, pTag))
        }
        return pTag
    }

    fun getUpdateTag(superTag: CompoundTag, pRegistries: HolderLookup.Provider): CompoundTag {
        if (!fluid.isEmpty) {
            superTag.put("Fluid", fluid.write(pRegistries, CompoundTag()))
        }
        return superTag
    }

    override fun setFluidInTank(tank: Int, fluidStack: FluidStack) {
        fluid = fluidStack
    }
}