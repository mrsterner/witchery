package dev.sterner.witchery.data_attachment.possession.movement

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class TriState(private val serializedName: String) : StringRepresentable {
    TRUE("true"),
    FALSE("false"),
    DEFAULT("default");

    override fun getSerializedName(): String = serializedName.lowercase()

    companion object {
        val CODEC: Codec<TriState> = StringRepresentable.fromEnum { entries.toTypedArray() }
    }
}