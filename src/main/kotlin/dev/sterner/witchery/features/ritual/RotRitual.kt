package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.entity.animal.horse.ZombieHorse
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.*
import net.minecraft.world.entity.monster.hoglin.Hoglin
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.FlowerBlock
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.awt.Color

class RotRitual : Ritual("rot") {

    private val TRANSFORM_DELAY = 40
    private val EFFECT_RADIUS = 8.0

    private val transformingEntities = mutableMapOf<Entity, Int>()

    override fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

        val box = AABB(
            pos.x - EFFECT_RADIUS, pos.y - EFFECT_RADIUS, pos.z - EFFECT_RADIUS,
            pos.x + EFFECT_RADIUS, pos.y + EFFECT_RADIUS, pos.z + EFFECT_RADIUS
        )

        processLivingEntities(level, box)
        processItemEntities(level, box)
        processBlocks(level, pos)

        if (level.isClientSide) {
            spawnAmbientParticles(level, pos)
        }
    }

    private fun processLivingEntities(level: Level, box: AABB) {
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, box) { entity ->
            entity != null && canBeZombified(entity)
        }

        for (entity in entities) {
            if (entity is Player && !WitcheryApi.isWitchy(entity)) {
                val adjustedDelay = TRANSFORM_DELAY * 3

                if (entity in transformingEntities) {
                    val remainingTicks = transformingEntities[entity]!! - 1

                    if (remainingTicks <= 0) {
                        if (level.random.nextFloat() < 0.7f) {
                            transformingEntities.remove(entity)
                            return
                        }
                        transformEntity(level, entity)
                        transformingEntities.remove(entity)
                    } else {
                        transformingEntities[entity] = remainingTicks
                        if (remainingTicks % 20 == 0) {
                            spawnTransformParticles(level, entity)
                        }
                    }
                } else {
                    transformingEntities[entity] = adjustedDelay
                }
            } else {
                if (entity in transformingEntities) {
                    val remainingTicks = transformingEntities[entity]!! - 1

                    if (remainingTicks <= 0) {
                        transformEntity(level, entity)
                        transformingEntities.remove(entity)
                    } else {
                        transformingEntities[entity] = remainingTicks
                        spawnTransformParticles(level, entity)
                    }
                } else {
                    transformingEntities[entity] = TRANSFORM_DELAY
                }
            }
        }

        transformingEntities.entries.removeIf { (entity, _) ->
            !box.contains(entity.position()) || entity.isRemoved
        }
    }

    private fun processItemEntities(level: Level, box: AABB) {
        val itemEntities = level.getEntitiesOfClass(ItemEntity::class.java, box) { entity ->
            entity != null && canRotItem(entity.item)
        }

        for (itemEntity in itemEntities) {
            if (itemEntity in transformingEntities) {
                val remainingTicks = transformingEntities[itemEntity]!! - 1

                if (remainingTicks <= 0) {
                    transformItem(level, itemEntity)
                    transformingEntities.remove(itemEntity)
                } else {
                    transformingEntities[itemEntity] = remainingTicks
                    spawnTransformParticles(level, itemEntity)
                }
            } else {
                transformingEntities[itemEntity] = TRANSFORM_DELAY
            }
        }
    }

    private fun processBlocks(level: Level, pos: BlockPos) {
        val range = EFFECT_RADIUS.toInt()

        val blocksPerTick = 5
        var processed = 0

        for (x in -range..range) {
            for (z in -range..range) {
                if (x * x + z * z <= range * range) {
                    for (y in -range..range) {
                        val blockPos = pos.offset(x, y, z)

                        if (canTransformBlock(level, blockPos)) {
                            if (level.random.nextInt(20) == 0) {
                                spawnBlockTransformParticles(level, blockPos)

                                transformBlock(level, blockPos)

                                processed++
                                if (processed >= blocksPerTick) {
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun canBeZombified(entity: LivingEntity): Boolean {
        return when (entity) {
            is Villager,
            is Pig,
            is Horse,
            is Zoglin,
            is Skeleton
                -> true

            else -> false
        }
    }

    private fun transformEntity(level: Level, entity: LivingEntity) {
        if (level.isClientSide) return

        val zombieEntity = when (entity) {
            is Villager -> {
                val zombieVillager = ZombieVillager(EntityType.ZOMBIE_VILLAGER, level)
                zombieVillager.finalizeSpawn(
                    level as ServerLevel,
                    level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.CONVERSION,
                    Zombie.ZombieGroupData(false, true)
                )
                zombieVillager.villagerData = entity.villagerData
                zombieVillager.setGossips(entity.gossips.store(NbtOps.INSTANCE))
                zombieVillager.setTradeOffers(entity.offers.copy())
                zombieVillager.villagerXp = entity.villagerXp
                zombieVillager.setPos(entity.x, entity.y, entity.z)
                zombieVillager.yRot = entity.yRot
                zombieVillager.xRot = entity.xRot
                zombieVillager
            }

            is Pig -> {
                val zombiePig = ZombifiedPiglin(EntityType.ZOMBIFIED_PIGLIN, level)
                zombiePig.finalizeSpawn(
                    level as ServerLevel,
                    level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.CONVERSION,
                    null
                )
                zombiePig.setPos(entity.x, entity.y, entity.z)
                zombiePig.yRot = entity.yRot
                zombiePig.xRot = entity.xRot
                zombiePig
            }

            is Horse -> {
                val zombieHorse = ZombieHorse(EntityType.ZOMBIE_HORSE, level)
                zombieHorse.finalizeSpawn(
                    level as ServerLevel,
                    level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.CONVERSION,
                    null
                )
                zombieHorse.setPos(entity.x, entity.y, entity.z)
                zombieHorse.yRot = entity.yRot
                zombieHorse.xRot = entity.xRot
                zombieHorse
            }

            is Hoglin -> {
                val zoglin = Zoglin(EntityType.ZOGLIN, level)
                zoglin.finalizeSpawn(
                    level as ServerLevel,
                    level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.CONVERSION,
                    null
                )
                zoglin.setPos(entity.x, entity.y, entity.z)
                zoglin.yRot = entity.yRot
                zoglin.xRot = entity.xRot
                zoglin
            }

            is Skeleton -> {
                val witherSkeleton = WitherSkeleton(EntityType.WITHER_SKELETON, level)
                witherSkeleton.finalizeSpawn(
                    level as ServerLevel,
                    level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.CONVERSION,
                    null
                )
                witherSkeleton.setPos(entity.x, entity.y, entity.z)
                witherSkeleton.yRot = entity.yRot
                witherSkeleton.xRot = entity.xRot
                witherSkeleton
            }

            else -> null
        }

        if (zombieEntity != null) {
            entity.remove(Entity.RemovalReason.DISCARDED)
            level.addFreshEntity(zombieEntity)

            level.playSound(null, entity.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0f, 1.0f)
            spawnCompletionParticles(level, zombieEntity)
        }
    }

    private fun canRotItem(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        return item == Items.BEEF ||
                item == Items.PORKCHOP ||
                item == Items.CHICKEN ||
                item == Items.MUTTON ||
                item == Items.RABBIT
    }

    private fun transformItem(level: Level, itemEntity: ItemEntity) {
        if (level.isClientSide) return

        val stack = itemEntity.item
        var rottenStack: ItemStack? = null

        if (canRotItem(stack)) {
            rottenStack = ItemStack(Items.ROTTEN_FLESH, stack.count)
        }

        if (rottenStack != null) {
            val newItemEntity = ItemEntity(
                level,
                itemEntity.x, itemEntity.y, itemEntity.z,
                rottenStack
            )

            newItemEntity.deltaMovement = itemEntity.deltaMovement

            itemEntity.remove(Entity.RemovalReason.DISCARDED)
            level.addFreshEntity(newItemEntity)

            level.playSound(
                null,
                itemEntity.blockPosition(),
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.BLOCKS,
                0.5f,
                0.8f
            )
            spawnCompletionParticles(level, newItemEntity)
        }
    }

    private fun canTransformBlock(level: Level, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        val block = state.block

        return block is CropBlock ||
                block is FlowerBlock ||
                block is DoublePlantBlock
    }

    private fun transformBlock(level: Level, pos: BlockPos) {
        if (level.isClientSide) return

        val state = level.getBlockState(pos)
        val block = state.block

        val newState = when (block) {
            is CropBlock -> {
                if (level.random.nextBoolean()) {
                    Blocks.DEAD_BUSH.defaultBlockState()
                } else {
                    if (level.random.nextBoolean())
                        Blocks.BROWN_MUSHROOM.defaultBlockState()
                    else
                        Blocks.RED_MUSHROOM.defaultBlockState()
                }
            }

            is FlowerBlock, is DoublePlantBlock -> {
                if (level.random.nextBoolean()) {
                    Blocks.DEAD_BUSH.defaultBlockState()
                } else {
                    Blocks.WITHER_ROSE.defaultBlockState()
                }
            }

            else -> null
        }

        if (newState != null) {
            level.setBlockAndUpdate(pos, newState)

            level.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 0.5f, 0.8f)
            spawnCompletionParticles(level, Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5))
        }
    }

    private fun spawnTransformParticles(level: Level, entity: Entity) {
        if (!level.isClientSide) return

        val random = level.random
        val box = entity.boundingBox

        (0 until 5).forEach { i ->
            val x = box.minX + random.nextDouble() * (box.maxX - box.minX)
            val y = box.minY + random.nextDouble() * (box.maxY - box.minY)
            val z = box.minZ + random.nextDouble() * (box.maxZ - box.minZ)

            level.addParticle(
                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, Color(145, 80, 40).rgb),
                x, y, z,
                0.3, 0.1, 0.1
            )
        }
    }

    private fun spawnBlockTransformParticles(level: Level, pos: BlockPos) {
        if (!level.isClientSide) return

        val random = level.random

        (0 until 5).forEach { i ->
            level.addParticle(
                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, Color(145, 80, 40).rgb),
                pos.x + random.nextDouble(),
                pos.y + random.nextDouble(),
                pos.z + random.nextDouble(),
                0.3, 0.1, 0.1
            )
        }
    }

    private fun spawnCompletionParticles(level: Level, entity: Entity) {
        spawnCompletionParticles(level, entity.position())
    }

    private fun spawnCompletionParticles(level: Level, pos: Vec3) {
        if (!level.isClientSide) return

        (0 until 15).forEach { i ->
            level.addParticle(
                ParticleTypes.LARGE_SMOKE,
                pos.x(), pos.y(), pos.z(),
                level.random.nextGaussian() * 0.05,
                level.random.nextGaussian() * 0.05,
                level.random.nextGaussian() * 0.05
            )
        }
    }

    private fun spawnAmbientParticles(level: Level, pos: BlockPos) {
        if (!level.isClientSide) return

        val random = level.random

        if (random.nextInt(5) == 0) {
            val x = pos.x + random.nextDouble() * EFFECT_RADIUS * 2 - EFFECT_RADIUS
            val y = pos.y + random.nextDouble() * 2
            val z = pos.z + random.nextDouble() * EFFECT_RADIUS * 2 - EFFECT_RADIUS

            level.addParticle(
                ParticleTypes.SMOKE,
                x, y, z,
                0.0, 0.05, 0.0
            )
        }
    }
}