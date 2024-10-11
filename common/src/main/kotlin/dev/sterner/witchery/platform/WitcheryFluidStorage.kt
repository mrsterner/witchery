package dev.sterner.witchery.platform

import dev.architectury.fluid.FluidStack
import net.minecraft.world.level.material.Fluid
import java.util.*


abstract class WitcheryFluidStorage {

    abstract fun add(fluid: FluidStack, amount: Long, simulate: Boolean): Long

    abstract fun remove(amount: Long, simulate: Boolean): Long

    abstract fun getFluidStack(): Optional<FluidStack>

    abstract fun setFluidStack(fluid: FluidStack)

    abstract fun getFluid(): Fluid?

    abstract fun getAmount(): Long

    abstract fun setAmount(amount: Long)

    abstract fun isEmpty(): Boolean

    abstract fun isFull(): Boolean

    abstract fun getMaxAmount(): Long
}