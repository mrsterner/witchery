package dev.sterner.witchery.core.api.interfaces

import net.minecraft.world.entity.LivingEntity

interface OnRemovedEffect {

    fun onRemovedEffect(entity: LivingEntity)
}