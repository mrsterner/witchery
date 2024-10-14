package dev.sterner.witchery.block.ritual

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class CommandContext : StringRepresentable {
    NOTHING,
    PLAYER,
    PLAYER_OR_ENTITY,
    ENTITY,
    BLOCKPOS;

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CODEC: Codec<CommandContext> = StringRepresentable.fromEnum(::values)
    }
}