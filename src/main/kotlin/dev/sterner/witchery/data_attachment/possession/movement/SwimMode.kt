package dev.sterner.witchery.data_attachment.possession.movement

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable


enum class SwimMode(private val serializedName: String) : StringRepresentable {
    DISABLED("disabled"),
    ENABLED("enabled"),
    SINKING("sinking"),
    UNSPECIFIED("unspecified");

    override fun getSerializedName(): String = serializedName.lowercase()

    companion object {
        val CODEC: Codec<SwimMode> = StringRepresentable.fromEnum { values() }
    }
}
