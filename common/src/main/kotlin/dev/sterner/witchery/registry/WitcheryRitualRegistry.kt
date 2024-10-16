package dev.sterner.witchery.registry

import com.google.common.base.Suppliers
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.ritual.EmptyRitual
import dev.sterner.witchery.ritual.PushMobsRitual
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier


object WitcheryRitualRegistry {

    val RITUAL_KEY: ResourceKey<Registry<Ritual>> = ResourceKey.createRegistryKey(Witchery.id("ritual"))

    val EMPTY_KEY: ResourceKey<Ritual> = ResourceKey.create(RITUAL_KEY, Witchery.id("empty"))
    val PUSH_MOBS_KEY: ResourceKey<Ritual> = ResourceKey.create(RITUAL_KEY, Witchery.id("push_mobs"))

    val CODEC: Codec<Ritual?> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Ritual> ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { ritual -> ritual.id }
        ).apply(instance) { resourceLocation ->
            Ritual(resourceLocation)
        }
    }

    fun bootstrap(it: BootstrapContext<Ritual>) {
        it.register(EMPTY_KEY, EmptyRitual())
        it.register(PUSH_MOBS_KEY, PushMobsRitual())
    }


    //Registry is throwing hands, this poor excuse of a registry have to do for now
    fun getSadImplementation(tag: CompoundTag): Ritual {
        var ritual: Ritual = EmptyRitual()
        if (tag.getString("ritualType") == "witchery:push_mobs") {
            ritual = PushMobsRitual()
        }

        return ritual
    }
}