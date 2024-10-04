package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent

object WitcherySounds {

    val SOUNDS: DeferredRegister<SoundEvent> = DeferredRegister.create(Witchery.MODID, Registries.SOUND_EVENT)


}