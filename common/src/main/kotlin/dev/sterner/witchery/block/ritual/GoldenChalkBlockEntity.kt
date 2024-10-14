package dev.sterner.witchery.block.ritual

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class GoldenChalkBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GOLDEN_CHALK.get(), blockPos, blockState) {

    val ritualRecipe: RitualRecipe? = null

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)
    }

    override fun init(level: Level, pos: BlockPos, state: BlockState) {
        super.init(level, pos, state)
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        return super.onUseWithoutItem(pPlayer)
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
    }
}