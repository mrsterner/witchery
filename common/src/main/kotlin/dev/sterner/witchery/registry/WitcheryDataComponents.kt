package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.GlobalPos
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries

object WitcheryDataComponents  {
    val DATA = DeferredRegister.create(Witchery.MODID, Registries.DATA_COMPONENT_TYPE)

    val GLOBAL_POS_COMPONENT = DATA.register("global_pos") {
        DataComponentType.builder<GlobalPos>().persistent(GlobalPos.CODEC).build()
    }

    val ENTITY_ID_COMPONENT = DATA.register("entity_id") {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    }
}