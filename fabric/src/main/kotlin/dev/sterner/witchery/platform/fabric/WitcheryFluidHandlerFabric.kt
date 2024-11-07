package dev.sterner.witchery.platform.fabric

import dev.architectury.fluid.FluidStack
import dev.sterner.witchery.api.fluid.WitcheryFluidHandler
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant


class WitcheryFluidHandlerFabric(val witcheryFluidHandler: WitcheryFluidHandler, val tank: Int):
    SnapshotParticipant<ResourceAmount<FluidVariant>>(), SingleSlotStorage<FluidVariant> {

    override fun createSnapshot(): ResourceAmount<FluidVariant> {
        return ResourceAmount<FluidVariant>(
            FluidVariant.of(witcheryFluidHandler.getFluidInTank(tank).fluid),
            witcheryFluidHandler.getFluidInTank(tank).amount
        )
    }

    override fun readSnapshot(snapshot: ResourceAmount<FluidVariant>) {
        witcheryFluidHandler.setFluidInTank(tank, FluidStack.create(snapshot.resource().fluid, snapshot.amount()))
    }

    override fun insert(resource: FluidVariant?, maxAmount: Long, transaction: TransactionContext?): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        val stack = FluidStack.create(resource!!.fluid, maxAmount)

        val insertedAmount: Long = witcheryFluidHandler.fill(stack, true)
        if (insertedAmount > 0) {
            updateSnapshots(transaction)
            return witcheryFluidHandler.fill(stack, false)
        }
        return 0
    }

    override fun extract(resource: FluidVariant?, maxAmount: Long, transaction: TransactionContext?): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        val stack = FluidStack.create(resource!!.fluid, maxAmount)

        val extractedAmount: Long = witcheryFluidHandler.drain(stack, true).amount
        if (extractedAmount > 0) {
            updateSnapshots(transaction)
            return witcheryFluidHandler.drain(stack, false).amount
        }
        return 0
    }

    override fun isResourceBlank(): Boolean {
        return witcheryFluidHandler.getFluidInTank(tank).isEmpty
    }

    override fun getResource(): FluidVariant {
        return FluidVariant.of(witcheryFluidHandler.getFluidInTank(tank).fluid)
    }

    override fun getAmount(): Long {
        return witcheryFluidHandler.getFluidInTank(tank).amount
    }

    override fun getCapacity(): Long {
        return witcheryFluidHandler.getTankCapacity(tank)
    }
}