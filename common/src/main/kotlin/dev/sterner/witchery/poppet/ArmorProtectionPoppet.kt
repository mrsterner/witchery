package dev.sterner.witchery.poppet

import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.handler.poppet.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item

class ArmorProtectionPoppet : PoppetType {
    override val item: Item = WitcheryItems.ARMOR_PROTECTION_POPPET.get()

    override fun isValidFor(entity: LivingEntity, source: DamageSource?): Boolean {
        return true
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int {
        return 1
    }

    override fun onActivate(entity: LivingEntity, source: DamageSource?): Boolean {
        entity.level().playSound(
            null,
            entity.x, entity.y, entity.z,
            SoundEvents.ARMOR_STAND_HIT,
            SoundSource.PLAYERS,
            0.7f,
            1.2f
        )
        return true
    }
}