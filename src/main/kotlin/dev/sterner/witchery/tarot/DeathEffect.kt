package dev.sterner.witchery.tarot

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class DeathEffect : TarotEffect(14) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Death (Reversed)" else "Death"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Stagnation and decay" else "Transformation through endings"
    )

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed) {
            if (player.level().random.nextFloat() < 0.05f && player.level() is ServerLevel) {
                //TODO Chance to spawn spectral version
                // Could spawn spectral pig or other spectral entities
                player.displayClientMessage(
                    Component.literal("Not implemented").withStyle(ChatFormatting.DARK_PURPLE),
                    true
                )
            }
        }
    }

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            if (player.level().gameTime % 200 == 0L) {
                player.causeFoodExhaustion(0.5f)
            }
        }
    }

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            val negativeEffects = player.activeEffects
                .filter { !it.effect.value().isBeneficial }
                .map { it.effect }

            negativeEffects.forEach { player.removeEffect(it) }
        }
    }
}