package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.registry.WitcheryPoppetRegistry
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

        if (level.gameTime % 100L == 0L) {
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
        damage: Float,
        catBoosted: Boolean
    ) {
        super.onHurt(level, player, damageSource, damage, catBoosted)

        if (level.random.nextFloat() < 0.2f) {
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

            player.displayClientMessage(Component.translatable("curse.witchery.corrupt_poppet.cleansed").withStyle(
                ChatFormatting.GREEN), true)
        }
    }

    override fun onAdded(level: Level, player: Player, catBoosted: Boolean) {
        super.onAdded(level, player, catBoosted)

        CorruptPoppetPlayerAttachment.setData(player, CorruptPoppetPlayerAttachment.Data())
        CorruptPoppetPlayerAttachment.sync(player, CorruptPoppetPlayerAttachment.Data())

        player.displayClientMessage(Component.translatable("curse.witchery.corrupt_poppet.applied").withStyle(ChatFormatting.DARK_PURPLE), true)

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

        WitcheryPoppetRegistry.getAllTypes().forEach { poppetType ->
            if (poppetType.canBeCorrupted() && !corruptData.corruptedPoppets.contains(poppetType.getRegistryId())) {
                val (poppet, location) = PoppetHandler.findPoppet(player, poppetType)
                if (poppet != null && location != null) {
                    allPoppets.add(Pair(poppetType, location))
                }
            }
        }

        if (allPoppets.isEmpty()) return

        val (selectedPoppet, _) = allPoppets.random()

        val updatedData = corruptData.copy(
            corruptedPoppetCount = corruptData.corruptedPoppetCount + 1,
            corruptedPoppets = corruptData.corruptedPoppets.apply {
                add(selectedPoppet.getRegistryId())
            }
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
            Component.translatable("curse.witchery.corrupt_poppet.corrupted",
                Component.translatable(selectedPoppet.item.descriptionId)
            ).withStyle(ChatFormatting.DARK_PURPLE),
            true
        )
    }

    private fun showCorruptedPoppetParticles(level: Level, player: Player) {
        if (!level.isClientSide) return

        val corruptData = CorruptPoppetPlayerAttachment.getData(player)
        if (corruptData.corruptedPoppetCount <= 0) return

        WitcheryPoppetRegistry.getAllTypes().forEach { poppetType ->
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