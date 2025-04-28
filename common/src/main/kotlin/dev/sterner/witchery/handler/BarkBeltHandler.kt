package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment.getData
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment.setData
import dev.sterner.witchery.registry.WitcheryTags
import dev.sterner.witchery.util.RenderUtils
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object BarkBeltHandler {

    const val TIME_TO_RECHARGE = 20 * 5

    /**
     * Modifies the incoming damage if the player has a bark belt equipped and charged. Will absorb some of the damage.
     */
    fun hurt(livingEntity: LivingEntity?, damageSource: DamageSource, damage: Float): Float {
        if (livingEntity is Player) {
            val data = getData(livingEntity)

            if (damageSource.entity is LivingEntity) {
                val living = damageSource.entity as LivingEntity
                if (living.mainHandItem.`is`(WitcheryTags.WOODEN_WEAPONS)) {
                    setData(livingEntity, data.copy(currentBark = 0))
                    return damage
                }
            }

            if (data.currentBark > 0) {
                val absorbedDamage = (damage / 2).coerceAtMost(data.currentBark.toFloat())
                val newCharge = (data.currentBark - absorbedDamage).toInt()
                val remainingDamage = damage - absorbedDamage

                setData(livingEntity, data.copy(currentBark = newCharge, tickCounter = 0))

                return remainingDamage
            }
        }

        return damage
    }

    /**
     * Recharges the bark belt
     */
    fun tick(player: Player?) {
        if (player is ServerPlayer) {
            val data = getData(player)
            val newTickCounter = data.tickCounter + 1

            if (newTickCounter >= TIME_TO_RECHARGE && data.currentBark < data.maxBark) {
                val newCharge = (data.currentBark + data.rechargeRate).coerceAtMost(data.maxBark)
                setData(player, data.copy(currentBark = newCharge, tickCounter = 0))
            } else {
                setData(player, data.copy(tickCounter = newTickCounter))
            }
        }
    }

    /**
     * Render the bark belt HUD
     */
    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker?) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val bl = client.gameMode!!.canHurtPlayer()
        if (!bl) {
            return
        }

        val bl2 = TransformationHandler.isBat(player)
        val bl3 = player.armorValue > 0
        val y = guiGraphics.guiHeight() - 18 - 18 - 12 - (if (bl3) 10 else 0) - (if (bl2) 8 else 0)
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 3

        val bark = getData(player)
        if (bark.maxBark > 0) {
            for (i in 0 until bark.maxBark) {
                RenderUtils.blitWithAlpha(
                    guiGraphics.pose(),
                    Witchery.id("textures/gui/bark_empty.png"),
                    x + i * 8,
                    y,
                    0f,
                    0f,
                    8,
                    8,
                    8,
                    8
                )
            }
            for (i in 0 until bark.currentBark) {
                RenderUtils.blitWithAlpha(
                    guiGraphics.pose(),
                    Witchery.id("textures/gui/bark_full.png"),
                    x + i * 8,
                    y,
                    0f,
                    0f,
                    8,
                    8,
                    8,
                    8
                )
            }
        }
    }
}