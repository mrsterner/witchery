package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.PoppetType
import dev.sterner.witchery.poppet.ArmorProtectionPoppet
import dev.sterner.witchery.poppet.DeathProtectionPoppet
import dev.sterner.witchery.poppet.HungerProtectionPoppet
import dev.sterner.witchery.poppet.VampiricPoppet
import dev.sterner.witchery.poppet.VoodooPoppet
import dev.sterner.witchery.poppet.VoodooProtectionPoppet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier
import kotlin.collections.get

object WitcheryPoppetRegistry {

    val ID = Witchery.id("poppet")

    val POPPET_REGISTRY_KEY: ResourceKey<Registry<PoppetType>> = ResourceKey.createRegistryKey(ID)

    val POPPETS: DeferredRegister<PoppetType> = DeferredRegister.create(POPPET_REGISTRY_KEY, Witchery.MODID)

    val DEATH_PROTECTION: DeferredHolder<PoppetType, DeathProtectionPoppet> =
        POPPETS.register("death_protection", Supplier { DeathProtectionPoppet() })

    val VAMPIRIC: DeferredHolder<PoppetType, VampiricPoppet> =
        POPPETS.register("vampiric", Supplier {  VampiricPoppet() })

    val VOODOO: DeferredHolder<PoppetType, VoodooPoppet> =
        POPPETS.register("voodoo", Supplier {  VoodooPoppet() })

    val HUNGER_PROTECTION: DeferredHolder<PoppetType, HungerProtectionPoppet> =
        POPPETS.register("hunger_protection", Supplier {  HungerProtectionPoppet() })

    val ARMOR_PROTECTION: DeferredHolder<PoppetType, ArmorProtectionPoppet> =
        POPPETS.register("armor_protection", Supplier {  ArmorProtectionPoppet() })

    val VOODOO_PROTECTION: DeferredHolder<PoppetType, VoodooProtectionPoppet> =
        POPPETS.register("voodoo_protection", Supplier {  VoodooProtectionPoppet() })

    @JvmStatic
    fun getType(item: Item): PoppetType? {
        return POPPETS.entries.find { it.get().item == item }?.get()
    }

    @JvmStatic
    fun getType(id: ResourceLocation): PoppetType? {
        return POPPETS.entries.find { it.id == id }?.get()
    }

    fun getAllTypes(): List<PoppetType> = POPPETS.entries.map { it.get() }

    fun getIdForPoppet(poppetType: PoppetType): ResourceLocation? =
        POPPETS.entries.firstOrNull { it.get() == poppetType }?.id
}
