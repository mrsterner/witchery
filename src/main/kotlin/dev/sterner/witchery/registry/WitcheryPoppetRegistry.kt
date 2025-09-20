package dev.sterner.witchery.registry

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.poppet.PoppetType
import dev.sterner.witchery.poppet.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

object WitcheryPoppetRegistry {

    val ID = Witchery.id("poppet")

    val POPPETS: Registrar<PoppetType> = RegistrarManager.get(Witchery.MODID).builder<PoppetType>(ID)
        .syncToClients().build()

    val DEATH_PROTECTION: RegistrySupplier<DeathProtectionPoppet> = POPPETS.register(Witchery.id("death_protection")) {
        DeathProtectionPoppet()
    }

    val VAMPIRIC: RegistrySupplier<VampiricPoppet> = POPPETS.register(Witchery.id("vampiric")) {
        VampiricPoppet()
    }

    val VOODOO: RegistrySupplier<VoodooPoppet> = POPPETS.register(Witchery.id("voodoo")) {
        VoodooPoppet()
    }

    val HUNGER_PROTECTION = POPPETS.register(Witchery.id("hunger_protection")) {
        HungerProtectionPoppet()
    }

    val ARMOR_PROTECTION: RegistrySupplier<ArmorProtectionPoppet> = POPPETS.register(Witchery.id("armor_protection")) {
        ArmorProtectionPoppet()
    }

    val VOODOO_PROTECTION: RegistrySupplier<VoodooProtectionPoppet> = POPPETS.register(Witchery.id("voodoo_protection")) {
        VoodooProtectionPoppet()
    }

    @JvmStatic
    fun getType(item: Item): PoppetType? = POPPETS.find { it.item == item }

    @JvmStatic
    fun getType(id: ResourceLocation): PoppetType? = POPPETS[id]


    fun register() {

    }

    fun getAllTypes(): List<PoppetType>  {
        return POPPETS.entrySet().map { it.value }
    }

    fun getIdForPoppet(poppetType: PoppetType): ResourceLocation {
        return POPPETS.getId(poppetType)!!
    }
}