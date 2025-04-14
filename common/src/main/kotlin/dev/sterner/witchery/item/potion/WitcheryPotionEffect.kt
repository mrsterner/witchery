package dev.sterner.witchery.item.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

open class WitcheryPotionEffect(
    val effectId: ResourceLocation,
    val duration: Int,
    val amplifier: Int
) {

    constructor(
        effectId: String,
        duration: Int,
        amplifier: Int
        ): this(Witchery.id(effectId), duration, amplifier)

    open fun affectEntity(livingEntity: LivingEntity, activeIngredient: WitcheryPotionIngredient) {}

    companion object {
        val CODEC: Codec<WitcheryPotionEffect> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("effect").forGetter { it.effectId },
                Codec.INT.fieldOf("duration").forGetter { it.duration },
                Codec.INT.fieldOf("amplifier").forGetter { it.amplifier },
            ).apply(instance) { id, dur, amp ->
                WitcheryPotionEffect(id, dur, amp)
            }
        }
    }
}

