package dev.sterner.witchery.registry

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import dev.sterner.witchery.features.affliction.AfflictionAbilityHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.mixin.SaplingBlockAccessor
import dev.sterner.witchery.world.WitcheryWorldState
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.grower.TreeGrower
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


object WitcherySpecialPotionEffects {

    val ID = Witchery.id("special_potion_effect")

    val SPECIAL_REGISTRY_KEY: ResourceKey<Registry<SpecialPotion>> = ResourceKey.createRegistryKey(ID)

    val SPECIAL_REGISTRY: Registry<SpecialPotion> =
        RegistryBuilder(SPECIAL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    private val SPECIALS: DeferredRegister<SpecialPotion> = DeferredRegister.create(SPECIAL_REGISTRY, Witchery.MODID)

    fun register(modEventBus: IEventBus) {
        SPECIALS.register(modEventBus)
    }

    val HARVEST = SPECIALS.register("harvest", Supplier {
        object : SpecialPotion("harvest") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)

                val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                blockPoses.forEach { pos ->
                    val state = level.getBlockState(pos)
                    if (state.block is CropBlock) {
                        val c = state.block as CropBlock
                        level.setBlockAndUpdate(pos, c.getStateForAge(0))
                    } else if (state.canBeReplaced()) {
                        level.destroyBlock(pos, true)
                    }
                }
            }
        }
    })

    val FERTILE = SPECIALS.register("fertile", Supplier {
        object : SpecialPotion("fertile") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                if (level is ServerLevel) {
                    val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                    blockPoses.forEach { pos ->
                        val state = level.getBlockState(pos)
                        if (state.block is CropBlock) {
                            val c = state.block as CropBlock
                            c.performBonemeal(level, level.random, pos, state)
                            if (level.random.nextBoolean()) {
                                c.performBonemeal(level, level.random, pos, state)
                            }
                        }
                    }
                }
            }
        }
    })

    val EXTINGUISH = SPECIALS.register("extinguish", Supplier {
        object : SpecialPotion("extinguish") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                if (level is ServerLevel) {
                    val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                    blockPoses.forEach { pos ->
                        val state = level.getBlockState(pos)
                        if (state.block is FireBlock) {
                            level.removeBlock(pos, false)
                        }
                    }
                }
            }
        }
    })
    val GROW_FLOWERS = SPECIALS.register("grow_flowers", Supplier {
        object : SpecialPotion("grow_flowers") {

            fun getAllFlowers(): List<Block> {
                return BuiltInRegistries.BLOCK
                    .stream()
                    .filter { block: Block ->
                        block.builtInRegistryHolder().`is`(BlockTags.FLOWERS)
                    }
                    .collect(Collectors.toList())
            }

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box).filter {
                    level.getBlockState(it.below()).`is`(BlockTags.DIRT)
                            && level.getBlockState(it).isAir
                }

                val flowers = getAllFlowers()

                blockPoses.forEach { pos ->
                    if (level.random.nextFloat() < 0.45) {
                        level.setBlockAndUpdate(pos, flowers.random().defaultBlockState())
                    }
                }
            }
        }
    })
    val TILL_LAND = SPECIALS.register("till_land", Supplier {
        object : SpecialPotion("till_land") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                if (level is ServerLevel) {
                    val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                        .filter { level.getBlockState(it).`is`(BlockTags.DIRT) }
                    blockPoses.forEach { pos ->
                        val state = level.getBlockState(pos)
                        if (!level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos)) {
                            val newState = TILLABLES[state.block]
                            newState?.let { changeIntoState(it, level, pos, owner) }
                        }
                    }
                }
            }
        }
    })
    val GROW_LILY = SPECIALS.register("grow_lily", Supplier {
        object : SpecialPotion("grow_lily") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                if (level is ServerLevel) {
                    val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                    blockPoses.forEach { pos ->
                        if (level.random.nextFloat() < 0.25f) {
                            val state = level.getBlockState(pos)
                            if (level.getFluidState(pos.below()).type == Fluids.WATER && state.canBeReplaced()) {
                                level.setBlockAndUpdate(pos, Blocks.LILY_PAD.defaultBlockState())
                            }
                        }
                    }
                }
            }
        }
    })
    val PRUNE_LEAVES = SPECIALS.register("prune_leaves", Supplier {
        object : SpecialPotion("prune_leaves") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                if (level is ServerLevel) {
                    val blockPoses: Stream<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(box)
                    blockPoses.forEach { pos ->
                        val state = level.getBlockState(pos)
                        if (state.`is`(BlockTags.LEAVES)) {
                            level.destroyBlock(pos, false)
                        }
                    }
                }
            }
        }
    })
    val PART_WATER = SPECIALS.register("part_water", Supplier {
        object : SpecialPotion("part_water") {
            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                partLiquidFor(level, getBox(hitResult, mergedDispersalModifier), Fluids.WATER)
            }
        }
    })
    val PLANT_DROPPED_SEEDS = SPECIALS.register("plant_dropped_seeds", Supplier {
        object : SpecialPotion("plant_dropped_seeds") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {

                val box = getBox(hitResult, mergedDispersalModifier).inflate(2.0)

                if (level is ServerLevel) {

                    val blockPoses = BlockPos.betweenClosedStream(box).filter {
                        val air = level.getBlockState(it).isAir
                        val below = level.getBlockState(it.below())
                        val isFarmland = below.block is FarmBlock
                        air && isFarmland
                    }
                    val seedEntities = level.getEntities(EntityType.ITEM, box) { entity ->
                        val item = entity.item.item
                        item is ItemNameBlockItem && item.block is CropBlock
                    }.toMutableList()

                    for (pos in blockPoses) {
                        val seedEntity = seedEntities.firstOrNull { it.item.count > 0 } ?: break
                        val seedItem = seedEntity.item.item as ItemNameBlockItem

                        level.setBlockAndUpdate(pos, seedItem.block.defaultBlockState())

                        seedEntity.item.shrink(1)
                        if (seedEntity.item.isEmpty) {
                            seedEntity.discard()
                            seedEntities.remove(seedEntity)
                        }
                    }
                }
            }
        }
    })
    val FELL_TREE = SPECIALS.register("fell_tree", Supplier {
        object : SpecialPotion("fell_tree") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val logStart = BlockPos
                    .betweenClosedStream(box)
                    .filter { pos -> level.getBlockState(pos).`is`(BlockTags.LOGS) }
                    .findFirst()


                logStart.ifPresent { start ->
                    val visited = mutableSetOf<BlockPos>()
                    val queue = ArrayDeque<BlockPos>()
                    queue.add(start)

                    while (queue.isNotEmpty() && visited.size < 64) {
                        val current = queue.removeFirst()

                        if (!visited.add(current)) continue
                        if (!level.getBlockState(current).`is`(BlockTags.LOGS)) continue

                        level.destroyBlock(current, true)

                        for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
                            val neighbor = current.offset(dx, dy, dz)
                            if (!visited.contains(neighbor) && level.getBlockState(neighbor).`is`(BlockTags.LOGS)) {
                                queue.add(neighbor)
                            }
                        }
                    }
                }
            }
        }
    })
    val PART_LAVA = SPECIALS.register("part_lava", Supplier {
        object : SpecialPotion("part_lava") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                partLiquidFor(level, getBox(hitResult, mergedDispersalModifier), Fluids.LAVA)
            }
        }
    })
    val SPROUTING = SPECIALS.register("sprouting", Supplier {
        object : SpecialPotion("sprouting") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val pos = BlockPos.containing(hitResult.location)

                var sapling = Blocks.OAK_SAPLING

                if (owner is Player) {
                    for (i in 0 until owner.inventory.containerSize) {
                        val stack = owner.inventory.getItem(i)
                        val item = stack.item
                        if (item is BlockItem) {
                            if (item.block is SaplingBlock) {
                                sapling = item.block
                                break
                            }
                        }
                    }
                }

                if (level is ServerLevel) {
                    val success = (sapling as SaplingBlockAccessor).treeGrower.growTree(
                        level,
                        level.chunkSource.generator,
                        pos,
                        sapling.defaultBlockState(),
                        level.random
                    )
                    if (!success) {
                        TreeGrower.OAK.growTree(
                            level,
                            level.chunkSource.generator,
                            pos,
                            sapling.defaultBlockState(),
                            level.random
                        )
                    }

                    val treeHeight = getTreeHeight(level, pos)

                    val radius = 10
                    val entities = level.getEntitiesOfClass(Entity::class.java, AABB(pos).inflate(radius.toDouble()))

                    for (entity in entities) {
                        if (entity is LivingEntity) {
                            val entityPos = entity.position()

                            if (isUnderTree(level, entity, treeHeight)) {
                                val newPos = Vec3(entityPos.x, pos.y + treeHeight.toDouble(), entityPos.z)

                                entity.teleportTo(newPos.x, newPos.y, newPos.z)
                            }
                        }
                    }
                }
            }

            private fun getTreeHeight(level: Level, basePos: BlockPos): Int {
                var height = 0
                var foundLeaf = false
                while (!foundLeaf && level.getBlockState(basePos.above(height))
                        .`is`(BlockTags.LEAVES) || level.getBlockState(basePos.above(height)).`is`(BlockTags.LOGS)
                ) {
                    if (level.getBlockState(basePos.above(height)).`is`(Blocks.OAK_LEAVES)) {
                        foundLeaf = true
                    }
                    height++
                }
                return height
            }

            private fun isUnderTree(level: Level, entity: Entity, treeHeight: Int): Boolean {
                val entityPos = entity.blockPosition()

                for (y in 0 until treeHeight) {
                    val treePart = level.getBlockState(entityPos.above(y))
                    if (treePart.`is`(BlockTags.LEAVES) || treePart.`is`(BlockTags.LOGS)) {
                        return true
                    }
                }

                return false
            }
        }
    })


    val PULL = SPECIALS.register("pull", Supplier {
        object : SpecialPotion("pull") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val center = box.center

                list.filter { entity ->
                    box.contains(entity.blockPosition().center)
                }.forEach { entity ->
                    val entityPos = entity.position()

                    val direction = center.subtract(entityPos).normalize().scale(0.5)

                    entity.deltaMovement = entity.deltaMovement.add(direction)
                }
            }
        }
    })
    val PUSH = SPECIALS.register("push", Supplier {
        object : SpecialPotion("push") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val center = box.center

                list.filter { entity ->
                    box.contains(entity.blockPosition().center)
                }.forEach { entity ->
                    val entityPos = entity.position()
                    val direction = entityPos.subtract(center).normalize().scale(0.5)

                    entity.deltaMovement = entity.deltaMovement.add(direction)
                }
            }
        }
    })
    val TELEPORT = SPECIALS.register("teleport", Supplier {
        object : SpecialPotion("teleport") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val entitiesInBox = level.getEntitiesOfClass(LivingEntity::class.java, box)

                for (entity in entitiesInBox) {
                    for (i in 0 until 16) {
                        val d = entity.x + (entity.random.nextDouble() - 0.5) * 16.0
                        val e = Mth.clamp(
                            entity.y + (entity.random.nextInt(16) - 8).toDouble(),
                            level.minBuildHeight.toDouble(),
                            (level.minBuildHeight + (level as ServerLevel).logicalHeight - 1).toDouble()
                        )
                        val f = entity.z + (entity.random.nextDouble() - 0.5) * 16.0

                        if (entity.isPassenger) {
                            entity.stopRiding()
                        }

                        val vec3 = entity.position()
                        if (entity.randomTeleport(d, e, f, true)) {

                            level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity))
                            val soundEvent: SoundEvent = if (entity is Fox) {
                                SoundEvents.FOX_TELEPORT
                            } else {
                                SoundEvents.CHORUS_FRUIT_TELEPORT
                            }
                            val soundSource: SoundSource = SoundSource.PLAYERS

                            level.playSound(null, entity.x, entity.y, entity.z, soundEvent, soundSource)
                            entity.resetFallDistance()
                            break
                        }
                    }
                }
            }
        }
    })
    val TAME_ANIMALS = SPECIALS.register("tame_animals", Supplier {
        object : SpecialPotion("tame_animals") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)

                if (owner is Player) {
                    val entitiesInBox = level.getEntitiesOfClass(LivingEntity::class.java, box)

                    for (entity in entitiesInBox) {
                        if (entity is TamableAnimal) {
                            if (!entity.isTame) {
                                entity.navigation.moveTo(owner, 1.0)
                                entity.tame(owner)
                                val particleOptions: ParticleOptions = ParticleTypes.HEART

                                for (i in 0..6) {
                                    val d: Double = entity.random.nextGaussian() * 0.02
                                    val e: Double = entity.random.nextGaussian() * 0.02
                                    val f: Double = entity.random.nextGaussian() * 0.02
                                    entity.level().addParticle(
                                        particleOptions,
                                        entity.getRandomX(1.0),
                                        entity.randomY + 0.5,
                                        entity.getRandomZ(1.0), d, e, f
                                    )
                                }
                            } else {
                                val dx = entity.x - owner.x
                                val dz = entity.z - owner.z
                                val distance = sqrt(dx * dx + dz * dz)

                                if (distance < 3.0) {
                                    val randomDirection = entity.random.nextDouble() * 360.0
                                    val newX = entity.x + cos(randomDirection) * 5.0
                                    val newZ = entity.z + sin(randomDirection) * 5.0

                                    entity.navigation.moveTo(newX, entity.y, newZ, 1.0)
                                }

                                entity.setTame(true, true)
                                entity.ownerUUID = null
                                val particleOptions: ParticleOptions = ParticleTypes.SMOKE

                                for (i in 0..6) {
                                    val d: Double = entity.random.nextGaussian() * 0.02
                                    val e: Double = entity.random.nextGaussian() * 0.02
                                    val f: Double = entity.random.nextGaussian() * 0.02
                                    entity.level().addParticle(
                                        particleOptions,
                                        entity.getRandomX(1.0),
                                        entity.randomY + 0.5,
                                        entity.getRandomZ(1.0), d, e, f
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    })

    val LOVE = SPECIALS.register("love", Supplier {
        object : SpecialPotion("love") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                val box = getBox(hitResult, mergedDispersalModifier)
                val lovableEntities = level.getEntitiesOfClass(Animal::class.java, box)
                lovableEntities.forEach {
                    it.setInLove(if (owner is Player) owner else null)
                }
            }
        }
    })
    val GROW = SPECIALS.register("grow", Supplier {
        object : SpecialPotion("grow") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                list.filterIsInstance<LivingEntity>().forEach { living ->
                    living.addEffect(MobEffectInstance(WitcheryMobEffects.GROW, duration, amplifier))
                }
            }

            override fun onDrunk(
                level: Level,
                owner: LivingEntity?,
                duration: Int,
                amplifier: Int
            ) {
                owner?.addEffect(MobEffectInstance(WitcheryMobEffects.GROW, duration, amplifier))
            }
        }
    })
    val SHRINK = SPECIALS.register("shrink", Supplier {
        object : SpecialPotion("shrink") {

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                list.filterIsInstance<LivingEntity>().forEach { living ->
                    living.addEffect(MobEffectInstance(WitcheryMobEffects.SHRINK, duration, amplifier))
                }
            }

            override fun onDrunk(
                level: Level,
                owner: LivingEntity?,
                duration: Int,
                amplifier: Int
            ) {
                owner?.addEffect(MobEffectInstance(WitcheryMobEffects.SHRINK, duration, amplifier))
            }
        }
    })
    val SOUL_SEVERANCE = SPECIALS.register("soul_severance", Supplier {
        object : SpecialPotion("soul_severance") {

            override fun onDrunk(
                level: Level,
                owner: LivingEntity?,
                duration: Int,
                amplifier: Int
            ) {
                if (owner is ServerPlayer) {
                    AfflictionAbilityHandler.addAbilityOnLevelUp(owner, LichdomAbility.SOUL_FORM.requiredLevel, AfflictionTypes.LICHDOM, force = true)
                    LichdomSpecificEventHandler.activateSoulForm(owner)
                }
            }

            override fun onActivated(
                level: Level,
                owner: Entity?,
                hitResult: HitResult,
                list: MutableList<Entity>,
                mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
                duration: Int,
                amplifier: Int
            ) {
                list.filterIsInstance<ServerPlayer>().forEach { serverPlayer ->
                    AfflictionAbilityHandler.addAbilityOnLevelUp(serverPlayer, LichdomAbility.SOUL_FORM.requiredLevel, AfflictionTypes.LICHDOM, force = true)
                    LichdomSpecificEventHandler.activateSoulForm(serverPlayer)
                }
            }
        }
    })
    val SUMMON_LEONARD = SPECIALS.register("summon_leonard", Supplier {
        object : SpecialPotion("summon_leonard") {
            /*
                   Summon Leonard	Witches Hat	12	10,000	Tool	This one is weird. First to get that much space in your brew you need a nether star.
                   Second it summons a guy which has a drop that lets you use potions an unlimited number of times via mystic branch
                   Third he helps you cast potions as rituals with limitless range even across dimensions
            */
        }
    })

    val CODEC: Codec<SpecialPotion> =
        RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<SpecialPotion> ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter { special -> special.id }
            ).apply(instance) { resourceLocation ->
                SPECIALS.registry.get().get(resourceLocation)
            }
        }


    fun changeIntoState(state: BlockState, level: Level, pos: BlockPos, entity: Entity?) {
        level.setBlock(pos, state, 11)
        level.gameEvent(
            GameEvent.BLOCK_CHANGE,
            pos,
            GameEvent.Context.of(entity, state)
        )
    }

    fun serverTick(server: MinecraftServer) {
        for (level in server.allLevels) {
            val levelKey = level.dimension()
            val data = WitcheryWorldState.get(level)

            val iterator = data.pendingRestores.iterator()
            while (iterator.hasNext()) {
                val (globalPos, pair) = iterator.next()
                val (ticks, stateMap) = pair

                if (globalPos.dimension() != levelKey) continue

                if (ticks - 1 <= 0) {
                    for ((pos, state) in stateMap) {
                        if (level.getBlockState(pos).isAir || !level.getBlockState(pos).fluidState.isEmpty) {
                            level.setBlockAndUpdate(pos, state)
                        }
                    }
                    iterator.remove()
                    data.setDirty()
                } else {
                    data.pendingRestores[globalPos] = (ticks - 1) to stateMap
                }
            }
        }
    }

    val TILLABLES: Map<Block, BlockState> = Maps.newHashMap(
        ImmutableMap.of(
            Blocks.GRASS_BLOCK,
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.DIRT_PATH,
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.DIRT,
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.COARSE_DIRT,
            Blocks.DIRT.defaultBlockState(),
            Blocks.ROOTED_DIRT,
            Blocks.DIRT.defaultBlockState()
        )
    )
}