package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class InfusionType : StringRepresentable {
    NONE,
    LIGHT,
    OVERWORLD,
    INFERNAL,
    OTHERWHERE;

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CODEC: Codec<InfusionType> = StringRepresentable.fromEnum(::values)
    }
}