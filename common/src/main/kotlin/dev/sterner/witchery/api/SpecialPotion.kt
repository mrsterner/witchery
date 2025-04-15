package dev.sterner.witchery.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryRitualRegistry.RITUALS
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

open class SpecialPotion(val id: ResourceLocation) {

    constructor(id: String): this(Witchery.id(id))

    open fun onActivated(level: Level, player: Player) {

    }


}