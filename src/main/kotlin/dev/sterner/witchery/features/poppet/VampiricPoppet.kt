package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.core.api.PoppetLocation
import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.features.poppet.PoppetHandler
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.hunter.HunterArmorDefenseHandler
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class VampiricPoppet : PoppetType {
    override val item = WitcheryItems.VAMPIRIC_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean = true

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        return false
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner !is Player) return false

        owner.hurt(owner.damageSources().magic(), 2.0f)

        owner.addEffect(MobEffectInstance(MobEffects.HUNGER, 200, 1))

        if (owner.level() is ServerLevel) {
            val serverLevel = owner.level() as ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                owner.x, owner.y + owner.bbHeight * 0.5, owner.z,
                10, 0.2, 0.2, 0.2, 0.05
            )
        }

        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.WITCH_DRINK,
            SoundSource.PLAYERS,
            0.7f,
            0.8f
        )

        owner.displayClientMessage(
            Component.translatable("curse.witchery.corrupt_poppet.vampiric_effect")
                .withStyle(ChatFormatting.DARK_PURPLE),
            true
        )

        return true
    }

    fun handleDamage(target: LivingEntity, damageSource: DamageSource, damage: Float): Float {
        val (poppet, location) = PoppetHandler.findPoppet(target, this)

        if (poppet != null) {
            val level = target.level()
            val boundPlayer = PoppetHandler.getBoundPlayer(level, poppet)
            val serverLevel = level as? ServerLevel
            val boundEntity = serverLevel?.let { PoppetHandler.getBoundEntity(it, poppet) }

            if (boundPlayer is Player) {
                val corruptData = CorruptPoppetPlayerAttachment.getData(boundPlayer)
                if (corruptData.corruptedPoppets.contains(getRegistryId())) {
                    val amplifiedDamage = damage * 1.5f

                    serverLevel?.sendParticles(
                        ParticleTypes.WITCH,
                        target.x, target.y + target.bbHeight * 0.5, target.z,
                        15, 0.3, 0.3, 0.3, 0.05
                    )

                    when (location) {
                        PoppetLocation.ACCESSORY, PoppetLocation.INVENTORY -> {
                            poppet.damageValue += getDurabilityDamage(PoppetUsage.DAMAGE)
                            if (poppet.damageValue >= poppet.maxDamage) {
                                poppet.shrink(1)
                            }
                        }

                        PoppetLocation.WORLD -> {
                            if (level is ServerLevel) {
                                val poppetData = PoppetLevelAttachment.getPoppetData(level)
                                val blockPoppet = poppetData.poppetDataMap.find {
                                    it.poppetItemStack == poppet
                                }

                                if (blockPoppet != null) {
                                    blockPoppet.poppetItemStack.damageValue += getDurabilityDamage(PoppetUsage.DAMAGE)
                                    if (blockPoppet.poppetItemStack.damageValue >= blockPoppet.poppetItemStack.maxDamage) {
                                        blockPoppet.poppetItemStack.shrink(1)
                                    }
                                    PoppetLevelAttachment.updatePoppetItem(
                                        level,
                                        blockPoppet.blockPos,
                                        blockPoppet.poppetItemStack
                                    )
                                }
                            }
                        }

                        null -> {}
                    }

                    if (target is Player) {
                        target.displayClientMessage(
                            Component.translatable("curse.witchery.corrupt_poppet.vampiric_damage")
                                .withStyle(ChatFormatting.DARK_PURPLE),
                            true
                        )
                    }

                    return amplifiedDamage
                }
            }

            if (boundPlayer != null || boundEntity != null) {
                val outDamage = damage

                val (playerShare, boundShare) =
                    if (boundPlayer is Player && !WitcheryApi.isWitchy(boundPlayer)) {
                        0.75f to 0.25f
                    } else {
                        0.5f to 0.5f
                    }

                val baseDamageToPlayer = outDamage * playerShare
                val baseDamageToBound = outDamage * boundShare

                boundPlayer?.hurt(damageSource, baseDamageToPlayer)

                boundEntity?.hurt(damageSource, baseDamageToBound)

                when (location) {
                    PoppetLocation.ACCESSORY, PoppetLocation.INVENTORY -> {
                        poppet.damageValue += getDurabilityDamage(PoppetUsage.DAMAGE)
                        if (poppet.damageValue >= poppet.maxDamage) {
                            poppet.shrink(1)
                        }
                    }

                    PoppetLocation.WORLD -> {
                        if (level is ServerLevel) {
                            val poppetData = PoppetLevelAttachment.getPoppetData(level)
                            val blockPoppet = poppetData.poppetDataMap.find {
                                it.poppetItemStack == poppet
                            }

                            if (blockPoppet != null) {
                                blockPoppet.poppetItemStack.damageValue += getDurabilityDamage(PoppetUsage.DAMAGE)
                                if (blockPoppet.poppetItemStack.damageValue >= blockPoppet.poppetItemStack.maxDamage) {
                                    blockPoppet.poppetItemStack.shrink(1)
                                }
                                PoppetLevelAttachment.updatePoppetItem(
                                    level,
                                    blockPoppet.blockPos,
                                    blockPoppet.poppetItemStack
                                )
                            }
                        }
                    }

                    null -> {}
                }

                return outDamage
            }
        }

        return damage
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when (usage) {
        PoppetUsage.DAMAGE -> 1
        PoppetUsage.PROTECTION -> 1
        else -> 0
    }
}
