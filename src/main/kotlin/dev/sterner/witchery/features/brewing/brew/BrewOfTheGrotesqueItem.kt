package dev.sterner.witchery.features.brewing.brew

import dev.sterner.witchery.core.registry.WitcheryMobEffects
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player


class BrewOfTheGrotesqueItem(color: Int, properties: Properties) : BrewItem(color, properties) {

    override fun applyEffectOnSelf(player: Player, hasFrog: Boolean) {
        val dur = if (hasFrog) 20 * 60 * 2 else 20 * 60
        player.addEffect(MobEffectInstance(WitcheryMobEffects.GROTESQUE, dur))
    }
}