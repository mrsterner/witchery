package dev.sterner.witchery.api.interfaces

import net.minecraft.world.entity.LivingEntity

interface OnRemovedEffect {

    fun onRemovedEffect(entity: LivingEntity)
}