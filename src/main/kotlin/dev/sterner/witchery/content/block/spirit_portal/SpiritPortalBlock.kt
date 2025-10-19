package dev.sterner.witchery.content.block.spirit_portal


import dev.sterner.witchery.api.multiblock.MultiBlockHorizontalDirectionStructure
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.features.misc.ManifestationHandler
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier
import java.util.function.ToIntFunction

class SpiritPortalBlock(properties: Properties) : WitcheryBaseEntityBlock(
    properties.noCollission().lightLevel(
        litBlockEmission(8)
    )
) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.OPEN, false)
        )
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING, BlockStateProperties.OPEN)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val dir = state.getValue(HORIZONTAL_FACING)
        return when (dir) {
            Direction.NORTH -> {
                NORTH
            }

            Direction.EAST -> {
                EAST
            }

            Direction.WEST -> {
                WEST
            }

            else -> {
                SOUTH
            }
        }
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        if (state.getValue(BlockStateProperties.OPEN)) {
            return Shapes.empty()
        }
        val dir = state.getValue(HORIZONTAL_FACING)
        return when (dir) {
            Direction.NORTH -> {
                NORTH
            }

            Direction.EAST -> {
                EAST
            }

            Direction.WEST -> {
                WEST
            }

            else -> {
                SOUTH
            }
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.SPIRIT_PORTAL.get().create(pos, state)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (entity is Player && state.getValue(BlockStateProperties.OPEN)) {

            val dir = state.getValue(HORIZONTAL_FACING)
            val portalShape = when (dir) {
                Direction.NORTH -> NORTH
                Direction.EAST -> EAST
                Direction.WEST -> WEST
                Direction.SOUTH -> SOUTH
                else -> return
            }

            val portalBoundingBox = portalShape.bounds().move(pos)

            if (portalBoundingBox.move(pos).intersects(entity.boundingBox)) {
                handleEntityInside(level, entity)
            }
        }

        super.entityInside(state, level, pos, entity)
    }

    fun handleEntityInside(level: Level, entity: Player) {
        if (ManifestationPlayerAttachment.getData(entity).hasRiteOfManifestation) {
            val overworld = level.server?.overworld()
            if (overworld != null) {
                val itemsToKeep = mutableListOf<ItemStack>()

                for (i in 0 until entity.inventory.containerSize) {
                    val itemStack = entity.inventory.getItem(i)
                    if (itemStack.item == WitcheryItems.ICY_NEEDLE.get()) {
                        itemsToKeep.add(entity.inventory.removeItem(i, itemStack.count))
                    }
                }

                entity.inventory.dropAll()

                for (keep in itemsToKeep) {
                    entity.inventory.add(keep.copy())
                }

                ManifestationHandler.setManifestationTimer(entity)
                entity.teleportTo(overworld, entity.x, entity.y, entity.z, setOf(), entity.yHeadRot, entity.xRot)
            }
        }
    }

    companion object {
        val SOUTH: VoxelShape = Shapes.create(0.0, 0.0, 13.0 / 16, 1.0, 1.0, 1.0)
        val EAST: VoxelShape = Shapes.create(13.0 / 16, 0.0, 0.0, 1.0, 1.0, 1.0)
        val WEST: VoxelShape = Shapes.create(0.0, 0.0, 0.0, 3.0 / 16, 1.0, 1.0)
        val NORTH: VoxelShape = Shapes.create(0.0, 0.0, 0.0, 1.0, 1.0, 3.0 / 16)

        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        BlockStateProperties.OPEN
                    ) as Boolean
                ) lightValue else 4
            }
        }

        val STRUCTURE: Supplier<MultiBlockHorizontalDirectionStructure> =
            Supplier<MultiBlockHorizontalDirectionStructure> {
                (MultiBlockHorizontalDirectionStructure.of(
                    MultiBlockStructure.StructurePiece(
                        1,
                        0,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        1,
                        1,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get().defaultBlockState()
                    ),
                ))
            }
    }
}