package dev.sterner.witchery.core.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.BrazierPassive
import dev.sterner.witchery.features.brazier.AnguishOfTheDeadBrazierPassive
import dev.sterner.witchery.features.brazier.DeathlyVeilBrazierPassive
import dev.sterner.witchery.features.brazier.DrainGrowthBrazierPassive
import dev.sterner.witchery.features.brazier.EmptyBrazierPassive
import dev.sterner.witchery.features.brazier.FortificationOfTheCorpseBrazierPassive
import dev.sterner.witchery.features.brazier.GraveyardMistBrazierPassive
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier


object WitcheryBrazierRegistry {

    val ID = Witchery.id("brazier_passive")

    val BRAZIER_REGISTRY_KEY: ResourceKey<Registry<BrazierPassive>> = ResourceKey.createRegistryKey(ID)

    val BRAZIER_REGISTRY: Registry<BrazierPassive> =
        RegistryBuilder(BRAZIER_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    private val BRAZIERS: DeferredRegister<BrazierPassive> = DeferredRegister.create(BRAZIER_REGISTRY, Witchery.MODID)


    val EMPTY: DeferredHolder<BrazierPassive, EmptyBrazierPassive> = BRAZIERS.register("empty", Supplier { EmptyBrazierPassive() })
    val GRAVEYARD_MIST: DeferredHolder<BrazierPassive, GraveyardMistBrazierPassive> = BRAZIERS.register("graveyard_mist", Supplier { GraveyardMistBrazierPassive() })
    val ANGUISH_OF_THE_DEAD: DeferredHolder<BrazierPassive, AnguishOfTheDeadBrazierPassive> = BRAZIERS.register("anguish_of_the_dead", Supplier { AnguishOfTheDeadBrazierPassive() })
    val DEATHLY_VEIL: DeferredHolder<BrazierPassive, DeathlyVeilBrazierPassive> = BRAZIERS.register("deathly_veil", Supplier { DeathlyVeilBrazierPassive() })
    val DRAIN_GROWTH: DeferredHolder<BrazierPassive, DrainGrowthBrazierPassive> = BRAZIERS.register("drain_growth", Supplier { DrainGrowthBrazierPassive() })
    val FORTIFICATION_OF_THE_CORPSE: DeferredHolder<BrazierPassive, FortificationOfTheCorpseBrazierPassive> = BRAZIERS.register("fortification_of_the_corpse", Supplier { FortificationOfTheCorpseBrazierPassive() })


    fun getById(id: ResourceLocation): BrazierPassive? {
        val holder = BRAZIERS.entries.firstOrNull { it.id == id }
        return holder?.get()
    }

    val CODEC: Codec<BrazierPassive> = RecordCodecBuilder.create { instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { it.id }
        ).apply(instance) { resourceLocation ->
            getById(resourceLocation) ?: EmptyBrazierPassive()
        }
    }

    fun register(modEventBus: IEventBus) {
        BRAZIERS.register(modEventBus)
    }
}
