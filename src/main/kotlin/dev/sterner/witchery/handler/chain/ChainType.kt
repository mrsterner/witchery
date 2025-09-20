package dev.sterner.witchery.handler.chain

import net.minecraft.util.StringRepresentable

enum class ChainType(val index: Int) : StringRepresentable {
    SPIRIT(0),
    SOUL(1);

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}