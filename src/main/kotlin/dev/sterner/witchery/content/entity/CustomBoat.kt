package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.util.BoatTypeHelper
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class CustomBoat(type: EntityType<out Boat>, level: Level) : Boat(type, level) {

    constructor(level: Level, x: Double, y: Double, z: Double) : this(WitcheryEntityTypes.CUSTOM_BOAT.get(), level) {
        this.setPos(x, y, z)
        this.xo = x
        this.yo = y
        this.zo = z
    }

    override fun getDisplayName(): MutableComponent =
        Component.translatable("entity.witchery.${this.variant.name}_boat")

    override fun getDropItem(): Item {
        return when (this.variant) {
            BoatTypeHelper.getRowanBoatType() -> WitcheryItems.ROWAN_BOAT.get()
            BoatTypeHelper.getAlderBoatType() -> WitcheryItems.ALDER_BOAT.get()
            BoatTypeHelper.getHawthornBoatType() -> WitcheryItems.HAWTHORN_BOAT.get()
            else -> super.getDropItem()
        }
    }
}