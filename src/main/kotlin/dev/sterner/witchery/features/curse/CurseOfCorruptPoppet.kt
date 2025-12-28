package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.PoppetLocation
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import dev.sterner.witchery.features.poppet.PoppetHandler
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.features.poppet.CorruptPoppetPlayerAttachment
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfCorruptPoppet : Curse() {

    companion object {
        const val MAX_CORRUPTED_POPPETS = 10
        private val CORRUPTED_POPPET_PARTICLES = ParticleTypes.WITCH
    }

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0

        val baseInterval = if (WitcheryApi.isWitchy(player)) {
            100L
        } else {
            300L
        }

        val tickReduction = (witchPower * 1L).coerceAtMost(50L)
        val adjustedInterval = (baseInterval - tickReduction).coerceAtLeast(50L)

        if (level.gameTime % adjustedInterval == 0L) {
            attemptToCorruptPoppet(level, player)
        }

        if (level.isClientSide && level.random.nextFloat() < 0.1f) {
            showCorruptedPoppetParticles(level, player)
        }
    }

    override fun onHurt(
        level: Level,
        player: Player,
        damageSource: DamageSource?,
        fl: Float,
        catBoosted: Boolean
    ) {
        super.onHurt(level, player, damageSource, fl, catBoosted)

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0

        val baseChance = if (WitcheryApi.isWitchy(player)) {
            0.2f
        } else {
            0.05f
        }

        val witchPowerBonus = (witchPower * 0.005f).coerceAtMost(0.05f)
        val corruptChance = baseChance + witchPowerBonus

        if (level.random.nextFloat() < corruptChance) {
            attemptToCorruptPoppet(level, player)
        }
    }

    override fun onRemoved(level: Level, player: Player, catBoosted: Boolean) {
        super.onRemoved(level, player, catBoosted)

        val corruptData = CorruptPoppetPlayerAttachment.getData(player)
        if (corruptData.corruptedPoppetCount > 0) {
            CorruptPoppetPlayerAttachment.setData(player, CorruptPoppetPlayerAttachment.Data())
            CorruptPoppetPlayerAttachment.sync(player, CorruptPoppetPlayerAttachment.Data())

            if (level is ServerLevel) {
                level.sendParticles(
                    ParticleTypes.ENCHANT,
                    player.x,
                    player.y + 1.0,
                    player.z,
                    30,
                    0.5,
                    0.5,
                    0.5,
                    0.1
                )
            }

            player.displayClientMessage(
                Component.translatable("curse.witchery.corrupt_poppet.cleansed").withStyle(
                    ChatFormatting.GREEN
                ), true
            )
        }
    }

    override fun onAdded(level: Level, player: Player, catBoosted: Boolean) {
        super.onAdded(level, player, catBoosted)

        CorruptPoppetPlayerAttachment.setData(player, CorruptPoppetPlayerAttachment.Data())
        CorruptPoppetPlayerAttachment.sync(player, CorruptPoppetPlayerAttachment.Data())

        player.displayClientMessage(
            Component.translatable("curse.witchery.corrupt_poppet.applied").withStyle(ChatFormatting.DARK_PURPLE), true
        )

        if (level is ServerLevel) {
            level.sendParticles(
                ParticleTypes.WITCH,
                player.x,
                player.y + 1.0,
                player.z,
                20,
                0.5,
                0.5,
                0.5,
                0.1
            )
        }
    }

    private fun attemptToCorruptPoppet(level: Level, player: Player) {
        if (level !is ServerLevel) return

        val corruptData = CorruptPoppetPlayerAttachment.getData(player)

        if (corruptData.corruptedPoppetCount >= MAX_CORRUPTED_POPPETS) return

        val allPoppets = mutableListOf<Pair<PoppetType, PoppetLocation>>()

        WitcheryPoppetRegistry.POPPET_REGISTRY.forEach { poppetType ->
            val registryId = poppetType.getRegistryId()

            if (poppetType.canBeCorrupted() &&
                registryId != null &&
                !corruptData.corruptedPoppets.contains(registryId)) {

                val (poppet, location) = PoppetHandler.findPoppet(player, poppetType)
                if (poppet != null && location != null) {
                    allPoppets.add(Pair(poppetType, location))
                }
            }
        }

        if (allPoppets.isEmpty()) return

        val (selectedPoppet, _) = allPoppets.random()
        val selectedPoppetId = selectedPoppet.getRegistryId() ?: return

        val updatedCorruptedPoppets = corruptData.corruptedPoppets.toMutableSet()
        updatedCorruptedPoppets.add(selectedPoppetId)

        val updatedData = corruptData.copy(
            corruptedPoppetCount = corruptData.corruptedPoppetCount + 1,
            corruptedPoppets = updatedCorruptedPoppets
        )

        CorruptPoppetPlayerAttachment.setData(player, updatedData)
        CorruptPoppetPlayerAttachment.sync(player, updatedData)

        level.playSound(
            null,
            player.x, player.y, player.z,
            SoundEvents.WITCH_CELEBRATE,
            SoundSource.PLAYERS,
            1.0f,
            0.5f + level.random.nextFloat() * 0.5f
        )

        level.sendParticles(
            CORRUPTED_POPPET_PARTICLES,
            player.x,
            player.y + 1.0,
            player.z,
            15,
            0.5,
            0.5,
            0.5,
            0.1
        )

        player.displayClientMessage(
            Component.translatable(
                "curse.witchery.corrupt_poppet.corrupted",
                Component.translatable(selectedPoppet.item.descriptionId)
            ).withStyle(ChatFormatting.DARK_PURPLE),
            true
        )
    }

    private fun showCorruptedPoppetParticles(level: Level, player: Player) {
        if (!level.isClientSide) return

        val corruptData = CorruptPoppetPlayerAttachment.getData(player)
        if (corruptData.corruptedPoppetCount <= 0) return

        WitcheryPoppetRegistry.POPPET_REGISTRY.forEach { poppetType ->
            if (corruptData.corruptedPoppets.contains(poppetType.getRegistryId())) {
                val (poppet, location) = PoppetHandler.findPoppet(player, poppetType)
                if (poppet != null) {
                    when (location) {
                        PoppetLocation.ACCESSORY -> {
                            level.addParticle(
                                CORRUPTED_POPPET_PARTICLES,
                                player.x + (level.random.nextFloat() - 0.5) * 0.5,
                                player.y + level.random.nextFloat() * player.bbHeight,
                                player.z + (level.random.nextFloat() - 0.5) * 0.5,
                                0.0, 0.0, 0.0
                            )
                        }

                        PoppetLocation.INVENTORY -> {
                            if (level.random.nextFloat() < 0.3f) {
                                level.addParticle(
                                    CORRUPTED_POPPET_PARTICLES,
                                    player.x + (level.random.nextFloat() - 0.5) * 0.3,
                                    player.y + 0.5 + level.random.nextFloat() * 0.5,
                                    player.z + (level.random.nextFloat() - 0.5) * 0.3,
                                    0.0, 0.0, 0.0
                                )
                            }
                        }

                        PoppetLocation.WORLD -> {
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}