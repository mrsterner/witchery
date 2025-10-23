package dev.sterner.witchery.core.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.mob_effect.AttractArrowsMobEffect
import dev.sterner.witchery.content.mob_effect.BaneOfArthropodsWeaponMobEffect
import dev.sterner.witchery.content.mob_effect.DiseaseMobEffect
import dev.sterner.witchery.content.mob_effect.EmptyMobEffect
import dev.sterner.witchery.content.mob_effect.FortuneToolMobEffect
import dev.sterner.witchery.content.mob_effect.PoisonWeaponMobEffect
import dev.sterner.witchery.content.mob_effect.ReflectArrowsMobEffect
import dev.sterner.witchery.content.mob_effect.ResizeMobEffect
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.awt.Color
import java.util.function.Supplier

object WitcheryMobEffects {

    val EFFECTS: DeferredRegister<MobEffect> =
        DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Witchery.MODID)

    val EMPTY: DeferredHolder<MobEffect, MobEffect> =
        EFFECTS.register("empty", Supplier {
            EmptyMobEffect(MobEffectCategory.NEUTRAL, Color(255, 255, 255).rgb)
        })

    val POISON_WEAPON: DeferredHolder<MobEffect, MobEffect> =
        EFFECTS.register("poison_weapon", Supplier {
            PoisonWeaponMobEffect(MobEffectCategory.BENEFICIAL, Color(70, 255, 110).rgb)
        })

    val BANE_OF_ARTHROPODS_WEAPON: DeferredHolder<MobEffect, MobEffect> =
        register("bane_of_arthropods_weapon") {
            BaneOfArthropodsWeaponMobEffect(MobEffectCategory.HARMFUL, Color(180, 10, 20).rgb)
        }

    val REFLECT_ARROW: DeferredHolder<MobEffect, MobEffect> =
        register("reflect_arrow") {
            ReflectArrowsMobEffect(MobEffectCategory.BENEFICIAL, Color(220, 255, 110).rgb)
        }

    val ATTRACT_ARROW: DeferredHolder<MobEffect, MobEffect> =
        register("attract_arrow") {
            AttractArrowsMobEffect(MobEffectCategory.BENEFICIAL, Color(220, 255, 110).rgb)
        }

    val DISEASE: DeferredHolder<MobEffect, MobEffect> =
        register("disease") {
            DiseaseMobEffect(MobEffectCategory.HARMFUL, Color(220, 100, 110).rgb)
        }

    val FORTUNE_TOOL: DeferredHolder<MobEffect, MobEffect> =
        register("fortune_tool") {
            FortuneToolMobEffect(MobEffectCategory.BENEFICIAL, Color(100, 170, 210).rgb)
        }

    val ENDER_BOUND: DeferredHolder<MobEffect, MobEffect> =
        register("ender_bound") {
            EmptyMobEffect(MobEffectCategory.HARMFUL, Color(255, 60, 210).rgb)
        }

    val WEREWOLF_BOUND: DeferredHolder<MobEffect, MobEffect> =
        register("werewolf_bound") {
            EmptyMobEffect(MobEffectCategory.HARMFUL, Color(255, 255, 100).rgb)
        }

    val GROW: DeferredHolder<MobEffect, MobEffect> =
        register("grow") {
            ResizeMobEffect(true, MobEffectCategory.NEUTRAL, Color(255, 255, 100).rgb)
        }

    val SHRINK: DeferredHolder<MobEffect, MobEffect> =
        register("shrink") {
            ResizeMobEffect(false, MobEffectCategory.NEUTRAL, Color(255, 255, 100).rgb)
        }

    val GROTESQUE: DeferredHolder<MobEffect, MobEffect> =
        register("grotesque") {
            EmptyMobEffect(MobEffectCategory.NEUTRAL, Color(255, 100, 100).rgb)
        }

    private fun register(name: String, effectSupplier: () -> MobEffect): DeferredHolder<MobEffect, MobEffect> {
        return EFFECTS.register(name, Supplier(effectSupplier))
    }

    fun invertEffect(effect: Holder<MobEffect>): Holder<MobEffect> {
        return when (effect) {
            MobEffects.MOVEMENT_SPEED -> MobEffects.MOVEMENT_SLOWDOWN
            MobEffects.MOVEMENT_SLOWDOWN -> MobEffects.MOVEMENT_SPEED
            MobEffects.DIG_SPEED -> MobEffects.DIG_SLOWDOWN
            MobEffects.DIG_SLOWDOWN -> MobEffects.DIG_SPEED
            MobEffects.DAMAGE_BOOST -> MobEffects.WEAKNESS
            MobEffects.WEAKNESS -> MobEffects.DAMAGE_BOOST
            MobEffects.REGENERATION -> MobEffects.POISON
            MobEffects.POISON -> MobEffects.REGENERATION
            MobEffects.HEAL -> MobEffects.HARM
            MobEffects.HARM -> MobEffects.HEAL
            MobEffects.NIGHT_VISION -> MobEffects.BLINDNESS
            MobEffects.BLINDNESS -> MobEffects.NIGHT_VISION
            MobEffects.SATURATION -> MobEffects.HUNGER
            MobEffects.HUNGER -> MobEffects.SATURATION
            MobEffects.LUCK -> MobEffects.UNLUCK
            MobEffects.UNLUCK -> MobEffects.LUCK
            MobEffects.SLOW_FALLING -> MobEffects.LEVITATION
            MobEffects.LEVITATION -> MobEffects.SLOW_FALLING
            GROW -> SHRINK
            REFLECT_ARROW -> ATTRACT_ARROW
            SHRINK -> GROW
            ATTRACT_ARROW -> REFLECT_ARROW
            MobEffects.BAD_OMEN -> MobEffects.HERO_OF_THE_VILLAGE
            MobEffects.HERO_OF_THE_VILLAGE -> MobEffects.BAD_OMEN
            else -> effect
        }
    }
}