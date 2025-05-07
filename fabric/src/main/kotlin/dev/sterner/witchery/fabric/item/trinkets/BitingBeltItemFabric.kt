package dev.sterner.witchery.fabric.item.trinkets

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import dev.sterner.witchery.item.accessories.BitingBeltItem

class BitingBeltItemFabric(settings: Properties) : BitingBeltItem(settings), Trinket {

    init {
        TrinketsApi.registerTrinket(this, this)
    }


}