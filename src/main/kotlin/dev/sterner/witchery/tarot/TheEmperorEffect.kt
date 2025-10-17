package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class TheEmperorEffect : TarotEffect(5) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Emperor (Reversed)" else "The Emperor"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Authority crumbles" else "Command and control"
    )

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (isReversed) {
            player.giveExperiencePoints(-10)
        } else {
            player.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 12000, 0))
        }
    }

    override fun onEntityHit(player: Player, target: Entity, isReversed: Boolean) {
        if (!isReversed && target is LivingEntity) {
            if (player.level().random.nextFloat() < 0.15f) {
                target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1))
            }
        }
    }
}