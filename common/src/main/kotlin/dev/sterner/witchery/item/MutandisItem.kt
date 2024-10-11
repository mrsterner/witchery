package dev.sterner.witchery.item

import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import kotlin.jvm.optionals.getOrNull

class MutandisItem(properties: Properties): Item(properties) {
    override fun useOn(useOnContext: UseOnContext): InteractionResult {
        val level = useOnContext.level as? ServerLevel ?: return super.useOn(useOnContext)
        val pos = useOnContext.clickedPos
        val state = level.getBlockState(pos)

        if (state.`is`(BlockTags.SAPLINGS)) {
            val block = getRandomBlockFromTag(BlockTags.SAPLINGS) ?: return super.useOn(useOnContext)
            level.setBlockAndUpdate(pos, block.defaultBlockState())
            return InteractionResult.SUCCESS
        }

        if (state.`is`(BlockTags.FLOWERS)) {
            val block = getRandomBlockFromTag(BlockTags.FLOWERS) ?: return super.useOn(useOnContext)
            level.setBlockAndUpdate(pos, block.defaultBlockState())
            return InteractionResult.SUCCESS
        }

        return super.useOn(useOnContext)
    }

    fun getRandomBlockFromTag(tag: TagKey<Block>) =
        BuiltInRegistries.BLOCK.getTag(tag).getOrNull()?.map(Holder<Block>::value)?.random()
}