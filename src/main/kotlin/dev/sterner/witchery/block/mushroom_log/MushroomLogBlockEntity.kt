package dev.sterner.witchery.block.mushroom_log

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import kotlin.math.min
import net.minecraft.nbt.NbtOps
import team.lodestar.lodestone.systems.multiblock.MultiBlockCoreEntity

class MushroomLogBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.MUSHROOM_LOG.get(), MushroomLogBlock.STRUCTURE.get(), blockPos, blockState) {

    var currentMushroom: ItemStack = ItemStack.EMPTY

    var growthStage: Float = 0.0f
    private val maxGrowthStage: Float = 1.0f
    private val growthRate: Float = 0.005f

    private val mushroomPositions = mutableListOf<MushroomData>()
    private val random = RandomSource.create(blockPos.asLong())

    private val harvestAmount = 1

    data class MushroomData(
        val xOffset: Float, 
        val zOffset: Float,
        val rotation: Float,
        var scale: Float,
        val targetScale: Float,
        val growthOffset: Float
    ) {
        companion object {
            val CODEC: Codec<MushroomData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.FLOAT.fieldOf("xOffset").forGetter { it.xOffset },
                    Codec.FLOAT.fieldOf("zOffset").forGetter { it.zOffset },
                    Codec.FLOAT.fieldOf("rotation").forGetter { it.rotation },
                    Codec.FLOAT.fieldOf("scale").forGetter { it.scale },
                    Codec.FLOAT.fieldOf("targetScale").forGetter { it.targetScale },
                    Codec.FLOAT.fieldOf("growthOffset").forGetter { it.growthOffset }
                ).apply(instance, ::MushroomData)
            }
        }
    }
    
    /**
     * Set the mushroom type and reset growth
     */
    fun setMushroom(mushroom: ItemStack) {
        val shouldGeneratePositions = currentMushroom.isEmpty || 
                currentMushroom.item != mushroom.item ||
                mushroomPositions.isEmpty()
                
        currentMushroom = mushroom.copy()
        growthStage = 0.0f
        
        if (shouldGeneratePositions) {
            generateMushroomPositions()
        } else {
            mushroomPositions.forEach { it.scale = 0f }
        }
        
        setChanged()
    }

    override fun onUseWithItem(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult {
        if (level != null) {
            if (pPlayer.mainHandItem.isEmpty) {
                harvest(level!!, blockPos)?.let {
                    Containers.dropItemStack(level!!, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), it)
                }
            } else if (pStack.`is`(WitcheryTags.MUSHROOMS)) {
                Containers.dropItemStack(level!!, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), currentMushroom.copy())
                setMushroom(pStack)
                pPlayer.mainHandItem.shrink(1)
                pPlayer.swing(pHand)
                return ItemInteractionResult.SUCCESS
            }

        }
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    /**
     * Generate random positions for mushrooms across the entire two-block log
     */
    private fun generateMushroomPositions() {
        val seed = blockPos.asLong() +
                if(currentMushroom.isEmpty) 0 else currentMushroom.item.descriptionId.hashCode().toLong()
        random.setSeed(seed)

        mushroomPositions.clear()

        val count = 4 + random.nextInt(4)

        val positions = mutableListOf<Pair<Float, Float>>()

        for (i in 0 until 30) {
            val x = -0.4f + random.nextFloat() * 0.8f

            val z = -1.5f + random.nextFloat() * 2.0f

            var tooClose = false
            for (pos in positions) {
                val dx = pos.first - x
                val dz = pos.second - z
                val distSq = dx * dx + dz * dz
                if (distSq < 0.2f * 0.2f) {
                    tooClose = true
                    break
                }
            }

            if (!tooClose) {
                positions.add(Pair(x, z))
            }
        }

        val shuffledPositions = positions.shuffled(Random(seed))
        val selectedPositions = shuffledPositions.take(count)

        for (pos in selectedPositions) {
            val rotation = random.nextFloat() * 360f
            val targetScale = 0.4f + random.nextFloat() * 0.6f
            val growthOffset = random.nextFloat() * 0.3f

            mushroomPositions.add(
                MushroomData(
                    pos.first,
                    pos.second,
                    rotation,
                    0f,
                    targetScale,
                    growthOffset
                )
            )
        }
    }

    /**
     * Handle growth tick
     */
    override fun serverTick(level: ServerLevel) {

        if (level.gameTime % 20 == 0L && level.random.nextFloat() < 0.33f) {
            if (!currentMushroom.isEmpty && growthStage < maxGrowthStage) {
                if (canGrow(level, blockPos)) {
                    growthStage = min(maxGrowthStage, growthStage + growthRate)

                    if ((growthStage * 100).toInt() % 10 == 0) {
                        setChanged()
                        level.sendBlockUpdated(blockPos, blockState, blockState, 3)
                    }
                }
            }
        }
    }
    
    /**
     * Check if the mushroom can grow based on environmental conditions
     */
    private fun canGrow(level: Level, pos: BlockPos): Boolean {
        if (level !is ServerLevel) return false

        val lightLevel = level.getBrightness(LightLayer.BLOCK, pos)
        val isRaining = level.isRaining

        return random.nextFloat() < (1.0f - lightLevel / 15.0f) * (if (isRaining) 1.5f else 1.0f)
    }
    
    /**
     * Harvest mushrooms when player right-clicks
     */
    fun harvest(level: Level, pos: BlockPos): ItemStack? {
        if (currentMushroom.isEmpty || growthStage < 0.5f) return null

        val harvestedMushroom = currentMushroom.copy()
        harvestedMushroom.count = harvestAmount

        growthStage = 0.2f.coerceAtLeast(growthStage - 0.3f)

        setChanged()

        level.playSound(
            null,
            pos,
            SoundEvents.FUNGUS_BREAK,
            SoundSource.BLOCKS,
            0.8f,
            0.8f + level.random.nextFloat() * 0.4f
        )
        
        return harvestedMushroom
    }
    
    /**
     * Get mushroom positions with current scales based on growth
     */
    fun getMushroomData(): List<MushroomData> {
        if (!currentMushroom.isEmpty && mushroomPositions.isEmpty()) {
            generateMushroomPositions()
        }
        
        mushroomPositions.forEach { data ->
            val actualGrowth = maxOf(0f, growthStage - data.growthOffset)
            data.scale = actualGrowth * data.targetScale
        }
        
        return mushroomPositions
    }
    
    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        if (!currentMushroom.isEmpty) {
            val itemTag = currentMushroom.save(registries)
            tag.put("currentMushroom", itemTag)
        }
        tag.putFloat("growthStage", growthStage)

        val mushroomPositionsNbt = MushroomData.CODEC.listOf()
            .encodeStart(NbtOps.INSTANCE, mushroomPositions)
            .getOrThrow()
    
        tag.put("mushroomPositions", mushroomPositionsNbt)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
    
        if (pTag.contains("currentMushroom", 10)) {
            val itemTag = pTag.getCompound("currentMushroom")
            currentMushroom = ItemStack.parse(pRegistries, itemTag).orElse(ItemStack.EMPTY)
        }
    
        growthStage = pTag.getFloat("growthStage")

        if (pTag.contains("mushroomPositions")) {
            val result = MushroomData.CODEC.listOf()
                .parse(NbtOps.INSTANCE, pTag.get("mushroomPositions"))
                .getOrThrow()
        
            mushroomPositions.clear()
            mushroomPositions.addAll(result)
        }
    
        if (mushroomPositions.isEmpty() && !currentMushroom.isEmpty) {
            generateMushroomPositions()
        }
    }
}