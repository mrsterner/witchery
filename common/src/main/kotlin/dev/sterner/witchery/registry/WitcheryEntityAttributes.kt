package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.RangedAttribute

object WitcheryEntityAttributes {

    val ATTRIBUTES = DeferredRegister.create(Witchery.MODID, Registries.ATTRIBUTE)

    val VAMPIRE_KNOCKBACK = ATTRIBUTES.register("vampire_knockback") {
        RangedAttribute("attribute.name.witcery.vampire_knockback", 0.0,0.0,1.0).setSyncable(true)
    }
}