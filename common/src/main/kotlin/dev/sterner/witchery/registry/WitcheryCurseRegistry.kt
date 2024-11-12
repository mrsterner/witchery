package dev.sterner.witchery.registry

import com.google.common.base.Suppliers
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.curse.EmptyCurse
import dev.sterner.witchery.ritual.BindFamiliarRitual
import dev.sterner.witchery.ritual.EmptyRitual
import dev.sterner.witchery.ritual.PushMobsRitual
import dev.sterner.witchery.ritual.ResurrectFamiliarRitual
import io.wispforest.accessories.Accessories.MODID
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier


object WitcheryCurseRegistry {

    val ID = Witchery.id("curse")

    val CURSES: Registrar<Curse> = RegistrarManager.get(Witchery.MODID).builder<Curse>(ID)
        .syncToClients().build()

    val EMPTY: RegistrySupplier<EmptyCurse> = CURSES.register(Witchery.id("empty")) {
        EmptyCurse()
    }

    val CODEC: Codec<Curse?> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Curse> ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { curse -> curse.id }
        ).apply(instance) { resourceLocation ->
            Curse(resourceLocation)
        }
    }

    fun init(){

    }
}