package dev.sterner.witchery.handler.affliction

import net.minecraft.util.StringRepresentable

enum class AfflictionTypes : StringRepresentable {
    NONE,
    WITCH,
    VAMPIRE,
    WEREWOLF,
    LICH;

    override fun getSerializedName(): String = name.lowercase()
}