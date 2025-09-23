package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.PoppetType
import dev.sterner.witchery.poppet.*
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegistryBuilder
import java.util.function.Supplier

object WitcheryPoppetRegistry {

    val ID = Witchery.id("poppet")

    val POPPET_REGISTRY_KEY: ResourceKey<Registry<PoppetType>> = ResourceKey.createRegistryKey(ID)

    val POPPET_REGISTRY: Registry<PoppetType> =
        RegistryBuilder(POPPET_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ID)
            .maxId(256)
            .create()

    private val POPPETS: DeferredRegister<PoppetType> = DeferredRegister.create(POPPET_REGISTRY, Witchery.MODID)

    val DEATH_PROTECTION: DeferredHolder<PoppetType, DeathProtectionPoppet> =
        POPPETS.register("death_protection", Supplier { DeathProtectionPoppet() })

    val VAMPIRIC: DeferredHolder<PoppetType, VampiricPoppet> =
        POPPETS.register("vampiric", Supplier { VampiricPoppet() })

    val VOODOO: DeferredHolder<PoppetType, VoodooPoppet> =
        POPPETS.register("voodoo", Supplier { VoodooPoppet() })

    val HUNGER_PROTECTION: DeferredHolder<PoppetType, HungerProtectionPoppet> =
        POPPETS.register("hunger_protection", Supplier { HungerProtectionPoppet() })

    val ARMOR_PROTECTION: DeferredHolder<PoppetType, ArmorProtectionPoppet> =
        POPPETS.register("armor_protection", Supplier { ArmorProtectionPoppet() })

    val VOODOO_PROTECTION: DeferredHolder<PoppetType, VoodooProtectionPoppet> =
        POPPETS.register("voodoo_protection", Supplier { VoodooProtectionPoppet() })

    @JvmStatic
    fun getType(item: Item): PoppetType? {
        return POPPET_REGISTRY.firstOrNull { it.item == item }
    }

    @JvmStatic
    fun getType(id: ResourceLocation): PoppetType? {
        return POPPET_REGISTRY.firstOrNull { it.getRegistryId() == id }
    }

    fun register(modEventBus: IEventBus) {
        POPPETS.register(modEventBus)
    }
}
