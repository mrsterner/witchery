package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import java.util.*

object WitcheryDataComponents {

    val DATA: DeferredRegister<DataComponentType<*>> = DeferredRegister.create(Witchery.MODID, Registries.DATA_COMPONENT_TYPE)

    val GLOBAL_POS_COMPONENT: RegistrySupplier<DataComponentType<GlobalPos>> = DATA.register("global_pos") {
        DataComponentType.builder<GlobalPos>().persistent(GlobalPos.CODEC).build()
    }

    val ENTITY_ID_COMPONENT: RegistrySupplier<DataComponentType<String>> = DATA.register("entity_uuid") {
        DataComponentType.builder<String>().persistent(Codec.STRING).build()
    }

    val ENTITY_NAME_COMPONENT: RegistrySupplier<DataComponentType<String>> = DATA.register("entity_name") {
        DataComponentType.builder<String>().persistent(Codec.STRING).build()
    }

    val EXPIRED_TAGLOCK: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("expired_taglock") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val TIMESTAMP: RegistrySupplier<DataComponentType<Long>> = DATA.register("timestamp") {
        DataComponentType.builder<Long>().persistent(Codec.LONG).build()
    }

    val ATTUNED: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("attuned") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val HAS_SOUP: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("has_soup") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val HAS_OINTMENT: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("has_ointment") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val PLAYER_UUID: RegistrySupplier<DataComponentType<UUID>> = DATA.register("player_uuid") {
        DataComponentType.builder<UUID>().persistent(UUIDUtil.CODEC).build()
    }

    val BLOOD: RegistrySupplier<DataComponentType<UUID>> = DATA.register("blood") {
        DataComponentType.builder<UUID>().persistent(UUIDUtil.CODEC).build()
    }

    val VAMPIRE_BLOOD: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("vampire_blood") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val CHICKEN_BLOOD: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("chicken_blood") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }

    val HAS_SUN: RegistrySupplier<DataComponentType<Boolean>> = DATA.register("has_sun") {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    }
}