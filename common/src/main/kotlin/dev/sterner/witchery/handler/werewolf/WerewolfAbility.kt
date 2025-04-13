package dev.sterner.witchery.handler.werewolf

import net.minecraft.util.StringRepresentable

enum class WerewolfAbility(val unlockLevel: Int) : StringRepresentable {
    ;

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}