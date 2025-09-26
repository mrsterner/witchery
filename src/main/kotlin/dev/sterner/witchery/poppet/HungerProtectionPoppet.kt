package dev.sterner.witchery.poppet

import dev.sterner.witchery.api.interfaces.PoppetType
import dev.sterner.witchery.api.PoppetUsage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class HungerProtectionPoppet : PoppetType {
    override val item = WitcheryItems.HUNGER_PROTECTION_POPPET.get()

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner is Player) {
            owner.foodData.foodLevel = owner.foodData.foodLevel + 2
            owner.level().playSound(
                null,
                owner.x, owner.y, owner.z,
                SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS,
                0.7f,
                1.0f
            )
        }
        return true
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner !is Player) return onActivate(owner, source)

        owner.foodData.foodLevel = Math.max(0, owner.foodData.foodLevel - 4)

        owner.addEffect(MobEffectInstance(MobEffects.HUNGER, 200, 2))

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
            SoundEvents.PLAYER_BURP,
            SoundSource.PLAYERS,
            0.7f,
            0.8f
        )

        owner.displayClientMessage(
            Component.translatable("curse.witchery.corrupt_poppet.hunger_effect")
                .withStyle(ChatFormatting.DARK_PURPLE),
            true
        )

        return true
    }

    override fun isValidFor(entity: LivingEntity, source: DamageSource?): Boolean {
        return source?.`is`(DamageTypes.STARVE) == true
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int {
        return 1
    }
}