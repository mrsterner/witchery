package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mobeffect.*
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import java.awt.Color

object WitcheryMobEffects {

    val POISON_WEAPON: Holder<MobEffect> = register("poison_weapon", PoisonWeaponMobEffect(MobEffectCategory.BENEFICIAL, Color(70, 255, 110).rgb))

    val BANE_OF_ARTHROPODS_WEAPON: Holder<MobEffect> = register("bane_of_arthropods_weapon", BaneOfArthropodsWeaponMobEffect(MobEffectCategory.HARMFUL, Color(100, 70, 20).rgb))

    val REFLECT_ARROW: Holder<MobEffect> = register("reflect_arrow", ReflectArrowsMobEffect(MobEffectCategory.BENEFICIAL, Color(220, 255, 110).rgb))

    val DISEASE: Holder<MobEffect> = register("disease", DiseaseMobEffect(MobEffectCategory.HARMFUL, Color(220, 100, 110).rgb))

    val FORTUNE_TOOL: Holder<MobEffect> = register("fortune_tool", FortuneToolMobEffect(MobEffectCategory.BENEFICIAL, Color(100, 170, 210).rgb))

    private fun register(name: String, effect: MobEffect): Holder<MobEffect> {
        return Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            Witchery.id(name),
            effect
        )
    }

    fun init(){

    }
}