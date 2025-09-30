package dev.sterner.witchery.util

import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object DamageHelper {

    fun tryProxyDamage(source: DamageSource, attacker: LivingEntity): DamageSource? {
        val delegate = if (attacker is Player) {
            PossessionComponentAttachment.get(attacker).getHost()
        } else {
            null
        }
        return delegate?.let { createProxiedDamage(source, it) }
    }

    fun createProxiedDamage(source: DamageSource, newAttacker: Entity): DamageSource? {
        return if (source.entity != null) {
            val damageType = source.typeHolder()
            newAttacker.damageSources().source(damageType.key!!, source.directEntity, source.entity)
        } else {
            null
        }
    }
}