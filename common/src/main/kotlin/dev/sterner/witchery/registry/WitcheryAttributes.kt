package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute

object WitcheryAttributes {

    val ATTRIBUTES = DeferredRegister.create(Witchery.MODID, Registries.ATTRIBUTE)

}