package dev.sterner.witchery.poppet

import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity

class HungerProtectionPoppet : PoppetType {
    override val item = WitcheryItems.HUNGER_PROTECTION_POPPET.get()

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        return false
    }

    override fun isValidFor(entity: LivingEntity, source: DamageSource?): Boolean {
        return source?.`is`(DamageTypes.STARVE) == true
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int {
        return 1
    }
}