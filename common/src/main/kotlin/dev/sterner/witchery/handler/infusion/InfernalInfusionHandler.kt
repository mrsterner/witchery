package dev.sterner.witchery.handler.infusion

import dev.architectury.event.events.common.LightningEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.platform.infusion.CreatureType
import dev.sterner.witchery.platform.infusion.InfernalInfusionPlayerAttachment.getData
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object InfernalInfusionHandler {

    fun registerEvents() {
        LightningEvent.STRIKE.register(::strikeLightning)
        TickEvent.PLAYER_POST.register(::tick)
    }

    private fun strikeLightning(lightningBolt: LightningBolt?, level: Level?, vec3: Vec3?, entities: MutableList<Entity>?) {
        if (entities != null) {
            for (entity in entities) {
                if (entity is Player && getData(entity).currentCreature == CreatureType.CREEPER) {
                    InfusionHandler.increaseInfusionCharge(entity, InfusionPlayerAttachment.MAX_CHARGE)
                }
            }
        }
    }

    private fun tick(player: Player?) {
        val data = player?.let { getData(it) }

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