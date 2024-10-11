package dev.sterner.witchery.platform.fabric

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric
import dev.sterner.witchery.platform.WitcheryFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.level.material.Fluid
import java.util.*

class WitcheryFluidStorageImpl(
    private val maxAmount: Long,
    private val setChanged: Runnable
) : WitcheryFluidStorage() {

    private val fluidStorage = object : SingleVariantStorage<FluidVariant>() {
        override fun getBlankVariant(): FluidVariant = FluidVariant.blank()

        override fun getCapacity(variant: FluidVariant): Long = this@WitcheryFluidStorageImpl.maxAmount

        override fun onFinalCommit() {
            this@WitcheryFluidStorageImpl.setChanged.run()
        }
    }

    override fun add(fluid: FluidStack, amount: Long, simulate: Boolean): Long {
        if (isFull()) return 0
        var added = 0L
        Transaction.openOuter().use { tx ->
            val inserted = fluidStorage.insert(FluidStackHooksFabric.toFabric(fluid), amount, tx)
            if (inserted == amount) added = inserted
            if (added > 0 && !simulate) tx.commit()
        }
        return added
    }

    override fun remove(amount: Long, simulate: Boolean): Long {
        if (isEmpty()) return 0
        var removed = 0L
        Transaction.openOuter().use { tx ->
            val extracted = fluidStorage.extract(fluidStorage.resource, amount, tx)
            if (extracted == amount) removed = extracted
            if (removed > 0 && !simulate) tx.commit()
        }
        return removed
    }

    override fun getFluidStack(): Optional<FluidStack> {
        return if (fluidStorage.isResourceBlank) Optional.empty() else Optional.of(
            FluidStackHooksFabric.fromFabric(
                fluidStorage
            )
        )
    }

    override fun setFluidStack(fluid: FluidStack) {
        fluidStorage.variant = FluidStackHooksFabric.toFabric(fluid)
        fluidStorage.amount = fluid.amount
        this.setChanged.run()
    }

    override fun getFluid(): Fluid? {
        return fluidStorage.resource.fluid
    }

    override fun getAmount(): Long {
        return fluidStorage.getAmount()
    }

    override fun setAmount(amount: Long) {
        fluidStorage.amount = amount
        this.setChanged.run()
    }

    override fun isEmpty(): Boolean {
        return fluidStorage.isResourceBlank || fluidStorage.getAmount() == 0L
    }

    override fun isFull(): Boolean {
        return !fluidStorage.isResourceBlank && fluidStorage.getAmount() >= this.maxAmount
    }

    override fun getMaxAmount(): Long {
        return fluidStorage.capacity
    }
}