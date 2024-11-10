package dev.sterner.witchery.item

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.jvm.optionals.getOrNull

class MutandisItem(properties: Properties) : Item(properties) {

    override fun useOn(useOnContext: UseOnContext): InteractionResult {
        val level = useOnContext.level as? ServerLevel ?: return super.useOn(useOnContext)
        val pos = useOnContext.clickedPos
        val state = level.getBlockState(pos)

        useOnContext.player?.let { WitcheryApi.makePlayerWitchy(it) }

        if (handleTagReplacement(
                useOnContext.player,
                level,
                pos,
                state,
                BlockTags.SAPLINGS
            )
        ) return InteractionResult.SUCCESS

        if (handleTagReplacement(
                useOnContext.player,
                level,
                pos,
                state,
                BlockTags.FLOWERS
            )
        ) return InteractionResult.SUCCESS

        if (handleTagReplacement(
                useOnContext.player,
                level,
                pos,
                state,
                WitcheryTags.VINES
            )
        ) return InteractionResult.SUCCESS

        return super.useOn(useOnContext)
    }

    companion object {

        @JvmStatic
        private fun handleTagReplacement(
            player: Player?,
            level: ServerLevel,
            pos: BlockPos,
            state: BlockState,
            tag: TagKey<Block>
        ): Boolean {
            val existingTag = MutandisDataAttachment.getTagForBlockPos(level, pos)

            if (state.`is`(tag) || existingTag != null) {
                val blockToApplyTag = existingTag ?: tag
                val block = getRandomBlockFromTag(blockToApplyTag)

                if (block != null) {
                    level.setBlockAndUpdate(pos, block.defaultBlockState())
                    if (player?.isCreative != true) {
                        player?.mainHandItem?.shrink(1)
                    }
                    if (existingTag == blockToApplyTag) {
                        MutandisDataAttachment.resetTimeForTagBlockPos(level, pos)
                    } else {
                        MutandisDataAttachment.setTagForBlockPos(level, pos, blockToApplyTag)
                    }

                    return true
                }
            }
            return false
        }

        @JvmStatic
        private fun getRandomBlockFromTag(tag: TagKey<Block>): Block? {
            return BuiltInRegistries.BLOCK.getTag(tag).getOrNull()?.map(Holder<Block>::value)?.randomOrNull()
        }
    }
}