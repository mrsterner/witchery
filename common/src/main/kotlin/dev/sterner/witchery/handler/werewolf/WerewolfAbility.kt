package dev.sterner.witchery.handler.werewolf

import net.minecraft.util.StringRepresentable

enum class WerewolfAbility(val unlockLevel: Int) : StringRepresentable {
    FREE_WOLF_TRANSFORM(5),
    FREE_WEREWOLF_TRANSITION(5);

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}