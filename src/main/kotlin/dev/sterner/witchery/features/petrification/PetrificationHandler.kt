package dev.sterner.witchery.features.petrification

import dev.sterner.witchery.Witchery
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import kotlin.math.cos
import kotlin.math.sin


@EventBusSubscriber
object PetrificationHandler {

    private val PETRIFIED_SLOWNESS_ID = Witchery.id("petrified_slowness")
    private val PETRIFIED_WEAKNESS_ID = Witchery.id("petrified_weakness")

    fun petrify(entity: LivingEntity, duration: Int) {
        val data = PetrifiedEntityAttachment.getData(entity)

        var f: Float = entity.yBodyRot
        val f1: Float = entity.yHeadRot
        var yaw = f1 - f
        val shouldSit = entity.isPassenger && (entity.vehicle != null && entity.vehicle!!.shouldRiderSit())
        if (shouldSit && entity.vehicle is LivingEntity) {
            val livingentity = entity.vehicle as LivingEntity
            f = livingentity.yBodyRot
            yaw = f1 - f
            var f3 = Mth.wrapDegrees(yaw)
            if (f3 < -85.0f) {
                f3 = -85.0f
            }

            if (f3 >= 85.0f) {
                f3 = 85.0f
            }

            f = f1 - f3
            if (f3 * f3 > 2500.0f) {
                f += f3 * 0.2f
            }

            yaw = f1 - f
        }

        val pitch: Float = entity.xRot

        val newData = data.withPetrification(
            duration,
            entity.tickCount.toFloat(),
            entity.walkAnimation.speed(),
            entity.walkAnimation.position(),
            yaw,
            pitch,
            entity.yBodyRot
        )

        PetrifiedEntityAttachment.setData(entity, newData)

        entity.level().playSound(
            null,
            entity.blockPosition(),
            SoundEvents.STONE_BREAK,
            SoundSource.HOSTILE,
            1.0f,
            0.5f
        )

        applyPetrificationEffects(entity)
    }

    fun unpetrify(entity: LivingEntity) {
        val data = PetrifiedEntityAttachment.getData(entity).copy(
            petrified = false,
            petrificationTicks = 0,
            breakProgress = 0,
            playerPunchCount = 0
        )
        PetrifiedEntityAttachment.setData(entity, data)

        removePetrificationEffects(entity)

        entity.level().playSound(
            null,
            entity.blockPosition(),
            SoundEvents.STONE_BREAK,
            SoundSource.HOSTILE,
            1.0f,
            1.5f
        )

        // Spawn break particles
        if (entity.level() is ServerLevel) {
            spawnBreakParticles(entity.level() as ServerLevel, entity)
        }
    }

    @SubscribeEvent
    fun onLivingTick(event: EntityTickEvent.Pre) {
        val entity = event.entity
        if (entity.level().isClientSide) return
        if (entity !is LivingEntity) return

        val data = PetrifiedEntityAttachment.getData(entity)
        if (!data.isPetrified()) return

        val newData = data.tick()
        PetrifiedEntityAttachment.setData(entity, newData)

        if (newData.isPetrified()) {
            applyPetrificationEffects(entity)

            if (entity.level().gameTime % 20 == 0L) {
                spawnPetrifiedParticles(entity.level() as ServerLevel, entity)
            }
        } else {
            removePetrificationEffects(entity)
        }
    }

    @SubscribeEvent
    fun onAttackEntity(event: AttackEntityEvent) {
        val attacker = event.entity
        val target = event.target

        if (target !is LivingEntity) return

        val data = PetrifiedEntityAttachment.getData(target)
        if (!data.isPetrified()) return

        // If player is petrified and punching air (target is themselves somehow), count it
        if (attacker is Player && target == attacker) {
            handlePlayerPunch(attacker)
            event.isCanceled = true
            return
        }

        // Check if attacker has pickaxe
        if (!attacker.mainHandItem.`is`(ItemTags.PICKAXES)) {
            event.isCanceled = true
            return
        }
    }

    @SubscribeEvent
    fun onLivingDamage(event: LivingIncomingDamageEvent) {
        val entity = event.entity as LivingEntity
        val data = PetrifiedEntityAttachment.getData(entity)

        if (!data.isPetrified()) return

        val attacker = event.source.entity

        if (attacker is LivingEntity && attacker.mainHandItem.`is`(ItemTags.PICKAXES)) {

            val level = entity.level()
            if (level is ServerLevel) {
                val newData = data.incrementBreakProgress()
                PetrifiedEntityAttachment.setData(entity, newData)

                level.playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.STONE_HIT,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
                )

                if (newData.breakProgress >= 5) {
                    unpetrify(entity)
                }
            }
        } else {
            event.amount = 0f
        }
    }

    fun handlePlayerPunch(player: Player) {
        val data = PetrifiedEntityAttachment.getData(player)
        if (!data.isPetrified()) return

        val newData = data.incrementPunchCount()
        PetrifiedEntityAttachment.setData(player, newData)

        val level = player.level()

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.STONE_HIT,
            SoundSource.PLAYERS,
            0.5f,
            0.8f + (newData.playerPunchCount * 0.02f)
        )

        if (newData.playerPunchCount >= 10) {
            unpetrify(player)
        }
    }

    private fun applyPetrificationEffects(entity: LivingEntity) {
        val speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED)
        speedAttribute?.let { attr ->
            attr.removeModifier(PETRIFIED_SLOWNESS_ID)

            val slowAmount = -0.95 * 1
            attr.addTransientModifier(
                AttributeModifier(
                    PETRIFIED_SLOWNESS_ID,
                    slowAmount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            )
        }

        val attackAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE)
        attackAttribute?.let { attr ->
            attr.removeModifier(PETRIFIED_WEAKNESS_ID)

            val weaknessAmount = -0.8 * 1
            attr.addTransientModifier(
                AttributeModifier(
                    PETRIFIED_WEAKNESS_ID,
                    weaknessAmount,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
            )
        }
    }

    private fun removePetrificationEffects(entity: LivingEntity) {
        entity.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(PETRIFIED_SLOWNESS_ID)
        entity.getAttribute(Attributes.ATTACK_DAMAGE)?.removeModifier(PETRIFIED_WEAKNESS_ID)
    }

    fun spawnPetrifiedParticles(level: ServerLevel, entity: LivingEntity) {
        val pos = entity.position()
        val random = entity.random

        for (i in 0..2) {
            val offsetX = (random.nextDouble() - 0.5) * entity.bbWidth
            val offsetY = random.nextDouble() * entity.bbHeight
            val offsetZ = (random.nextDouble() - 0.5) * entity.bbWidth

            level.sendParticles(
                BlockParticleOption(
                    ParticleTypes.BLOCK,
                    Blocks.STONE.defaultBlockState()
                ),
                pos.x + offsetX,
                pos.y + offsetY,
                pos.z + offsetZ,
                1,
                0.0, -0.1, 0.0,
                0.1
            )
        }
    }

    private fun spawnBreakParticles(level: ServerLevel, entity: LivingEntity) {
        val pos = entity.position()

        for (i in 0..20) {
            val angle = (i / 20.0) * Math.PI * 2
            val radius = entity.bbWidth * 0.5

            val x = pos.x + cos(angle) * radius
            val y = pos.y + entity.bbHeight / 2
            val z = pos.z + sin(angle) * radius

            level.sendParticles(
                BlockParticleOption(
                    ParticleTypes.BLOCK,
                    Blocks.STONE.defaultBlockState()
                ),
                x, y, z,
                3,
                0.2, 0.2, 0.2,
                0.15
            )
        }
    }

    fun spawnPetrificationWave(level: ServerLevel, start: Vec3, end: Vec3) {
        val direction = end.subtract(start).normalize()
        val distance = start.distanceTo(end)
        val steps = (distance * 5).toInt().coerceAtLeast(10)

        for (i in 0..steps) {
            val progress = i / steps.toDouble()
            val pos = start.add(direction.scale(distance * progress))

            level.sendParticles(
                ParticleTypes.SMOKE,
                pos.x, pos.y, pos.z,
                3,
                0.1, 0.1, 0.1,
                0.02
            )
        }
    }

    fun spawnTransformationParticles(level: ServerLevel, entity: LivingEntity) {
        val pos = entity.position()
        val height = entity.bbHeight

        for (i in 0..30) {
            val angle = (i / 30.0) * Math.PI * 4
            val heightOffset = (i / 30.0) * height
            val radius = entity.bbWidth * 0.7

            val x = pos.x + cos(angle) * radius
            val y = pos.y + heightOffset
            val z = pos.z + sin(angle) * radius

            level.sendParticles(
                ParticleTypes.END_ROD,
                x, y, z,
                1,
                0.0, 0.0, 0.0,
                0.0
            )
        }

        level.sendParticles(
            ParticleTypes.EXPLOSION,
            pos.x, pos.y + height / 2, pos.z,
            1,
            0.0, 0.0, 0.0,
            0.0
        )
    }
}