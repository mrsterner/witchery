package dev.sterner.witchery.content.block.effigy

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class EffigyState : StringRepresentable {
    IDLE,                               // Deactivated
    PLAYER_UNKNOWN_TAGLOCK_NEAR,        // Activate if a player is nearby whose taglock is unknown
    PLAYER_KNOWN_TAGLOCK_NEAR,          // Activate if a player is nearby whose taglock is known
    ENTITY_UNKNOWN_TAGLOCK_NEAR,        // Activate if a player or creature is nearby whose taglock is unknown
    ANY_KNOWN_TAGLOCK_ENTITY_NEAR,      // Activate when some known creatures are nearby
    ALL_KNOWN_TAGLOCK_ENTITY_AWAY;      // Activate when all known creatures are not nearby

    companion object {
        val CODEC: Codec<EffigyState> = Codec.STRING.xmap(
            { name -> valueOf(name.uppercase()) },
            { it.name.lowercase() }
        )
    }

    override fun getSerializedName(): String {
        return name.lowercase()
    }
}
