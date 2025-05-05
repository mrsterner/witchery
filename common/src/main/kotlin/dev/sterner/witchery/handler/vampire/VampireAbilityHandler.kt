package dev.sterner.witchery.handler.vampire

import dev.architectury.event.EventResult
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.handler.ability.AbilityHandler
import dev.sterner.witchery.handler.ability.AbilityScrollHandler
import dev.sterner.witchery.handler.ability.VampireAbility
import dev.sterner.witchery.payload.VampireAbilitySelectionC2SPayload
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

object VampireAbilityHandler : AbilityHandler<VampireAbility> {

    override val abilityIndex: Int
        get() = VampirePlayerAttachment.getData(Minecraft.getInstance().player!!).abilityIndex

    override fun getLevel(player: Player): Int =
        VampirePlayerAttachment.getData(player).getVampireLevel()

    override fun getAbilities(player: Player): List<VampireAbility> =
        VampireAbility.entries.filter { it.isAvailable(getLevel(player)) }

    override fun setAbilityIndex(player: Player, index: Int) {
        updateAbilityIndex(player, index)
        if (player.level().isClientSide) {
            NetworkManager.sendToServer(VampireAbilitySelectionC2SPayload(index))
        }
    }

    override fun updateAbilityIndex(player: Player, index: Int) {
        val data = VampirePlayerAttachment.getData(player)
        VampirePlayerAttachment.setData(player, data.copy(abilityIndex = index))
    }

    /**
     * This will increase when the vampire-player is exposed to the sun, used to damage it
     */
    @JvmStatic
    fun increaseInSunTick(player: Player) {
        val data = VampirePlayerAttachment.getData(player)
        val newInSunTick = (data.inSunTick + 1).coerceAtMost(20 * 5)
        VampirePlayerAttachment.setData(player, data.copy(inSunTick = newInSunTick))
    }

    /**
     * This is decreasing the accumulated ticks when then vampire-player is in hiding from the sun
     */
    @JvmStatic
    fun decreaseInSunTick(player: Player, amount: Int = 1) {
        val data = VampirePlayerAttachment.getData(player)
        val newInSunTick = (data.inSunTick - amount).coerceAtLeast(0)
        VampirePlayerAttachment.setData(player, data.copy(inSunTick = newInSunTick))
    }

    //Vampire Ability Section
    /**
     * Set to true to enable the vampire-player's night vision status effect
     */
    @JvmStatic
    fun setNightVision(player: Player, active: Boolean) {
        val data = VampirePlayerAttachment.getData(player)
        val newNightVisionData = data.copy(isNightVisionActive = active)
        VampirePlayerAttachment.setData(player, newNightVisionData)
    }

    /**
     * Toggles the effect on/off
     */
    @JvmStatic
    fun toggleNightVision(player: Player) {
        setNightVision(player, !VampirePlayerAttachment.getData(player).isNightVisionActive)
    }

    /**
     * Set to true to enable the vampire-player's speed boost status effect
     */
    @JvmStatic
    fun setSpeedBoost(player: Player, active: Boolean) {
        val data = VampirePlayerAttachment.getData(player)
        val newSpeedBoostData = data.copy(isSpeedBoostActive = active)
        VampirePlayerAttachment.setData(player, newSpeedBoostData)
    }

    /**
     * Toggles the effect on/off
     */
    @JvmStatic
    fun toggleSpeedBoost(player: Player) {
        setSpeedBoost(player, !VampirePlayerAttachment.getData(player).isSpeedBoostActive)
    }

    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult {
        val player = minecraft?.player ?: return EventResult.pass()

        // Get player's current abilities
        val abilities = getAbilities(player)
        if (abilities.isEmpty()) return EventResult.pass()

        // Handle scrolling with modified logic
        return AbilityScrollHandler().handleScroll(player, y, this)
    }
}