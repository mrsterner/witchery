package dev.sterner.witchery.data_attachment.possession

import dev.sterner.witchery.api.interfaces.Possessable
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.jetbrains.annotations.Nullable


object DamageHelper {

    /**
     * If applicable, returns the possessed entity that is responsible for the damage
     */
    @Nullable
    private fun getPossessionAttacker(source: DamageSource): LivingEntity? {
        // players do not care about humanity anyway, so we check for their possessed entity directly
        val attacker: Entity? = if (source.entity is Player) PossessionManager.getHost(source.entity as Player) else source.entity

        // check that the attacker is being possessed, and that it can use its equipment
        if (attacker is Possessable && attacker.isBeingPossessed && attacker.getType().`is`(WitcheryTags.ITEM_USERS)
        ) {
            return attacker as LivingEntity
        }

        return null
    }

    fun tryProxyDamage(source: DamageSource, attacker: LivingEntity): DamageSource? {
        val delegate: Entity? = PossessionAttachment.getHost(attacker)
        return if (delegate != null) createProxiedDamage(source, delegate) else null
    }

    fun createProxiedDamage(source: DamageSource, newAttacker: Entity): DamageSource? {
        if (source.entity != null && source.typeHolder().key != null) {
            return newAttacker.damageSources().source(
                source.typeHolder().key!!,
                source.directEntity,
                source.entity
            )
        }
        return null
    }


}