package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.ritual.EmptyRitual
import dev.sterner.witchery.ritual.PushMobsRitual
import net.minecraft.core.HolderGetter
import net.minecraft.core.Registry
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation


object WitcheryRitualRegistry {

    val RITUAL_KEY: ResourceKey<Registry<Ritual>> = ResourceKey.createRegistryKey(Witchery.id("ritual"))

    val EMPTY_KEY = ResourceKey.create(RITUAL_KEY, Witchery.id("empty"))
    val PUSH_MOBS_KEY = ResourceKey.create(RITUAL_KEY, Witchery.id("push_mobs"))

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
}