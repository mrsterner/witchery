package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.level.Level

class SpectralPigEntity(level: Level) : Pig(WitcheryEntityTypes.SPECTRAL_PIG.get(), level) {
}