package dev.sterner.witchery.handler.vampire

import dev.architectury.event.EventResult
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.payload.VampireAbilitySelectionC2SPayload
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object VampireAbilities {

    private var abilityIndex = -1 // -1 means player is in the hotbar, not abilities

    /**
     * Handles the selection of abilities with the scroll wheel
     */
    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult? {
        val player = minecraft?.player
        if (minecraft == null || player == null) {
            return EventResult.pass()
        }

        val abilityCount = getAbilities(player).size
        if (abilityCount == 0) {
            return EventResult.pass()
        }

        if (abilityIndex == -1) {
            if (player.inventory.selected == 0 && y > 0.0) {
                abilityIndex = 0
                setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                abilityIndex = abilityCount - 1
                setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            }
        } else {
            if (y > 0.0) {
                if (abilityIndex < abilityCount - 1) {
                    abilityIndex++
                    setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 8
                    abilityIndex = -1
                    setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            } else if (y < 0.0) {
                if (abilityIndex > 0) {
                    abilityIndex--
                    setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 0
                    abilityIndex = -1
                    setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            }
        }

        return EventResult.pass()
    }
    //Misc Vampire logic

    /**
     * This will increase when the vampire-player is exposed to the sun, used to damage it
     */
    @JvmStatic
    fun increaseInSunTick(player: Player) {
        val data = getData(player)
        val newInSunTick = (data.inSunTick + 1).coerceAtMost(20 * 5)
        setData(player, data.copy(inSunTick = newInSunTick))
    }

    /**
     * This is decreasing the accumulated ticks when then vampire-player is in hiding from the sun
     */
    @JvmStatic
    fun decreaseInSunTick(player: Player, amount: Int = 1) {
        val data = getData(player)
        val newInSunTick = (data.inSunTick - amount).coerceAtLeast(0)
        setData(player, data.copy(inSunTick = newInSunTick))
    }

    //Vampire Ability Section

    /**
     * This is used to select which ability is the selected one
     */
    @JvmStatic
    fun setAbilityIndex(player: Player, abilityIndex: Int) {
        updateAbilityIndex(player, abilityIndex)
        NetworkManager.sendToServer(VampireAbilitySelectionC2SPayload(abilityIndex))
    }

    @JvmStatic
    fun updateAbilityIndex(player: Player, abilityIndex: Int){
        val data = getData(player)
        setData(player, data.copy(abilityIndex = abilityIndex))
    }

    /**
     * Get all the available abilities for the current vampire level
     */
    @JvmStatic
    fun getAbilities(player: Player): List<VampireAbility> {
        val level = getData(player).vampireLevel
        return VampireAbility.entries.filter { it.unlockLevel <= level }
    }

    /**
     * Set to true to enable the vampire-player's night vision status effect
     */
    @JvmStatic
    fun setNightVision(player: Player, active: Boolean) {
        val data = getData(player)
        val newNightVisionData = data.copy(isNightVisionActive = active)
        setData(player, newNightVisionData)
    }

    /**
     * Toggles the effect on/off
     */
    @JvmStatic
    fun toggleNightVision(player: Player) {
        setNightVision(player, !getData(player).isNightVisionActive)
    }

    /**
     * Set to true to enable the vampire-player's speed boost status effect
     */
    @JvmStatic
    fun setSpeedBoost(player: Player, active: Boolean) {
        val data = getData(player)
        val newSpeedBoostData = data.copy(isSpeedBoostActive = active)
        setData(player, newSpeedBoostData)
    }

    /**
     * Toggles the effect on/off
     */
    @JvmStatic
    fun toggleSpeedBoost(player: Player) {
        setSpeedBoost(player, !getData(player).isSpeedBoostActive)
    }

    /**
     * Set to true to enable the vampire-player's bat-form ability
     */
    @JvmStatic
    fun setBatForm(player: Player, active: Boolean) {
        val data = getData(player)
        val newBatFormData = data.copy(isBatFormActive = active)
        setData(player, newBatFormData)
    }

    /**
     * Toggles the form on/off
     */
    @JvmStatic
    fun toggleBatForm(player: Player) {
        setBatForm(player, !getData(player).isBatFormActive)
    }
}