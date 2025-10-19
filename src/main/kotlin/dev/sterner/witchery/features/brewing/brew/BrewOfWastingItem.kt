package dev.sterner.witchery.features.brewing.brew

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.awt.Color
import kotlin.math.sqrt

class BrewOfWastingItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {
        if (livingEntity is Player) {
            val hungerDuration = if (hasFrog) 600 else 300
            val hungerAmplifier = if (hasFrog) 2 else 1

            livingEntity.addEffect(MobEffectInstance(MobEffects.HUNGER, hungerDuration, hungerAmplifier))

            level.playSound(
                null,
                livingEntity.x, livingEntity.y, livingEntity.z,
                SoundEvents.WITCH_DRINK,
                SoundSource.PLAYERS,
                1.0f,
                0.8f + level.random.nextFloat() * 0.4f
            )
        } else {
            val witherDuration = if (hasFrog) 200 else 100
            val witherAmplifier = if (hasFrog) 1 else 0

            livingEntity.addEffect(MobEffectInstance(MobEffects.WITHER, witherDuration, witherAmplifier))

            level.addParticle(
                ParticleTypes.SMOKE,
                livingEntity.x,
                livingEntity.y + livingEntity.bbHeight / 2,
                livingEntity.z,
                0.0, 0.0, 0.0
            )
        }
    }

    override fun applyEffectOnBlock(
        level: Level,
        blockHit: BlockHitResult,
        hasFrog: Boolean
    ) {
        val pos = blockHit.blockPos
        val state = level.getBlockState(pos)

        val isPlant = state.block is CropBlock ||
                state.block is FlowerBlock ||
                state.block is BushBlock ||
                state.block is LeavesBlock ||
                state.block is SaplingBlock

        if (isPlant) {
            level.destroyBlock(pos, false)

            level.addParticle(
                ParticleTypes.LARGE_SMOKE,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                0.0, 0.0, 0.0
            )

            if (hasFrog) {
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        for (dz in -1..1) {
                            if (dx == 0 && dy == 0 && dz == 0) continue

                            val nearbyPos = pos.offset(dx, dy, dz)
                            val nearbyState = level.getBlockState(nearbyPos)

                            val isNearbyPlant = nearbyState.block is CropBlock ||
                                    nearbyState.block is FlowerBlock ||
                                    nearbyState.block is BushBlock ||
                                    nearbyState.block is LeavesBlock ||
                                    nearbyState.block is SaplingBlock

                            if (isNearbyPlant) {
                                level.destroyBlock(nearbyPos, true)
                            }
                        }
                    }
                }
            }

            level.playSound(
                null,
                pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                SoundEvents.WITHER_AMBIENT,
                SoundSource.BLOCKS,
                0.5f,
                0.8f + level.random.nextFloat() * 0.4f
            )
        }
    }

    override fun applyEffectOnHitLocation(level: Level, location: Vec3, hasFrog: Boolean) {
        val radius = if (hasFrog) 4.0 else 2.5
        val entities = level.getEntitiesOfClass(
            LivingEntity::class.java,
            AABB(
                location.x - radius, location.y - radius, location.z - radius,
                location.x + radius, location.y + radius, location.z + radius
            )
        )

        for (entity in entities) {
            applyEffectOnEntities(level, entity, hasFrog)
        }

        val blockRadius = if (hasFrog) 5 else 4
        for (dx in -blockRadius..blockRadius) {
            for (dy in -blockRadius..blockRadius) {
                for (dz in -blockRadius..blockRadius) {
                    val pos = BlockPos(
                        location.x.toInt() + dx,
                        location.y.toInt() + dy,
                        location.z.toInt() + dz
                    )

                    val distance = sqrt(
                        (dx * dx + dy * dy + dz * dz).toDouble()
                    )

                    val chance = 1.0 - (distance / blockRadius)

                    if (level.random.nextDouble() < chance) {
                        val state = level.getBlockState(pos)
                        val isPlant = state.block is CropBlock ||
                                state.block is FlowerBlock ||
                                state.block is BushBlock ||
                                state.block is LeavesBlock ||
                                state.block is SaplingBlock

                        if (isPlant) {
                            val fakeHit = BlockHitResult(
                                location,
                                Direction.UP,
                                pos,
                                false
                            )

                            applyEffectOnBlock(level, fakeHit, hasFrog)
                        }
                    }
                }
            }
        }

        level.addParticle(
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, Color(145, 80, 40).rgb),
            location.x, location.y, location.z,
            0.3, 0.3, 0.0 // Yellow-green color
        )
    }
}