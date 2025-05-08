package dev.sterner.witchery.handler.poppet

import dev.sterner.witchery.registry.WitcheryPoppetRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item

interface PoppetType {

    val item: Item

    fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean

    fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean

    fun getDurabilityDamage(usage: PoppetUsage): Int

    fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        return onActivate(owner, source)
    }

    fun handleItemEntityTick(entity: ItemEntity) {

    }

    fun canBeCorrupted(): Boolean {
        return true
    }

    fun getRegistryId(): ResourceLocation {
        return WitcheryPoppetRegistry.getIdForPoppet(this)
    }
}