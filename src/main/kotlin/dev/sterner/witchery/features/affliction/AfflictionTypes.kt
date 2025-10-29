package dev.sterner.witchery.features.affliction

import net.minecraft.util.StringRepresentable

enum class AfflictionTypes : StringRepresentable {
    VAMPIRISM,
    LYCANTHROPY,
    DEATH,
    LICHDOM;

    override fun getSerializedName(): String = name.lowercase()
}