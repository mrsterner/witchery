package dev.sterner.witchery.features.brewing.brew

import dev.sterner.witchery.core.data_attachment.TarotPlayerAttachment
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.gossip.GossipType
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.function.Predicate

class BrewOfOblivion(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(
        level: Level,
        livingEntity: LivingEntity,
        hasFrog: Boolean
    ) {

        livingEntity.brain.clearMemories()

        if (livingEntity is Player) {
            TarotPlayerAttachment.setData(livingEntity, TarotPlayerAttachment.Data())

            livingEntity.lastHurtByMob = null
            livingEntity.setLastHurtMob(null)

            livingEntity.addEffect(MobEffectInstance(MobEffects.CONFUSION, 20 * 3, 0))
        }

        if (livingEntity is Mob) {
            livingEntity.target = null
            livingEntity.lastHurtByMob = null
            livingEntity.setLastHurtMob(null)
            livingEntity.setLastHurtByPlayer(null)
            livingEntity.setAggressive(false)

            livingEntity.navigation.stop()

            livingEntity.lookControl.setLookAt(livingEntity.x, livingEntity.y, livingEntity.z)

            if (livingEntity is NeutralMob) {
                livingEntity.stopBeingAngry()
                livingEntity.playerDied(null)
                livingEntity.persistentAngerTarget = null
                livingEntity.remainingPersistentAngerTime = 0
            }
        }

        if (livingEntity is Villager) {
            livingEntity.brain.clearMemories()

            for (type in GossipType.entries) {
                livingEntity.gossips.remove(type)
            }
        }

        if (level is ServerLevel) {

            level.sendParticles(
                ParticleTypes.SMOKE,
                livingEntity.x,
                livingEntity.eyeY,
                livingEntity.z,
                20,
                0.3,
                0.5,
                0.3,
                0.02
            )

            level.sendParticles(
                ParticleTypes.PORTAL,
                livingEntity.x,
                livingEntity.eyeY,
                livingEntity.z,
                15,
                0.4,
                0.4,
                0.4,
                0.5
            )
        }

        level.playSound(
            null,
            livingEntity.blockPosition(),
            SoundEvents.ZOMBIE_VILLAGER_CURE,
            SoundSource.PLAYERS,
            1.0f,
            1.5f
        )
    }
}