package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

class DeathProtectionPoppet : PoppetType {
    override val item = WitcheryItems.DEATH_PROTECTION_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean {
        return source == null || !source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when (usage) {
        PoppetUsage.PROTECTION -> 1
        else -> 0
    }

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.TOTEM_USE,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
        return true
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.WITCH_DEATH,
            SoundSource.PLAYERS,
            1.0f,
            0.8f
        )

        return false
    }
}