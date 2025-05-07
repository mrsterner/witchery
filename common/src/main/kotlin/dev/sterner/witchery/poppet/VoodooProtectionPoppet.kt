package dev.sterner.witchery.poppet

import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

class VoodooProtectionPoppet : PoppetType {
    override val item = WitcheryItems.VOODOO_PROTECTION_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean = true

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when(usage) {
        PoppetUsage.EFFECT -> 1
        else -> 0
    }

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean = false
}