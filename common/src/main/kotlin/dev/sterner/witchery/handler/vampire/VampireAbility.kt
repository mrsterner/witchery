package dev.sterner.witchery.handler.vampire

import net.minecraft.util.StringRepresentable

enum class VampireAbility(val unlockLevel: Int) : StringRepresentable {
    DRINK_BLOOD(1),
    TRANSFIX(2),
    SPEED(4),
    BAT_FORM(7);

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}