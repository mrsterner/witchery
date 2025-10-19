package dev.sterner.witchery.core.api

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.core.world.WitcheryWorldState
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult

open class SpecialPotion(val id: ResourceLocation) {

    constructor(id: String) : this(Witchery.id(id))

    open fun onDrunk(
        level: Level,
        owner: LivingEntity?,
        duration: Int,
        amplifier: Int
    ) {

    }

    open fun onActivated(
        level: Level,
        owner: Entity?,
        hitResult: HitResult,
        list: MutableList<Entity>,
        mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier,
        duration: Int,
        amplifier: Int
    ) {

    }

    fun getBox(hitResult: HitResult, mergedDispersalModifier: WitcheryPotionIngredient.DispersalModifier): AABB {
        return AABB.ofSize(
            hitResult.location,
            4 * mergedDispersalModifier.rangeModifier.toDouble(),
            2 * mergedDispersalModifier.rangeModifier.toDouble(),
            4 * mergedDispersalModifier.rangeModifier.toDouble()
        )
    }

    fun partLiquidFor(level: Level, box: AABB, fluid: Fluid, seconds: Int = 20) {
        val serverLevel = level as? ServerLevel ?: return
        val data = WitcheryWorldState.get(serverLevel)
        val origin = GlobalPos.of(serverLevel.dimension(), BlockPos.containing(box.center))

        val stateMap = mutableMapOf<BlockPos, BlockState>()
        BlockPos.betweenClosedStream(box).forEach { pos ->
            val state = level.getBlockState(pos)
            if (state.fluidState.`is`(fluid)) {
                stateMap[pos.immutable()] = state
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
            }
        }

        data.pendingRestores[origin] = 20 * seconds to stateMap
        data.setDirty()
    }
}