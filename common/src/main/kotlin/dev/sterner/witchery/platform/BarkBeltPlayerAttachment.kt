package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.util.RenderUtils
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.payload.SyncBarkS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object BarkBeltPlayerAttachment {

    const val TIME_TO_RECHARGE = 20 * 5

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncBarkS2CPacket(player, data))
        }
    }

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


            //guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.literal("${bark.currentBark} / ${bark.maxBark}"), x, y, -1)
        }
    }

    data class Data(
        val currentBark: Int = 0,
        val maxBark: Int = 0,
        val rechargeRate: Int = 1,
        val tickCounter: Int = 0
    ) {

        companion object {
            val ID: ResourceLocation = Witchery.id("player_bark_belt")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("currentBark").forGetter { it.currentBark },
                    Codec.INT.fieldOf("maxBark").forGetter { it.maxBark },
                    Codec.INT.fieldOf("rechargeRate").forGetter { it.rechargeRate },
                    Codec.INT.fieldOf("tickCounter").forGetter { it.tickCounter }
                ).apply(instance, ::Data)
            }
        }
    }
}