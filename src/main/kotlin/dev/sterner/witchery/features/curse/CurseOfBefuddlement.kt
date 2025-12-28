package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import dev.sterner.witchery.network.SpawnPortalParticlesS2CPayload
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

class CurseOfBefuddlement : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        if(level.isClientSide) return

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0

        val effectivenessMultiplier = getEffectivenessMultiplier(player)
        val witchPowerAmplifier = 1.0f + (witchPower * 0.01f).coerceAtMost(0.1f)
        val totalMultiplier = effectivenessMultiplier * witchPowerAmplifier

        val baseInterval = if (WitcheryApi.isWitchy(player)) {
            240L
        } else {
            420L
        }

        if (level.gameTime % baseInterval == 0L) {
            val nauseaChance = 0.18f * totalMultiplier
            val confusionDuration = (20 * 14 * totalMultiplier).toInt()

            if (level.random.nextFloat() < nauseaChance) {
                player.addEffect(MobEffectInstance(MobEffects.CONFUSION, confusionDuration, 0))
            }

            val miningFatigueChance = nauseaChance * 0.35f
            val miningFatigueDuration = (20 * (14 + 8) * totalMultiplier).toInt()

            if (level.random.nextFloat() < miningFatigueChance) {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, miningFatigueDuration, 0))
            }
        }

        if (level.random.nextFloat() < 0.05f * totalMultiplier) {
            if (player.level() is ServerLevel) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    player, SpawnPortalParticlesS2CPayload(
                        CompoundTag().apply {
                            putUUID("Id", player.uuid)
                        }
                    ))
            }
        }
    }

    override fun onAdded(level: Level, player: Player, catBoosted: Boolean) {
        super.onAdded(level, player, catBoosted)

        player.displayClientMessage(
            Component.translatable("curse.witchery.befuddlement.applied")
                .withStyle(ChatFormatting.DARK_PURPLE),
            true
        )
    }
}