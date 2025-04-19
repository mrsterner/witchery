package dev.sterner.witchery.api

import net.minecraft.world.entity.LivingEntity

interface OnRemovedEffect {

    fun onRemovedEffect(entity: LivingEntity)
}