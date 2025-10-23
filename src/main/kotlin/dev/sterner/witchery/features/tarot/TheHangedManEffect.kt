package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.content.block.altar.AltarBlockEntity
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player

class TheHangedManEffect : TarotEffect(13) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hanged_man.reversed" else "tarot.witchery.the_hanged_man"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hanged_man.reversed.description" else "tarot.witchery.the_hanged_man.description"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            //handled in event
        } else {
            if (player.deltaMovement.y < 0 && !player.onGround()) {
                player.deltaMovement = player.deltaMovement.multiply(1.0, 0.7, 1.0)
            }
        }
    }

    override fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        if (!isReversed && amount > 5f && player.level() is ServerLevel) {
            val altar = AltarBlockEntity.getClosestAltar(
                player.level() as ServerLevel,
                player.blockPosition(),
                16
            )
            altar?.let {
                it.currentPower = (it.currentPower + (amount * 100).toInt()).coerceAtMost(it.maxPower)
            }
        }
        return amount
    }
}