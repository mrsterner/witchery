package dev.sterner.witchery.api.fluid

import dev.architectury.fluid.FluidStack


interface WitcheryFluidHandler {

    fun getTanks(): Int

    fun getFluidInTank(tank: Int): FluidStack

    fun getTankCapacity(tank: Int): Long

    fun isFluidValid(tank: Int, fluidStack: FluidStack): Boolean

    fun fill(fluidStack: FluidStack, simulate: Boolean): Long

    fun drain(maxDrain: Long, simulate: Boolean): FluidStack

    fun drain(fluidStack: FluidStack, simulate: Boolean): FluidStack

    fun getFluidAmount(): Long

    fun isFluidValid(fluidStack: FluidStack?): Boolean

    fun setFluidInTank(tank: Int, fluidStack: FluidStack)
}