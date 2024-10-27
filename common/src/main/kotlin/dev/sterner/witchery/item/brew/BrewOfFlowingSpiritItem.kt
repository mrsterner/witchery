package dev.sterner.witchery.item.brew

import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BrewOfFlowingSpiritItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffect(level: Level, livingEntity: LivingEntity?, result: HitResult) {
        var pos = BlockPos.containing(result.location)
        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        } else {
            if (result.type == HitResult.Type.BLOCK) {
                val blockHitResult = result as BlockHitResult
                pos = pos.relative(blockHitResult.direction)
            }
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        }
    }
}