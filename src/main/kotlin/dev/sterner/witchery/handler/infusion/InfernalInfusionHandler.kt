package dev.sterner.witchery.handler.infusion

import dev.sterner.witchery.data_attachment.infusion.CreatureType
import dev.sterner.witchery.data_attachment.infusion.InfernalInfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

object InfernalInfusionHandler {

    fun strikeLightning(entity: Entity?) {
        if (entity is Player && InfernalInfusionPlayerAttachment.getData(entity).currentCreature == CreatureType.CREEPER) {
            InfusionHandler.increaseInfusionCharge(entity, InfusionPlayerAttachment.MAX_CHARGE)
        }
    }

    fun tick(player: Player?) {
        val data = player?.let { InfernalInfusionPlayerAttachment.getData(it) }

        if (data != null) {
            if (data.currentCreature == CreatureType.ZOMBIE_PIGMAN || data.currentCreature == CreatureType.GHAST || data.currentCreature == CreatureType.BLAZE) {
                player.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 3, 0))
            }

            if (data.currentCreature == CreatureType.SLIME || data.currentCreature == CreatureType.MAGMA_CUBE) {
                player.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 3, 0))
            }

            if (data.currentCreature == CreatureType.SILVERFISH || data.currentCreature == CreatureType.WOLF || data.currentCreature == CreatureType.OCELOT || data.currentCreature == CreatureType.HORSE) {
                if (!player.isInWaterOrRain) {
                    player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 3, 0))
                }
            }
        }
    }
}