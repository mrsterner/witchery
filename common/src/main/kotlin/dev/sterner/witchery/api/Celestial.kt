package dev.sterner.witchery.api

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class Celestial : StringRepresentable {
    DAY,
    NIGHT,
    FULL_MOON,
    NEW_MOON;

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CELESTIAL_CODEC: Codec<Celestial> = StringRepresentable.fromEnum(Celestial::values)
    }
}