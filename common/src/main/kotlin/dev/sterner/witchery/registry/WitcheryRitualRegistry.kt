package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import dev.sterner.witchery.ritual.PushMobsRitual


object WitcheryRitualRegistry {
    val RITUALS = DeferredRegister.create(MODID, WitcheryRegistries.RITUAL)

    val PUSH_MOBS_RITUAL = RITUALS.register(Witchery.id("empty")) { PushMobsRitual() }
}