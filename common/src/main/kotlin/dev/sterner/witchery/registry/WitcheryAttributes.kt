package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute

object WitcheryAttributes {

    val ATTRIBUTES = DeferredRegister.create(Witchery.MODID, Registries.ATTRIBUTE)

    val INFERNAL_SLIME_JUMP_BOOST = ATTRIBUTES.register("slime_jump_boost") {
        RangedAttribute("attribute.name.witchery.slime_jump_boost", 0.42, 0.0, 32.0).setSyncable(true)
    }
}