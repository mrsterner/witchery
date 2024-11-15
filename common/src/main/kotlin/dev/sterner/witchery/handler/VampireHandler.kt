package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.payload.SpawnBloodParticlesS2CPayload
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import dev.sterner.witchery.registry.WitcheryDamageSources
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.min

object VampireHandler {

    private val overlay = Witchery.id("textures/gui/ability_hotbar_selection.png")
    private val sun = Witchery.id("textures/gui/vampire_abilities/sun_")
    private var abilityIndex = -1 // -1 means player is in the hotbar, not abilities
    private var bloodTransferAmount = 10
    private var bloodThreshold = 75


    fun scroll(minecraft: Minecraft?, x: Double, y: Double): EventResult? {
        val player = minecraft?.player
        if (minecraft == null || player == null) {
            return EventResult.pass()
        }

        val abilityCount = VampirePlayerAttachment.getAbilities(player).size
        if (abilityCount == 0) {
            return EventResult.pass()
        }

        if (abilityIndex == -1) {
            if (player.inventory.selected == 0 && y > 0.0) {
                abilityIndex = 0
                VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            } else if (player.inventory.selected == 8 && y < 0.0) {
                abilityIndex = abilityCount - 1
                VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                return EventResult.interruptFalse()
            }
        } else {
            if (y > 0.0) {
                if (abilityIndex < abilityCount - 1) {
                    abilityIndex++
                    VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 8
                    abilityIndex = -1
                    VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            } else if (y < 0.0) {
                if (abilityIndex > 0) {
                    abilityIndex--
                    VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                } else {
                    player.inventory.selected = 0
                    abilityIndex = -1
                    VampirePlayerAttachment.setAbilityIndex(player, abilityIndex)
                }
                return EventResult.interruptFalse()
            }
        }

        return EventResult.pass()
    }


    fun handleHurt(player: LivingEntity, damageSource: DamageSource, original: Float): Float {

        if (player !is Player) {
            return original
        }

        val vampData = getData(player)
        if (vampData.vampireLevel < 1) {
            return original
        }

        val bloodData = BloodPoolLivingEntityAttachment.getData(player)
        if (bloodData.bloodPool > 0) {
            val bloodPerHealthPoint = bloodThreshold
            val maxBloodAbsorbableDamage = bloodData.bloodPool / bloodPerHealthPoint

            val absorbableDamage = minOf(original * 0.75f, maxBloodAbsorbableDamage.toFloat())
            val bloodRequired = (absorbableDamage * bloodPerHealthPoint).toInt()

            BloodPoolLivingEntityAttachment.setData(player, bloodData.copy(bloodPool = bloodData.bloodPool - bloodRequired))

            return original * 0.25f
        }

        return original
    }

    fun tick(player: Player?) {
        if (player == null || player.level().isClientSide) {
            return
        }

        val vampData = getData(player)
        if (vampData.vampireLevel < 1) {
            return
        }

        if (player.isAlive) {
            val isInSunlight = player.level().canSeeSky(player.blockPosition()) && player.level().isDay
            val sunDamageSource = player.level().damageSources().source(WitcheryDamageSources.IN_SUN)
            val bloodData = BloodPoolLivingEntityAttachment.getData(player)

            if (isInSunlight) {
                VampirePlayerAttachment.increaseInSunTick(player)

                if (getData(player).inSunTick >= 100) {
                    if (vampData.vampireLevel < 5) {
                        player.hurt(sunDamageSource, Float.MAX_VALUE)
                    } else {
                        if (bloodData.bloodPool >= bloodThreshold) {
                            //BloodPoolLivingEntityAttachment.decreaseBlood(player, 10)
                            if (player.level().random.nextFloat() > 0.75f) {
                                player.hurt(sunDamageSource, 2f)
                                if (player.level().random.nextFloat() > 0.95f) {
                                    player.level().playSound(null, player.x, player.y, player.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS , 0.5f, 1.0f)
                                    WitcheryPayloads.sendToPlayers(player.level(), SpawnBloodParticlesS2CPayload(player, player.position().add(0.5, 0.5, 0.5)))
                                }
                            }
                        } else {
                            player.hurt(sunDamageSource, Float.MAX_VALUE)
                        }
                    }
                }
            } else {
                VampirePlayerAttachment.decreaseInSunTick(player)
            }

            if (bloodData.bloodPool >= 75 && player.level().random.nextBoolean()) {
                if (player.health < player.maxHealth && player.health > 0) {
                    BloodPoolLivingEntityAttachment.decreaseBlood(player, 75)
                    player.heal(1f)
                }
            }
        }
    }


    @JvmStatic
    fun interactEntity(player: Player?, entity: Entity?, interactionHand: InteractionHand?): EventResult? {
        if (player != null && entity is LivingEntity && !player.level().isClientSide) {
            val playerData = getData(player)
            val playerBloodData = BloodPoolLivingEntityAttachment.getData(player)
            if (playerData.abilityIndex == VampirePlayerAttachment.VampireAbility.DRINK_BLOOD.ordinal) {
                val targetData = BloodPoolLivingEntityAttachment.getData(entity)
                val quality = BloodPoolHandler.BLOOD_PAIR[entity.type] ?: 0

                if (playerBloodData.bloodPool < playerBloodData.maxBlood && targetData.bloodPool >= 0 && targetData.maxBlood > 0) {

                    if (quality == 0 && player.level().random.nextFloat() < 0.25f) {
                        player.addEffect(MobEffectInstance(MobEffects.POISON, 200, 0))
                    }

                    if (quality == 1 && playerBloodData.bloodPool >= playerBloodData.maxBlood / 2) {
                        return EventResult.pass()
                    }

                    player.level().playSound(null, entity.x, entity.y, entity.z, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS)
                    val np = entity.position().add(0.5, 0.5, 0.5)
                    WitcheryPayloads.sendToPlayers(player.level(), SpawnBloodParticlesS2CPayload(player, np))

                    BloodPoolLivingEntityAttachment.decreaseBlood(entity, bloodTransferAmount)
                    BloodPoolLivingEntityAttachment.increaseBlood(player, bloodTransferAmount)

                    if (targetData.bloodPool < targetData.maxBlood / 2) {
                        entity.hurt(player.damageSources().playerAttack(player), 2f)
                    }
                    if (targetData.bloodPool <= 0) {
                        entity.kill()
                    }

                    return EventResult.interruptFalse()
                }
            }
        }
        return EventResult.pass()
    }

    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {

        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val abilityIndex = getData(player).abilityIndex
        val size = VampirePlayerAttachment.getAbilities(player)

        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5

        drawBloodSense(guiGraphics)
        drawSun(guiGraphics, player)

        val bl = player.isShiftKeyDown

        for (i in size.indices) {
            var name = size[i].serializedName
            if (size[i] == VampirePlayerAttachment.VampireAbility.TRANSFIX && bl) {
                name = "night_vision"
            }
            guiGraphics.blit(Witchery.id("textures/gui/vampire_abilities/${name}.png"), x - (25 * i) + 4, y + 4, 16, 16, 0f,0f,16, 16,16, 16)
        }

        if (abilityIndex != -1) {
            guiGraphics.blit(overlay, x - (25 * abilityIndex), y, 24, 23, 0f,0f,24, 23,24, 23)
        }
    }

    private fun drawSun(guiGraphics: GuiGraphics, player: Player) {
        val y = guiGraphics.guiHeight() - 36 - 18 - 2
        val x = guiGraphics.guiWidth() / 2 - 8
        val sunTick = min(getData(player).inSunTick / 20, 4)
        RenderUtils.blitWithAlpha(guiGraphics.pose(), sun.withSuffix("${sunTick}.png"), x, y, 0f,0f,16,16,16,16)
    }

    private fun drawBloodSense(guiGraphics: GuiGraphics) {
        val x = guiGraphics.guiWidth() / 2 + 13
        val y = guiGraphics.guiHeight() / 2 + 9
        val target = Minecraft.getInstance().crosshairPickEntity
        if (target is LivingEntity && BloodPoolLivingEntityAttachment.getData(target).maxBlood > 0) {
            RenderUtils.innerRenderBlood(guiGraphics,target, y, x)
        }
    }

    fun respawn(oldPlayer: ServerPlayer, newPlayer: ServerPlayer, b: Boolean) {
        if (getData(oldPlayer).vampireLevel > 0) {
            val oldBloodData = BloodPoolLivingEntityAttachment.getData(oldPlayer)
            newPlayer.foodData.foodLevel = 10
            BloodPoolLivingEntityAttachment.setData(newPlayer, BloodPoolLivingEntityAttachment.Data(oldBloodData.maxBlood, 900))
            val data = getData(oldPlayer)
            setData(newPlayer, data.copy(inSunTick = 0))
        }
    }
}