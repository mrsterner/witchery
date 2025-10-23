package dev.sterner.witchery.core.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.tarot.TarotEffect
import dev.sterner.witchery.features.tarot.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier

object WitcheryTarotEffects {

    val ID = Witchery.id("tarot_effect")

    val TAROT_REGISTRY_KEY: ResourceKey<Registry<TarotEffect>> = ResourceKey.createRegistryKey(ID)

    val TAROT_REGISTRY: Registry<TarotEffect> =
        RegistryBuilder(TAROT_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    private val TAROT_EFFECTS: DeferredRegister<TarotEffect> = DeferredRegister.create(TAROT_REGISTRY, Witchery.MODID)

    val THE_FOOL: DeferredHolder<TarotEffect, TheFoolEffect> =
        TAROT_EFFECTS.register("the_fool", Supplier { TheFoolEffect() })
    val THE_MAGICIAN: DeferredHolder<TarotEffect, TheMagicianEffect> =
        TAROT_EFFECTS.register("the_magician", Supplier { TheMagicianEffect() })
    val THE_HIGH_PRIESTESS: DeferredHolder<TarotEffect, TheHighPriestessEffect> =
        TAROT_EFFECTS.register("the_high_priestess", Supplier { TheHighPriestessEffect() })
    val THE_EMPRESS: DeferredHolder<TarotEffect, TheEmpressEffect> =
        TAROT_EFFECTS.register("the_empress", Supplier { TheEmpressEffect() })
    val THE_EMPEROR: DeferredHolder<TarotEffect, TheEmperorEffect> =
        TAROT_EFFECTS.register("the_emperor", Supplier { TheEmperorEffect() })
    val THE_HIEROPHANT: DeferredHolder<TarotEffect, TheHierophantEffect> =
        TAROT_EFFECTS.register("the_hierophant", Supplier { TheHierophantEffect() })
    val THE_LOVERS: DeferredHolder<TarotEffect, TheLoversEffect> =
        TAROT_EFFECTS.register("the_lovers", Supplier { TheLoversEffect() })
    val THE_CHARIOT: DeferredHolder<TarotEffect, TheChariotEffect> =
        TAROT_EFFECTS.register("the_chariot", Supplier { TheChariotEffect() })
    val STRENGTH: DeferredHolder<TarotEffect, StrengthEffect> =
        TAROT_EFFECTS.register("strength", Supplier { StrengthEffect() })
    val THE_HERMIT: DeferredHolder<TarotEffect, TheHermitEffect> =
        TAROT_EFFECTS.register("the_hermit", Supplier { TheHermitEffect() })
    val WHEEL_OF_FORTUNE: DeferredHolder<TarotEffect, WheelOfFortuneEffect> =
        TAROT_EFFECTS.register("wheel_of_fortune", Supplier { WheelOfFortuneEffect() })
    val JUSTICE: DeferredHolder<TarotEffect, JusticeEffect> =
        TAROT_EFFECTS.register("justice", Supplier { JusticeEffect() })
    val THE_HANGED_MAN: DeferredHolder<TarotEffect, TheHangedManEffect> =
        TAROT_EFFECTS.register("the_hanged_man", Supplier { TheHangedManEffect() })
    val DEATH: DeferredHolder<TarotEffect, DeathEffect> =
        TAROT_EFFECTS.register("death", Supplier { DeathEffect() })
    val TEMPERANCE: DeferredHolder<TarotEffect, TemperanceEffect> =
        TAROT_EFFECTS.register("temperance", Supplier { TemperanceEffect() })
    val THE_DEVIL: DeferredHolder<TarotEffect, TheDevilEffect> =
        TAROT_EFFECTS.register("the_devil", Supplier { TheDevilEffect() })
    val THE_TOWER: DeferredHolder<TarotEffect, TheTowerEffect> =
        TAROT_EFFECTS.register("the_tower", Supplier { TheTowerEffect() })
    val THE_STAR: DeferredHolder<TarotEffect, TheStarEffect> =
        TAROT_EFFECTS.register("the_star", Supplier { TheStarEffect() })
    val THE_MOON: DeferredHolder<TarotEffect, TheMoonEffect> =
        TAROT_EFFECTS.register("the_moon", Supplier { TheMoonEffect() })
    val THE_SUN: DeferredHolder<TarotEffect, TheSunEffect> =
        TAROT_EFFECTS.register("the_sun", Supplier { TheSunEffect() })
    val JUDGEMENT: DeferredHolder<TarotEffect, JudgementEffect> =
        TAROT_EFFECTS.register("judgement", Supplier { JudgementEffect() })
    val THE_WORLD: DeferredHolder<TarotEffect, TheWorldEffect> =
        TAROT_EFFECTS.register("the_world", Supplier { TheWorldEffect() })

    fun getById(id: ResourceLocation): TarotEffect? {
        val holder = TAROT_EFFECTS.entries.firstOrNull { it.id == id }
        return holder?.get()
    }

    fun getByCardNumber(cardNumber: Int): TarotEffect? {
        return TAROT_EFFECTS.entries.firstOrNull { it.get().cardNumber == cardNumber }?.get()
    }

    fun getAllEffects(): Collection<TarotEffect> {
        return TAROT_EFFECTS.entries.map { it.get() }
    }

    val CODEC: Codec<TarotEffect> = RecordCodecBuilder.create { instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { effect ->
                TAROT_EFFECTS.entries.firstOrNull { it.get() == effect }?.id ?: ID
            }
        ).apply(instance) { resourceLocation ->
            getById(resourceLocation) ?: TheFoolEffect()
        }
    }

    fun register(modEventBus: IEventBus) {
        TAROT_EFFECTS.register(modEventBus)
    }
}