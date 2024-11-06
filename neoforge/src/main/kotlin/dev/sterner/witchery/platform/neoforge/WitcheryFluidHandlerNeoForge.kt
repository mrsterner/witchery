package dev.sterner.witchery.platform.neoforge

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import dev.sterner.witchery.api.fluid.WitcheryFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction


class WitcheryFluidHandlerNeoForge(val witcheryFluidHandler: WitcheryFluidHandler) : IFluidHandler {

    override fun getTanks(): Int {
        return witcheryFluidHandler.getTanks()
    }

    override fun getFluidInTank(i: Int): net.neoforged.neoforge.fluids.FluidStack {
        return FluidStackHooksForge.toForge(witcheryFluidHandler.getFluidInTank(i))
    }

    override fun getTankCapacity(i: Int): Int {
        return witcheryFluidHandler.getTankCapacity(i).toInt()
    }

    override fun isFluidValid(i: Int, fluidStack: net.neoforged.neoforge.fluids.FluidStack): Boolean {
        return witcheryFluidHandler.isFluidValid(i, FluidStackHooksForge.fromForge(fluidStack))
    }

    override fun fill(fluidStack: net.neoforged.neoforge.fluids.FluidStack, fluidAction: FluidAction): Int {
        return witcheryFluidHandler.fill(FluidStackHooksForge.fromForge(fluidStack), fluidAction == FluidAction.SIMULATE).toInt()
    }

    override fun drain(
        fluidStack: net.neoforged.neoforge.fluids.FluidStack,
        fluidAction: FluidAction
    ): net.neoforged.neoforge.fluids.FluidStack {
        return FluidStackHooksForge.toForge(
            witcheryFluidHandler.drain(
                FluidStackHooksForge.fromForge(fluidStack),
                fluidAction== FluidAction.SIMULATE
            )
        )
    }

    override fun drain(i: Int, fluidAction: FluidAction): net.neoforged.neoforge.fluids.FluidStack {
        return FluidStackHooksForge.toForge(
            witcheryFluidHandler.drain(
                i.coerceAtLeast(Int.MAX_VALUE).toLong(),
                fluidAction == FluidAction.SIMULATE
            )
        )
    }
}