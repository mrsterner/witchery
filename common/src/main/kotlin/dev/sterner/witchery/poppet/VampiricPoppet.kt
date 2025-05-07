package dev.sterner.witchery.poppet

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.handler.poppet.PoppetLocation
import dev.sterner.witchery.handler.poppet.PoppetHandler
import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetUsage
import dev.sterner.witchery.platform.poppet.PoppetLevelAttachment
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class VampiricPoppet : PoppetType {
    override val item = WitcheryItems.VAMPIRIC_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean = true

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        return false
    }

    fun handleDamage(target: LivingEntity, damageSource: DamageSource, damage: Float): Float {
        val (poppet, location) = PoppetHandler.findPoppet(target, this) ?: return damage

        if (poppet != null) {
            val level = target.level()
            val boundPlayer = PoppetHandler.getBoundPlayer(level, poppet)
            val serverLevel = level as? ServerLevel
            val boundEntity = serverLevel?.let { PoppetHandler.getBoundEntity(it, poppet) }

            if (boundPlayer != null || boundEntity != null) {
                var outDamage = damage

                if (boundPlayer is Player && !WitcheryApi.isWitchy(boundPlayer)) {
                    boundPlayer.hurt(damageSource, outDamage * 0.75f)
                    boundEntity?.hurt(damageSource, outDamage * 0.25f)
                } else {
                    outDamage /= 2
                    boundPlayer?.hurt(damageSource, outDamage)
                    boundEntity?.hurt(damageSource, outDamage)
                }

                // Apply durability damage based on location
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

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when(usage) {
        PoppetUsage.DAMAGE -> 1
        else -> 0
    }
}