package dev.sterner.witchery.handler.ability

import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.entity.player.Player

enum class WerewolfAbility(override val unlockLevel: Int, override val cooldown: Int) : PlayerAbilityType {
    FREE_WOLF_TRANSFORM(5, 20 * 2) {
        override fun canUnlock(player: Player): Boolean {
            return player.hasMoonCharm()
        }
    },
    FREE_WEREWOLF_TRANSITION(5, 20 * 2) {
        override fun canUnlock(player: Player): Boolean {
            return player.hasMoonCharm()
        }
    };

    fun Player.hasMoonCharm(): Boolean {
        return PlatformUtils.allEquippedAccessories(this).map { it.item }.contains(WitcheryItems.MOON_CHARM.get())
    }

    override val id: String get() = name.lowercase()

    override fun isAvailable(level: Int): Boolean {
        return level >= unlockLevel
    }

    open fun canUnlock(player: Player): Boolean {
        return true
    }

    fun isAvailable(player: Player): Boolean {
        return isAvailable(WerewolfPlayerAttachment.getData(player).getWerewolfLevel()) && canUnlock(player)
    }
}