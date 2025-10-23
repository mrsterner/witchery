package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class TheEmperorEffect : TarotEffect(5) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_emperor.reversed" else "tarot.witchery.the_emperor"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_emperor.reversed.description" else "tarot.witchery.the_emperor.description"
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