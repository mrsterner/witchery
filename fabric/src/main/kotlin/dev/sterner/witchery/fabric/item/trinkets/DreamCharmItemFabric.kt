package dev.sterner.witchery.fabric.item.trinkets

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import dev.sterner.witchery.item.accessories.DreamweaverCharmItem

class DreamCharmItemFabric(settings: Properties) : DreamweaverCharmItem(settings), Trinket {

    init {
        TrinketsApi.registerTrinket(this, this)
    }
}