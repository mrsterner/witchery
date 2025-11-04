package dev.sterner.witchery.features.brazier

import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.core.api.BrazierPassive
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class FortificationOfTheCorpseBrazierPassive : BrazierPassive("fortification_of_the_corpse") {

    override fun onTickBrazier(
        level: Level,
        pos: BlockPos,
        blockEntity: BrazierBlockEntity
    ) {
        super.onTickBrazier(level, pos, blockEntity)
        level.getEntitiesOfClass(LivingEntity::class.java, AABB(pos).inflate(8.0)) { it is Mob }.forEach {
            it.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 2, 0))
        }
    }
}