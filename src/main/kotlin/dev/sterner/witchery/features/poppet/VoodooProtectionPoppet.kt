package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class VoodooProtectionPoppet : PoppetType {
    override val item = WitcheryItems.VOODOO_PROTECTION_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean = true

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when (usage) {
        PoppetUsage.EFFECT -> 1
        PoppetUsage.PROTECTION -> 1
        else -> 0
    }

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.SHIELD_BLOCK,
            SoundSource.PLAYERS,
            0.7f,
            1.3f
        )
        return true
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner !is Player) return false

        owner.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 200, 1))

        if (owner.level() is ServerLevel) {
            val serverLevel = owner.level() as ServerLevel
            serverLevel.sendParticles(
                ParticleTypes.WITCH,
                owner.x, owner.y + owner.bbHeight * 0.5, owner.z,
                15, 0.3, 0.3, 0.3, 0.05
            )
        }

        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.PLAYERS,
            0.7f,
            0.5f
        )

        return false
    }
}