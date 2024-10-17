package dev.sterner.witchery.client.model

import dev.sterner.witchery.Witchery
import net.minecraft.client.model.geom.ModelLayerLocation

object BoatModels {
    val ROWAN_BOAT_LAYER = ModelLayerLocation(Witchery.id("boat/rowan"), "main")
    val ROWAN_CHEST_BOAT_LAYER = ModelLayerLocation(Witchery.id("chest_boat/rowan"), "main")

    val ALDER_BOAT_LAYER = ModelLayerLocation(Witchery.id("boat/alder"), "main")
    val ALDER_CHEST_BOAT_LAYER = ModelLayerLocation(Witchery.id("chest_boat/alder"), "main")

    val HAWTHORN_BOAT_LAYER = ModelLayerLocation(Witchery.id("boat/hawthorn"), "main")
    val HAWTHORN_CHEST_BOAT_LAYER = ModelLayerLocation(Witchery.id("chest_boat/hawthorn"), "main")
}