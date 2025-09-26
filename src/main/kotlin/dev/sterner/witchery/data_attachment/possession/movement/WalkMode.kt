package dev.sterner.witchery.data_attachment.possession.movement

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class WalkMode(private val serializedName: String) : StringRepresentable {
    DISABLED("disabled"),
    NORMAL("normal"),
    UNSPECIFIED("unspecified");

    override fun getSerializedName(): String = serializedName.lowercase()

    companion object {
        val CODEC: Codec<WalkMode> = StringRepresentable.fromEnum { values() }
    }
}