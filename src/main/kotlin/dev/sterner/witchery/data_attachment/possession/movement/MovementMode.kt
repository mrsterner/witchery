package dev.sterner.witchery.data_attachment.possession.movement

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable


enum class MovementMode(private val serializedName: String) : StringRepresentable {
    DISABLED("disabled"),
    ENABLED("enabled"),
    FORCED("forced"),
    UNSPECIFIED("unspecified");

    override fun getSerializedName(): String = serializedName.lowercase()

    companion object {
        val CODEC: Codec<MovementMode> = StringRepresentable.fromEnum { values() }
    }
}
