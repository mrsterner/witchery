package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredRegister

object WitcherySounds {

    val SOUNDS: DeferredRegister<SoundEvent> = DeferredRegister.create(Registries.SOUND_EVENT, Witchery.MODID)

}