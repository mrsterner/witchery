package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.api.interfaces.VillagerTransfix
import dev.sterner.witchery.handler.ability.AbilityCooldownManager
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player

enum class VampireAbility(
    override val requiredLevel: Int,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.VAMPIRE
) : AfflictionAbility {

    DRINK_BLOOD(0, 0) {
        override val id: String
            get() = "drink_blood"
        override val requiresTarget = true

        override fun use(player: Player, target: Entity): Boolean {
            if (player !is ServerPlayer || target !is LivingEntity) return false

            val playerBloodData = BloodPoolLivingEntityAttachment.getData(player)
            val targetData = BloodPoolLivingEntityAttachment.getData(target)

            if (playerBloodData.bloodPool >= playerBloodData.maxBlood ||
                targetData.bloodPool <= 0
            ) return false

            AfflictionHandler.vampireDrinkBloodAbility(player, target, playerBloodData)
            return true
        }
    },

    NIGHT_VISION(1, 20 * 2) {
        override val id: String
            get() = "night_vision"
        override val affliction = AfflictionTypes.VAMPIRE

        override fun use(player: Player): Boolean {
            if (player.isShiftKeyDown) {
                val hadNightVision = AfflictionPlayerAttachment.getData(player).hasNightVision()

                AfflictionPlayerAttachment.batchUpdate(player) {
                    withNightVision(!hadNightVision)
                }

                if (hadNightVision) {
                    AbilityCooldownManager.startCooldown(player, this)
                }
                return true
            }
            return false
        }
    },

    TRANSFIX(1, 20 * 2) {
        override val id: String
            get() = "transfix"
        override val affliction = AfflictionTypes.VAMPIRE
        override val requiresTarget: Boolean
            get() = true

        override fun use(player: Player, target: Entity): Boolean {
            if (player !is ServerPlayer || target !is LivingEntity) return false

            if (target is Villager) {
                val transfixVillager = target as VillagerTransfix
                transfixVillager.setTransfixedLookVector(player.eyePosition)

                if (AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.VAMPIRE) >= 8) {
                    transfixVillager.`witchery$setMesmerized`(player.uuid)
                }

                AbilityCooldownManager.startCooldown(player, this)
                return true
            }

            return false
        }
    },

    SPEED(2, 20 * 5) {
        override val id: String
            get() = "speed"

        override fun use(player: Player): Boolean {
            val wasActive = AfflictionPlayerAttachment.getData(player).hasSpeedBoost()

            AfflictionPlayerAttachment.batchUpdate(player) {
                withSpeedBoost(!wasActive)
            }

            if (wasActive) {
                AbilityCooldownManager.startCooldown(player, this)
            }

            return true
        }
    },

    BAT_FORM(3, 20 * 10) {
        override val id: String
            get() = "bat_form"

        override fun use(player: Player): Boolean {
            val isBat = TransformationHandler.isBat(player)
            if (isBat) {
                TransformationHandler.removeForm(player)
                AbilityCooldownManager.startCooldown(player, this)
            } else {
                TransformationHandler.setBatForm(player)
            }
            return true
        }
    };
}
