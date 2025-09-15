package dev.sterner.witchery.handler.affliction

import net.minecraft.util.StringRepresentable

enum class AfflictionTypes : StringRepresentable {
    NONE,
    WITCH,
    VAMPIRISM,
    LYCANTHROPY,
    LICHDOM;

    override fun getSerializedName(): String = name.lowercase()
}