package dev.sterner.witchery.features.brewing.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.registry.WitcheryMobEffects
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.ItemStack
import java.awt.Color
import java.util.*

data class WitcheryPotionIngredient(
    val item: ItemStack,
    val effect: Holder<MobEffect>,
    val specialEffect: Optional<ResourceLocation>,
    val baseDuration: Int,
    val altarPower: Int,
    val capacityCost: Int = 1,
    val generalModifier: List<GeneralModifier> = mutableListOf(),
    val effectModifier: EffectModifier = EffectModifier(0, 0, 1),
    val dispersalModifier: DispersalModifier = DispersalModifier(1, 1),
    val type: Type = Type.CONSUMABLE,
    val color: Int = Color(90, 222, 100).rgb
) {

    enum class GeneralModifier {
        NO_PARTICLE,
        INVERT_NEXT,
        DRINK_SPEED_BOOST;

        companion object {
            val CODEC: Codec<GeneralModifier> = Codec.STRING.xmap(
                { value -> GeneralModifier.valueOf(value.uppercase()) },
                { it.name.lowercase() }
            )
        }
    }

    enum class Type {
        CONSUMABLE,
        SPLASH,
        LINGERING;

        companion object {
            val CODEC: Codec<Type> = Codec.STRING.xmap(
                { value -> Type.valueOf(value.uppercase()) },
                { it.name.lowercase() }
            )
        }
    }

    data class EffectModifier(
        val powerAddition: Int = 0,
        val durationAddition: Int = 0,
        val durationMultiplier: Int = 1,
    ) {
        companion object {
            val CODEC: Codec<EffectModifier> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.optionalFieldOf("power", 0).forGetter { it.powerAddition },
                    Codec.INT.optionalFieldOf("duration", 0).forGetter { it.durationAddition },
                    Codec.INT.optionalFieldOf("duration_multiplier", 1).forGetter { it.durationMultiplier }
                ).apply(instance, ::EffectModifier)
            }
        }
    }

    data class DispersalModifier(
        val rangeModifier: Int = 1,
        var lingeringDurationModifier: Int = 1,
    ) {
        companion object {
            val CODEC: Codec<DispersalModifier> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.optionalFieldOf("range", 1).forGetter { it.rangeModifier },
                    Codec.INT.optionalFieldOf("lingering_duration_modifier", 1)
                        .forGetter { it.lingeringDurationModifier },
                ).apply(instance, ::DispersalModifier)
            }
        }
    }

    companion object {
        val CODEC: Codec<WitcheryPotionIngredient> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter { it.item },
                MobEffect.CODEC.optionalFieldOf("effect", WitcheryMobEffects.EMPTY).forGetter { it.effect },
                ResourceLocation.CODEC.optionalFieldOf("special_effect").forGetter { it.specialEffect },
                Codec.INT.optionalFieldOf("base_duration", 0).forGetter { it.baseDuration },
                Codec.INT.optionalFieldOf("altar_power", 0).forGetter { it.altarPower },
                Codec.INT.optionalFieldOf("capacity_cost", 1).forGetter { it.capacityCost },
                Codec.list(GeneralModifier.CODEC).optionalFieldOf("general_modifier", emptyList())
                    .forGetter { it.generalModifier },
                EffectModifier.CODEC.optionalFieldOf("effect_modifier", EffectModifier(0, 0, 1))
                    .forGetter { it.effectModifier },
                DispersalModifier.CODEC.optionalFieldOf("dispersal_modifier", DispersalModifier(1, 1))
                    .forGetter { it.dispersalModifier },
                Type.CODEC.optionalFieldOf("type", Type.CONSUMABLE).forGetter { it.type },
                Codec.INT.optionalFieldOf("color", 0x000000).forGetter { it.color },
            ).apply(instance, ::WitcheryPotionIngredient)
        }
    }
}