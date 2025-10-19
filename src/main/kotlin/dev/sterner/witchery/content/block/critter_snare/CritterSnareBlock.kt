package dev.sterner.witchery.content.block.critter_snare


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.monster.Silverfish
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class CritterSnareBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        registerDefaultState(
            stateDefinition.any().setValue(CAPTURED_STATE, CapturedEntity.NONE)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(CAPTURED_STATE)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val cap = if (context.itemInHand.has(WitcheryDataComponents.CAPTURED_ENTITY.get())) context.itemInHand.get(
            WitcheryDataComponents.CAPTURED_ENTITY.get()
        ) else CapturedEntity.NONE

        return defaultBlockState().setValue(
            CAPTURED_STATE,
            cap!!
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.CRITTER_SNARE.get().create(pos, state)
    }

    private fun mayPlaceOn(state: BlockState, level: BlockGetter, pos: BlockPos?): Boolean {
        return state.`is`(BlockTags.DIRT) || state.`is`(Blocks.GRASS_BLOCK)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val cant = !state.canSurvive(level, pos)
        return if (cant) Blocks.AIR.defaultBlockState() else super.updateShape(
            state,
            direction,
            neighborState,
            level,
            pos,
            neighborPos
        )
    }

    override fun spawnAfterBreak(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        stack: ItemStack,
        dropExperience: Boolean
    ) {
        stack.set(WitcheryDataComponents.CAPTURED_ENTITY.get(), state.getValue(CAPTURED_STATE))
        super.spawnAfterBreak(state, level, pos, stack, dropExperience)
        stack.set(WitcheryDataComponents.CAPTURED_ENTITY.get(), state.getValue(CAPTURED_STATE))
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val stack = WitcheryItems.CRITTER_SNARE.get().defaultInstance
        stack.set(WitcheryDataComponents.CAPTURED_ENTITY.get(), state.getValue(CAPTURED_STATE))

        val list = mutableListOf<ItemStack>()
        list.add(stack)

        return list
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState): ItemStack {
        val snare = WitcheryItems.CRITTER_SNARE.get().defaultInstance

        snare.set(WitcheryDataComponents.CAPTURED_ENTITY.get(), state.getValue(CAPTURED_STATE))
        return snare
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val blockPos = pos.below()
        return this.mayPlaceOn(level.getBlockState(blockPos), level, blockPos)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (state.hasProperty(CAPTURED_STATE) && state.getValue(CAPTURED_STATE) == CapturedEntity.NONE) {
            if (entity is Bat) {
                level.setBlockAndUpdate(pos, state.setValue(CAPTURED_STATE, CapturedEntity.BAT))
                entity.discard()
            } else if (entity is Slime && entity.isTiny) {
                level.setBlockAndUpdate(pos, state.setValue(CAPTURED_STATE, CapturedEntity.SLIME))
                entity.discard()
            } else if (entity is Silverfish) {
                level.setBlockAndUpdate(pos, state.setValue(CAPTURED_STATE, CapturedEntity.SILVERFISH))
                entity.discard()
            }
        }

        super.entityInside(state, level, pos, entity)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    companion object {
        val SHAPE: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0)

        val CAPTURED_STATE = EnumProperty.create("captured", CapturedEntity::class.java)
    }

    enum class CapturedEntity : StringRepresentable {
        NONE,
        SLIME,
        SILVERFISH,
        BAT;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val CODEC: StringRepresentable.EnumCodec<CapturedEntity> =
                StringRepresentable.fromEnum { CapturedEntity.entries.toTypedArray() }
        }
    }
}