package dev.sterner.witchery.util

import dev.sterner.witchery.api.interfaces.Possessable
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object DamageHelper {

    private fun getPossessionAttacker(source: DamageSource): LivingEntity? {
        val attacker = if (source.entity is Player) {
            PossessionComponentAttachment.get(source.entity as Player).getHost()
        } else {
            source.entity
        }

        return if (attacker is Possessable && attacker.isBeingPossessed && attacker.type.`is`(WitcheryTags.ITEM_USERS)) {
            attacker as LivingEntity
        } else {
            null
        }
    }

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