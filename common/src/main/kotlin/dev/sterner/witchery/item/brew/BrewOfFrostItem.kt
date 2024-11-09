package dev.sterner.witchery.item.brew

import com.google.common.base.Predicate
import net.minecraft.core.Direction
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import kotlin.math.min

class BrewOfFrostItem(color: Int, properties: Properties, predicate: Predicate<Direction> = Predicate { true }) :
    ThrowableBrewItem(color, properties, predicate) {

    override fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity) {
        livingEntity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 4, 0))
        livingEntity.extinguishFire()
        val req = livingEntity.ticksRequiredToFreeze
        val oldFreeze = livingEntity.ticksFrozen
        livingEntity.ticksFrozen = min(oldFreeze + 20 * 4, req)
        super.applyEffectOnEntities(level, livingEntity)
    }

    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult) {
        val list = BrewOfErosionItem.collectPositionsInSphere(blockHit.blockPos, 2)

        for (pos in list) {
            if (level.getBlockState(pos).`is`(Blocks.WATER)) {
                level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState())
            }
            if (level.getBlockState(pos).`is`(Blocks.LAVA)) {
                level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState())
            }
        }

        super.applyEffectOnBlock(level, blockHit)
    }
}