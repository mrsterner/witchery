package dev.sterner.witchery.item.brew

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult

class BrewOfWebsItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity) {
        livingEntity.addEffect(MobEffectInstance(MobEffects.WEAVING, 20 * 20, 0))
    }

    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult) {
        val block = level.getBlockState(blockHit.blockPos)
        val dirBlock = level.getBlockState(blockHit.blockPos.relative(blockHit.direction))
        if (block.isAir) {
            level.setBlockAndUpdate(blockHit.blockPos, Blocks.COBWEB.defaultBlockState())
        } else if (dirBlock.isAir) {
            level.setBlockAndUpdate(blockHit.blockPos.relative(blockHit.direction), Blocks.COBWEB.defaultBlockState())
        }
        super.applyEffectOnBlock(level, blockHit)
    }
}