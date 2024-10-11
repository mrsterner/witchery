package dev.sterner.witchery.platform.neoforge

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import dev.sterner.witchery.platform.WitcheryFluidStorage
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import java.util.*

class WitcheryFluidStorageImpl(
    private val fluidTank: FluidTank,
    private val setChanged: Runnable?
) : WitcheryFluidStorage() {

    override fun add(fluid: FluidStack, amount: Long, simulate: Boolean): Long {
        val action = if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
        val fluidToInsert = fluid.copyWithAmount(amount.toInt().toLong())
        val inserted = fluidTank.fill(FluidStackHooksForge.toForge(fluidToInsert), action).toLong()
        if (inserted > 0 && !simulate) setChanged?.run()
        return inserted
    }

    override fun remove(amount: Long, simulate: Boolean): Long {
        val action = if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
        val drained = fluidTank.drain(amount.toInt(), action).amount.toLong()
        if (drained > 0 && !simulate) setChanged?.run()
        return drained
    }

    override fun getFluidStack(): Optional<FluidStack> {
        return if (fluidTank.fluid.isEmpty) Optional.empty() else Optional.of(FluidStackHooksForge.fromForge(fluidTank.fluid))
    }

    override fun setFluidStack(fluid: FluidStack) {
        fluidTank.fluid = FluidStackHooksForge.toForge(fluid)
        setChanged?.run()
    }

    override fun getFluid(): Fluid? {
        return fluidTank.fluid.fluid
    }

    override fun getAmount(): Long {
        return fluidTank.fluid.amount.toLong()
    }

    override fun setAmount(amount: Long) {
        fluidTank.fluid.amount = amount.toInt()
        setChanged?.run()
    }

    override fun isEmpty(): Boolean {
        return fluidTank.isEmpty
    }

    override fun isFull(): Boolean {
        return fluidTank.fluid.amount >= fluidTank.capacity
    }

    override fun getMaxAmount(): Long {
        return fluidTank.capacity.toLong()
    }
}