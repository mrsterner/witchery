package dev.sterner.witchery.content.recipe.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag

data class RitualConditions(
    val celestialConditions: Set<RitualRecipe.Celestial> = setOf(),
    val weather: Set<RitualRecipe.Weather> = setOf(),
    val requireCat: Boolean = false,
    val ritualData: CompoundTag = CompoundTag()
) {
    companion object {
        val CODEC: Codec<RitualConditions> = RecordCodecBuilder.create { instance ->
            instance.group(
                RitualRecipe.Celestial.CELESTIAL_SET_CODEC.fieldOf("celestialConditions").orElse(setOf())
                    .forGetter { it.celestialConditions },
                RitualRecipe.Weather.WEATHER_SET_CODEC.fieldOf("weather").orElse(setOf())
                    .forGetter { it.weather },
                Codec.BOOL.fieldOf("requireCat").orElse(false)
                    .forGetter { it.requireCat },
                CompoundTag.CODEC.fieldOf("ritualData").orElse(CompoundTag())
                    .forGetter { it.ritualData }
            ).apply(instance, ::RitualConditions)
        }
    }
}