package dev.sterner.witchery.entity

import dev.sterner.witchery.data_attachment.BoatTypeHelper
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class CustomChestBoat(type: EntityType<out Boat>, level: Level) : Boat(type, level) {

    constructor(level: Level, x: Double, y: Double, z: Double) : this(
        WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get(),
        level
    ) {
        this.setPos(x, y, z)
        this.xo = x
        this.yo = y
        this.zo = z
    }

    override fun getDisplayName(): MutableComponent =
        Component.translatable("entity.witchery.${this.variant.name}_chest_boat")

    override fun getDropItem(): Item {
        return when (this.variant) {
            BoatTypeHelper.getRowanBoatType() -> WitcheryItems.ROWAN_CHEST_BOAT.get()
            BoatTypeHelper.getAlderBoatType() -> WitcheryItems.ALDER_CHEST_BOAT.get()
            BoatTypeHelper.getHawthornBoatType() -> WitcheryItems.HAWTHORN_CHEST_BOAT.get()
            else -> super.getDropItem()
        }
    }
}