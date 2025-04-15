package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import dev.sterner.witchery.mobeffect.*
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.block.Block
import java.awt.Color

object WitcheryMobEffects {

    val EFFECTS: DeferredRegister<MobEffect> = DeferredRegister.create(MODID, Registries.MOB_EFFECT)

    val EMPTY: Holder<MobEffect> = register("empty", EmptyMobEffect(MobEffectCategory.NEUTRAL, Color(255, 255, 255).rgb))

    val POISON_WEAPON: Holder<MobEffect> = register("poison_weapon", PoisonWeaponMobEffect(MobEffectCategory.BENEFICIAL, Color(70, 255, 110).rgb))

    val BANE_OF_ARTHROPODS_WEAPON: Holder<MobEffect> = register("bane_of_arthropods_weapon", BaneOfArthropodsWeaponMobEffect(MobEffectCategory.HARMFUL, Color(100, 70, 20).rgb))

    val REFLECT_ARROW: Holder<MobEffect> = register("reflect_arrow", ReflectArrowsMobEffect(MobEffectCategory.BENEFICIAL, Color(220, 255, 110).rgb))

    val DISEASE: Holder<MobEffect> = register("disease", DiseaseMobEffect(MobEffectCategory.HARMFUL, Color(220, 100, 110).rgb))

    val FORTUNE_TOOL: Holder<MobEffect> = register("fortune_tool", FortuneToolMobEffect(MobEffectCategory.BENEFICIAL, Color(100, 170, 210).rgb))

    fun invertEffect(effect: Holder<MobEffect>): Holder<MobEffect> {
        return when (effect) {
            MobEffects.MOVEMENT_SPEED     -> MobEffects.MOVEMENT_SLOWDOWN
            MobEffects.MOVEMENT_SLOWDOWN  -> MobEffects.MOVEMENT_SPEED
            MobEffects.DIG_SPEED          -> MobEffects.DIG_SLOWDOWN
            MobEffects.DIG_SLOWDOWN       -> MobEffects.DIG_SPEED
            MobEffects.DAMAGE_BOOST       -> MobEffects.WEAKNESS
            MobEffects.WEAKNESS           -> MobEffects.DAMAGE_BOOST
            MobEffects.REGENERATION       -> MobEffects.POISON
            MobEffects.POISON             -> MobEffects.REGENERATION
            MobEffects.HEAL               -> MobEffects.HARM
            MobEffects.HARM               -> MobEffects.HEAL
            MobEffects.NIGHT_VISION       -> MobEffects.BLINDNESS
            MobEffects.BLINDNESS          -> MobEffects.NIGHT_VISION
            MobEffects.SATURATION         -> MobEffects.HUNGER
            MobEffects.HUNGER             -> MobEffects.SATURATION
            MobEffects.LUCK               -> MobEffects.UNLUCK
            MobEffects.UNLUCK             -> MobEffects.LUCK
            MobEffects.JUMP               -> MobEffects.LEVITATION
            MobEffects.LEVITATION         -> MobEffects.JUMP
            else -> effect
        }
    }

    private fun register(name: String, effect: MobEffect): Holder<MobEffect> {
        return EFFECTS.register(name) {effect}
    }
}