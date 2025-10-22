package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.block.critter_snare.CritterSnareBlock
import dev.sterner.witchery.content.block.grassper.GrassperBlockEntity
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlock
import dev.sterner.witchery.content.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CaveVinesPlantBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import tallestegg.guardvillagers.GuardEntityType

class MutatingSpringItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        if (level.getBlockState(pos).`is`(Blocks.WHEAT)) {
            makeWormwood(level, pos)
            return InteractionResult.SUCCESS
        }
        if (level.getBlockState(pos).`is`(Blocks.CHEST)) {
            makeGrassper(level, pos)
            return InteractionResult.SUCCESS
        }
        if (level.getBlockState(pos).`is`(Blocks.COBWEB)) {
            makeCritterSnare(level, pos)
            makeLifeBlood(level, pos)
            return InteractionResult.SUCCESS
        }
        if (level.getBlockState(pos).`is`(WitcheryBlocks.CRITTER_SNARE.get())) {
            makeFromSnare(level, pos)
            return InteractionResult.SUCCESS
        }


        val blockState = level.getBlockState(pos)
        if (blockState.`is`(Blocks.GRASS_BLOCK)) {
            level.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.DIRT)) {
            level.setBlockAndUpdate(pos, Blocks.CLAY.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.CLAY)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.MYCELIUM)) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState())
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }

    private fun makeFromSnare(level: Level, pos: BlockPos) {
        val state = level.getBlockState(pos)
        if (state.block is CritterSnareBlock && state.hasProperty(CritterSnareBlock.CAPTURED_STATE) && state.getValue(
                CritterSnareBlock.CAPTURED_STATE
            ) == CritterSnareBlock.CapturedEntity.SILVERFISH
        ) {
            val bl = checkCardinal(level, pos, Blocks.LILY_PAD)
            val bl2 = checkCardinal(level, pos.below(), Blocks.WATER)

            val grassperNW = level.getBlockEntity(pos.north().west())
            val grassperNE = level.getBlockEntity(pos.north().east())
            val grassperSW = level.getBlockEntity(pos.south().west())
            val grassperSE = level.getBlockEntity(pos.south().east())

            if (grassperNW is GrassperBlockEntity && grassperNE is GrassperBlockEntity && grassperSW is GrassperBlockEntity && grassperSE is GrassperBlockEntity) {

                val items = listOf(
                    grassperNW.getItem(0),
                    grassperNE.getItem(0),
                    grassperSW.getItem(0),
                    grassperSE.getItem(0)
                )

                val mutandis = WitcheryItems.MUTANDIS.get()
                val attunedStone = WitcheryItems.ATTUNED_STONE.get()
                val tongueOfDog = WitcheryItems.TONGUE_OF_DOG.get()

                val mutandisCount = items.count { it.`is`(mutandis) }
                val attunedStoneCount =
                    items.count { it.`is`(attunedStone) && it.get(WitcheryDataComponents.ATTUNED.get()) == true }
                val tongueOfDogCount = items.count { it.`is`(tongueOfDog) }
                if (bl && bl2 && mutandisCount >= 2 && attunedStoneCount >= 1 && tongueOfDogCount >= 1) {

                    var mutandisLeft = 2
                    var attunedStoneLeft = 1
                    var tongueOfDogLeft = 1

                    for (itemStack in items) {
                        when (itemStack.item) {
                            mutandis -> {
                                val toConsume = minOf(mutandisLeft, itemStack.count)
                                itemStack.shrink(toConsume)
                                mutandisLeft -= toConsume
                            }

                            attunedStone -> {
                                val toConsume = minOf(attunedStoneLeft, itemStack.count)
                                itemStack.shrink(toConsume)
                                attunedStoneLeft -= toConsume
                            }

                            tongueOfDog -> {
                                val toConsume = minOf(tongueOfDogLeft, itemStack.count)
                                itemStack.shrink(toConsume)
                                tongueOfDogLeft -= toConsume
                            }
                        }
                    }
                    val louse = WitcheryEntityTypes.PARASITIC_LOUSE.get().create(level)

                    louse?.let {
                        it.moveTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0f, 0f)
                        level.addFreshEntity(it)
                    }

                    removeCardinal(level, pos)
                    removeCardinal(level, pos.below())
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                }
            }
        }
    }

    private fun makeWormwood(level: Level, pos: BlockPos) {
        if (checkCardinal(level, pos, WitcheryBlocks.WISPY_COTTON.get()) && checkWaterDiagonals(level, pos)) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.WORMWOOD_CROP.get().defaultBlockState())
            removeCardinal(level, pos)
            removeDiagonals(level, pos)
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
        }
    }

    private fun makeGrassper(level: Level, pos: BlockPos) {
        if (checkCardinal(level, pos, Blocks.SHORT_GRASS) && level.getBlockState(pos.below()).`is`(Blocks.WATER)) {
            removeCardinal(level, pos)
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
            level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState())
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
            level.setBlockAndUpdate(pos.south(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.north(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.west(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.east(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
        }
    }

    private fun makeLifeBlood(level: Level, pos: BlockPos) {
        val hasVillager = level.getEntities(
            EntityType.VILLAGER,
            AABB.ofSize(pos.center, 1.0, 1.0, 1.0)
        ) {
            EtherealEntityAttachment.getData(it).isEthereal
        }

        val hasGuard = level.getEntities(
            GuardEntityType.GUARD.get(),
            AABB.ofSize(pos.center, 1.0, 1.0, 1.0)
        ) {
            EtherealEntityAttachment.getData(it).isEthereal
        }

        val hasZombieVill = level.getEntities(
            EntityType.ZOMBIE_VILLAGER,
            AABB.ofSize(pos.center, 1.0, 1.0, 1.0)
        ) {
            EtherealEntityAttachment.getData(it).isEthereal
        }

        val foundSoulCage = BlockPos.betweenClosedStream(AABB.ofSize(pos.center, 3.0, 3.0, 3.0))
            .toList().firstNotNullOfOrNull { blockPos ->
                val blockEntity = level.getBlockEntity(blockPos)
                if (blockEntity is SoulCageBlockEntity && blockEntity.hasSoul) {
                    blockEntity
                } else {
                    null
                }
            }

        if (hasVillager.isEmpty() && hasGuard.isEmpty() && hasZombieVill.isEmpty() && foundSoulCage == null) {
            return
        }

        val soulEntity = if (hasVillager.isNotEmpty()) {
            hasVillager[0]
        } else if (hasZombieVill.isNotEmpty()) {
            hasZombieVill[0]
        } else if (hasGuard.isNotEmpty()) {
            hasGuard[0]
        } else {
            null
        }

        val ne = pos.north().east().below()
        val nw = pos.north().west().below()
        val se = pos.south().east().below()
        val sw = pos.south().west().below()


        val hasCaveVines =
            (level.getBlockState(ne).`is`(Blocks.CAVE_VINES) || level.getBlockState(ne).`is`(Blocks.CAVE_VINES_PLANT)) &&
            (level.getBlockState(nw).`is`(Blocks.CAVE_VINES) || level.getBlockState(nw).`is`(Blocks.CAVE_VINES_PLANT)) &&
            (level.getBlockState(se).`is`(Blocks.CAVE_VINES) || level.getBlockState(se).`is`(Blocks.CAVE_VINES_PLANT)) &&
            (level.getBlockState(sw).`is`(Blocks.CAVE_VINES) || level.getBlockState(sw).`is`(Blocks.CAVE_VINES_PLANT))

        if (!hasCaveVines) return

        val nea = pos.north().east().above()
        val nwa = pos.north().west().above()
        val sea = pos.south().east().above()
        val swa = pos.south().west().above()

        val hasChorusPlants =
            (level.getBlockState(nea).`is`(Blocks.CHORUS_PLANT) || level.getBlockState(nea).`is`(Blocks.CHORUS_FLOWER)) &&
            (level.getBlockState(nwa).`is`(Blocks.CHORUS_PLANT) || level.getBlockState(nea).`is`(Blocks.CHORUS_FLOWER)) &&
            (level.getBlockState(sea).`is`(Blocks.CHORUS_PLANT) || level.getBlockState(nea).`is`(Blocks.CHORUS_FLOWER)) &&
            (level.getBlockState(swa).`is`(Blocks.CHORUS_PLANT) || level.getBlockState(nea).`is`(Blocks.CHORUS_FLOWER))

        if (!hasChorusPlants) return

        if (level is ServerLevel) {
            soulEntity?.discard()
            foundSoulCage?.extractSoul()

            listOf(
                pos.north().east().below(),
                pos.north().west().below(),
                pos.south().east().below(),
                pos.south().west().below()
            ).forEach { vinePos ->
                level.setBlockAndUpdate(
                    vinePos,
                    WitcheryBlocks.LIFE_BLOOD.get().defaultBlockState()
                        .setValue(CaveVinesPlantBlock.BERRIES, true)
                )
            }

            level.setBlockAndUpdate(pos.north().east().above(), Blocks.AIR.defaultBlockState())
            level.setBlockAndUpdate(pos.north().west().above(), Blocks.AIR.defaultBlockState())
            level.setBlockAndUpdate(pos.south().east().above(), Blocks.AIR.defaultBlockState())
            level.setBlockAndUpdate(pos.south().west().above(), Blocks.AIR.defaultBlockState())

            spawnLifebloodTransformationParticles(level, pos)

            level.playSound(
                null,
                pos,
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.BLOCKS,
                1.0f,
                0.8f
            )

            level.playSound(
                null,
                pos,
                SoundEvents.SOUL_ESCAPE.value(),
                SoundSource.BLOCKS,
                1.5f,
                1.2f
            )
        }
    }

    private fun spawnLifebloodTransformationParticles(level: ServerLevel, pos: BlockPos) {
        val center = pos.center

        for (i in 0..50) {
            val angle = (i / 50.0) * Math.PI * 2
            val radius = i / 25.0
            val x = center.x + Math.cos(angle) * radius
            val z = center.z + Math.sin(angle) * radius

            level.sendParticles(
                ParticleTypes.SOUL,
                x, center.y + 0.5, z,
                2,
                0.0, 0.1, 0.0, 0.05
            )
        }

        for (i in 0..60) {
            val angle = (i / 60.0) * Math.PI * 6
            val radius = 0.5 + (i / 60.0) * 2.0
            val height = (i / 60.0) * 3.0

            val x = center.x + Math.cos(angle) * radius
            val z = center.z + Math.sin(angle) * radius
            val y = center.y + height

            level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z,
                1,
                0.0, 0.0, 0.0, 0.0
            )
        }

        listOf(
            pos.north().east(),
            pos.north().west(),
            pos.south().east(),
            pos.south().west()
        ).forEach { chorusPos ->
            val chorusCenter = chorusPos.center
            for (i in 0..20) {
                level.sendParticles(
                    ParticleTypes.PORTAL,
                    chorusCenter.x,
                    chorusCenter.y,
                    chorusCenter.z,
                    1,
                    Mth.nextDouble(level.random, -0.3, 0.3),
                    Mth.nextDouble(level.random, 0.1, 0.5),
                    Mth.nextDouble(level.random, -0.3, 0.3),
                    0.2
                )
            }
        }

        listOf(
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west()
        ).forEach { vinePos ->
            val vineCenter = vinePos.center
            for (i in 0..15) {
                level.sendParticles(
                    ParticleTypes.FALLING_NECTAR,
                    vineCenter.x,
                    vineCenter.y + i * 0.1,
                    vineCenter.z,
                    1,
                    0.0, -0.1, 0.0, 0.0
                )
            }
        }
    }

    private fun makeCritterSnare(level: Level, pos: BlockPos) {
        val hasZombie = level.getEntities(EntityType.ZOMBIE, AABB.ofSize(pos.center, 1.0, 1.0, 1.0)) { true }
        val hasWolf = level.getEntities(EntityType.WOLF, AABB.ofSize(pos.center, 1.0, 1.0, 1.0)) { true }
        if (hasZombie.isNotEmpty()) {
            if (checkCardinal(level, pos, WitcheryBlocks.ALDER_SAPLING.get()) && level.getBlockState(pos.below())
                    .`is`(Blocks.WATER)
            ) {
                removeCardinal(level, pos)
                hasZombie.forEach { it.discard() }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState())
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
                level.setBlockAndUpdate(pos.south(), WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState())
                level.setBlockAndUpdate(pos.north(), WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState())
                level.setBlockAndUpdate(pos.west(), WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState())
                level.setBlockAndUpdate(pos.east(), WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState())
            }
        }
        if (hasWolf.isNotEmpty()) {
            if (level.getBlockState(pos.below()).`is`(Blocks.WATER)) {
                val batN = level.getBlockState(pos.north()).`is`(WitcheryBlocks.CRITTER_SNARE.get())
                        && level.getBlockState(pos.north()).hasProperty(CritterSnareBlock.CAPTURED_STATE)
                        && level.getBlockState(pos.north())
                    .getValue(CritterSnareBlock.CAPTURED_STATE) == CritterSnareBlock.CapturedEntity.BAT
                val batS = level.getBlockState(pos.south()).`is`(WitcheryBlocks.CRITTER_SNARE.get())
                        && level.getBlockState(pos.south()).hasProperty(CritterSnareBlock.CAPTURED_STATE)
                        && level.getBlockState(pos.south())
                    .getValue(CritterSnareBlock.CAPTURED_STATE) == CritterSnareBlock.CapturedEntity.BAT
                val batE = level.getBlockState(pos.east()).`is`(WitcheryBlocks.CRITTER_SNARE.get())
                        && level.getBlockState(pos.east()).hasProperty(CritterSnareBlock.CAPTURED_STATE)
                        && level.getBlockState(pos.east())
                    .getValue(CritterSnareBlock.CAPTURED_STATE) == CritterSnareBlock.CapturedEntity.BAT
                val batW = level.getBlockState(pos.west()).`is`(WitcheryBlocks.CRITTER_SNARE.get())
                        && level.getBlockState(pos.west()).hasProperty(CritterSnareBlock.CAPTURED_STATE)
                        && level.getBlockState(pos.west())
                    .getValue(CritterSnareBlock.CAPTURED_STATE) == CritterSnareBlock.CapturedEntity.BAT

                if (batN || batS || batW || batE) {

                    val grassperNW = level.getBlockEntity(pos.north().west())
                    val grassperNE = level.getBlockEntity(pos.north().east())
                    val grassperSW = level.getBlockEntity(pos.south().west())
                    val grassperSE = level.getBlockEntity(pos.south().east())
                    if (grassperNW is GrassperBlockEntity && grassperNE is GrassperBlockEntity && grassperSW is GrassperBlockEntity && grassperSE is GrassperBlockEntity) {
                        val items = listOf(
                            grassperNW.getItem(0),
                            grassperNE.getItem(0),
                            grassperSW.getItem(0),
                            grassperSE.getItem(0)
                        )

                        val mutandis = WitcheryItems.MUTANDIS_EXTREMIS.get()
                        val attunedStone = WitcheryItems.ATTUNED_STONE.get()

                        val mutandisCount = items.count { it.`is`(mutandis) }
                        val attunedStoneCount =
                            items.count { it.`is`(attunedStone) && it.get(WitcheryDataComponents.ATTUNED.get()) == true }

                        if (mutandisCount >= 3 && attunedStoneCount >= 1) {

                            var mutandisLeft = 3
                            var attunedStoneLeft = 1

                            for (itemStack in items) {
                                when (itemStack.item) {
                                    mutandis -> {
                                        val toConsume = minOf(mutandisLeft, itemStack.count)
                                        itemStack.shrink(toConsume)
                                        mutandisLeft -= toConsume
                                    }

                                    attunedStone -> {
                                        val toConsume = minOf(attunedStoneLeft, itemStack.count)
                                        itemStack.shrink(toConsume)
                                        attunedStoneLeft -= toConsume
                                    }
                                }
                            }

                            if (batN) {
                                val owl = WitcheryEntityTypes.OWL.get().create(level)
                                val no = pos.north()
                                owl?.let {
                                    it.moveTo(no.x + 0.5, no.y + 0.5, no.z + 0.5, 0f, 0f)
                                    level.addFreshEntity(it)
                                }
                                level.setBlockAndUpdate(
                                    pos.north(),
                                    WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState()
                                )
                            }
                            if (batS) {
                                val owl = WitcheryEntityTypes.OWL.get().create(level)
                                val no = pos.south()
                                owl?.let {
                                    it.moveTo(no.x + 0.5, no.y + 0.5, no.z + 0.5, 0f, 0f)
                                    level.addFreshEntity(it)
                                }
                                level.setBlockAndUpdate(
                                    pos.south(),
                                    WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState()
                                )
                            }
                            if (batE) {
                                val owl = WitcheryEntityTypes.OWL.get().create(level)
                                val no = pos.east()
                                owl?.let {
                                    it.moveTo(no.x + 0.5, no.y + 0.5, no.z + 0.5, 0f, 0f)
                                    level.addFreshEntity(it)
                                }
                                level.setBlockAndUpdate(
                                    pos.east(),
                                    WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState()
                                )
                            }
                            if (batW) {
                                val owl = WitcheryEntityTypes.OWL.get().create(level)
                                val no = pos.west()
                                owl?.let {
                                    it.moveTo(no.x + 0.5, no.y + 0.5, no.z + 0.5, 0f, 0f)
                                    level.addFreshEntity(it)
                                }
                                level.setBlockAndUpdate(
                                    pos.west(),
                                    WitcheryBlocks.CRITTER_SNARE.get().defaultBlockState()
                                )
                            }
                            hasWolf.forEach { it.discard() }
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                            level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState())
                        }
                    }
                }
            }
        }
    }

    private fun checkCardinal(level: Level, pos: BlockPos, block: Block): Boolean {
        return level.getBlockState(pos.north()).`is`(block) &&
                level.getBlockState(pos.south()).`is`(block) &&
                level.getBlockState(pos.east()).`is`(block) &&
                level.getBlockState(pos.west()).`is`(block)
    }

    private fun checkWaterDiagonals(level: Level, pos: BlockPos): Boolean {
        return isWaterloggedOrWater(level.getBlockState(pos.north().east().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.north().west().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.south().east().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.south().west().below()))
    }

    private fun isWaterloggedOrWater(state: BlockState): Boolean {
        return state.`is`(Blocks.WATER) || (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(
            BlockStateProperties.WATERLOGGED
        ))
    }

    private fun removeDiagonals(level: Level, pos: BlockPos) {
        listOf(
            pos.north().east().below(),
            pos.north().west().below(),
            pos.south().east().below(),
            pos.south().west().below()
        ).forEach { diagonalPos ->
            val state = level.getBlockState(diagonalPos)
            if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
                level.setBlockAndUpdate(diagonalPos, state.setValue(BlockStateProperties.WATERLOGGED, false))
            } else if (state.`is`(Blocks.WATER)) {
                level.setBlockAndUpdate(diagonalPos, Blocks.AIR.defaultBlockState())
            }
            val center = diagonalPos.center
            for (i in 0..16) {
                level.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE,
                    true,
                    center.x + 0.0 + Mth.nextDouble(level.random, -0.5, 0.5),
                    (center.y + 0.0) + Mth.nextDouble(level.random, -1.25, 1.25),
                    center.z + 0.0 + Mth.nextDouble(level.random, -0.5, 0.5),
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    private fun removeCardinal(level: Level, pos: BlockPos) {
        listOf(
            pos.east(),
            pos.north(),
            pos.south(),
            pos.west()
        ).forEach { cardinal ->
            level.setBlockAndUpdate(cardinal, Blocks.AIR.defaultBlockState())
            val center = cardinal.center
            for (i in 0..16) {
                level.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE,
                    true,
                    center.x + Mth.nextDouble(level.random, -0.5, 0.5),
                    (center.y) + Mth.nextDouble(level.random, -1.25, 1.25),
                    center.z + Mth.nextDouble(level.random, -0.5, 0.5),
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}