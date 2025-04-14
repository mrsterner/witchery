package dev.sterner.witchery.item.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.item.ItemStack
import java.awt.Color
import java.util.Optional

data class WitcheryPotionIngredient(
    val item: ItemStack,
    val effect: WitcheryPotionEffect,
    val altarPower: Int,
    val capacityCost: Int = 1,
    val generalModifier: Optional<GeneralModifier> = Optional.empty(),
    val effectModifier: Optional<EffectModifier> = Optional.empty(),
    val dispersalModifier: Optional<DispersalModifier> = Optional.empty(),
    val type: Type = Type.DRINK,
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
        DRINK,
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
        val powerModifier: Int = 1,
        val durationModifier: Int = 1
    ) {
        companion object {
            val CODEC: Codec<EffectModifier> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.optionalFieldOf("power", 1).forGetter { it.powerModifier },
                    Codec.INT.optionalFieldOf("duration", 1).forGetter { it.durationModifier }
                ).apply(instance, ::EffectModifier)
            }
        }
    }

    data class DispersalModifier(
        val rangeModifier: Int = 1,
        val durationModifier: Int = 1
    ) {
        companion object {
            val CODEC: Codec<DispersalModifier> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.optionalFieldOf("range", 1).forGetter { it.rangeModifier },
                    Codec.INT.optionalFieldOf("duration", 1).forGetter { it.durationModifier }
                ).apply(instance, ::DispersalModifier)
            }
        }
    }

    companion object {
        val CODEC: Codec<WitcheryPotionIngredient> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter { it.item },
                WitcheryPotionEffect.CODEC.fieldOf("effect").forGetter { it.effect },
                Codec.INT.fieldOf("altar_power").forGetter { it.altarPower },
                Codec.INT.optionalFieldOf("capacity_cost", 1).forGetter { it.capacityCost },
                GeneralModifier.CODEC.optionalFieldOf("general_modifier").forGetter { it.generalModifier },
                EffectModifier.CODEC.optionalFieldOf("effect_modifier").forGetter { it.effectModifier },
                DispersalModifier.CODEC.optionalFieldOf("dispersal_modifier").forGetter { it.dispersalModifier },
                Type.CODEC.fieldOf("type").forGetter{ it.type },
                Codec.INT.fieldOf("color").forGetter { it.color },
            ).apply(instance, ::WitcheryPotionIngredient)
        }
    }
}