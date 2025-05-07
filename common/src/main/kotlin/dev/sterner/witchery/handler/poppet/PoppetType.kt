package dev.sterner.witchery.handler.poppet

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item

interface PoppetType {
    // The item representing this poppet type
    val item: Item

    // What happens when the poppet is consumed/activated
    fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean

    // Is this poppet valid for the given situation
    fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean

    // How much damage/durability loss should occur when used
    fun getDurabilityDamage(usage: PoppetUsage): Int

    fun handleItemEntityTick(entity: ItemEntity) {

    }
}