package dev.sterner.witchery.core.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.features.ritual.BestialCallRitual
import dev.sterner.witchery.features.ritual.BindFamiliarRitual
import dev.sterner.witchery.features.ritual.BindSpectralCreaturesRitual
import dev.sterner.witchery.features.ritual.BindingRitual
import dev.sterner.witchery.features.ritual.BlocksBelowRitual
import dev.sterner.witchery.features.ritual.EmptyRitual
import dev.sterner.witchery.features.ritual.PullMobsRitual
import dev.sterner.witchery.features.ritual.PushMobsRitual
import dev.sterner.witchery.features.ritual.RainingToadRitual
import dev.sterner.witchery.features.ritual.RemoveCurseRitual
import dev.sterner.witchery.features.ritual.ResurrectFamiliarRitual
import dev.sterner.witchery.features.ritual.RotRitual
import dev.sterner.witchery.features.ritual.SoulSeveranceRitual
import dev.sterner.witchery.features.ritual.SoulbindRitual
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier


object WitcheryRitualRegistry {

    val ID = Witchery.id("ritual")

    val RITUAL_REGISTRY_KEY: ResourceKey<Registry<Ritual>> = ResourceKey.createRegistryKey(ID)

    val RITUAL_REGISTRY: Registry<Ritual> =
        RegistryBuilder(RITUAL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    private val RITUALS: DeferredRegister<Ritual> = DeferredRegister.create(RITUAL_REGISTRY, Witchery.MODID)


    val EMPTY: DeferredHolder<Ritual, EmptyRitual> = RITUALS.register("empty", Supplier { EmptyRitual() })
    val PUSH_MOBS: DeferredHolder<Ritual, PushMobsRitual> = RITUALS.register("push_mobs", Supplier { PushMobsRitual() })
    val REMOVE_CURSE: DeferredHolder<Ritual, RemoveCurseRitual> =
        RITUALS.register("remove_curse", Supplier { RemoveCurseRitual() })
    val BIND_FAMILIAR: DeferredHolder<Ritual, BindFamiliarRitual> =
        RITUALS.register("bind_familiar", Supplier { BindFamiliarRitual() })
    val BINDING: DeferredHolder<Ritual, BindingRitual> =
        RITUALS.register("binding", Supplier { BindingRitual() })
    val SOULBIND: DeferredHolder<Ritual, SoulbindRitual> =
        RITUALS.register("soulbind", Supplier { SoulbindRitual() })
    val RESURRECT_FAMILIAR: DeferredHolder<Ritual, ResurrectFamiliarRitual> =
        RITUALS.register("resurrect_familiar", Supplier { ResurrectFamiliarRitual() })
    val BIND_SPECTRAL_CREATURES: DeferredHolder<Ritual, BindSpectralCreaturesRitual> =
        RITUALS.register("bind_spectral_creatures", Supplier { BindSpectralCreaturesRitual() })
    val ROT: DeferredHolder<Ritual, RotRitual> = RITUALS.register("rot", Supplier { RotRitual() })
    val BLOCKS_BELOW: DeferredHolder<Ritual, BlocksBelowRitual> = RITUALS.register("blocks_below", Supplier { BlocksBelowRitual() })
    val BESTIAL_CALL: DeferredHolder<Ritual, BestialCallRitual> = RITUALS.register("bestial_call", Supplier { BestialCallRitual() })
    val PULL_MOBS: DeferredHolder<Ritual, PullMobsRitual> = RITUALS.register("pull_mobs", Supplier { PullMobsRitual() })
    val RAINING_TOAD: DeferredHolder<Ritual, RainingToadRitual> = RITUALS.register("raining_toad", Supplier { RainingToadRitual() })
    val SOUL_SEVERANCE: DeferredHolder<Ritual, SoulSeveranceRitual> =
        RITUALS.register("soul_severance", Supplier { SoulSeveranceRitual() })

    fun getById(id: ResourceLocation): Ritual? {
        val holder = RITUALS.entries.firstOrNull { it.id == id }
        return holder?.get()
    }

    val CODEC: Codec<Ritual> = RecordCodecBuilder.create { instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { it.id }
        ).apply(instance) { resourceLocation ->
            getById(resourceLocation) ?: EmptyRitual()
        }
    }

    fun register(modEventBus: IEventBus) {
        RITUALS.register(modEventBus)
    }
}
