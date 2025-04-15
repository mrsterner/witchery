package dev.sterner.witchery.handler.werewolf

import net.minecraft.util.StringRepresentable

enum class WerewolfAbility(val unlockLevel: Int) : StringRepresentable {
    FORCED_WOLF(1),
    FORCED_WEREWOLF(4),
    FREE_WOLF_TRANSFORM(5),
    FREE_WEREWOLF_TRANSITION(5);

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}