package dev.sterner.witchery.content.block.life_blood

import dev.sterner.witchery.content.block.Lifeblood
import dev.sterner.witchery.core.registry.WitcheryBlocks.LIFE_BLOOD
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.CaveVinesPlantBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.GrowingPlantHeadBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class LifeBloodPlantBlock(properties: Properties) : CaveVinesPlantBlock(properties), Lifeblood, EntityBlock {

    override fun getCloneItemStack(
        levelReader: LevelReader,
        blockPos: BlockPos,
        blockState: BlockState
    ): ItemStack {
        return ItemStack(WitcheryItems.LIFEBLOOD_BERRY.get())
    }

    override fun getHeadBlock(): GrowingPlantHeadBlock {
        return LIFE_BLOOD.get()
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        return Lifeblood.use(player, blockState, level, blockPos)
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return LifeBloodBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }
}