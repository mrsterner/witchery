package dev.sterner.witchery.platform.neoforge

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.neoforge.item.HunterArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.VampireArmorItemNeoForge
import dev.sterner.witchery.neoforge.item.WitchesRobesItemNeoForge
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.neoforged.fml.ModList

object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle(): BoneNeedleItem {
        return BoneNeedleItemForge(Item.Properties())
    }

    @JvmStatic
    fun witchesRobes(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return WitchesRobesItemNeoForge(witchesRobes, chestplate, properties)
    }

    @JvmStatic
    fun hunterArmor(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return HunterArmorItemNeoForge(witchesRobes, chestplate, properties)
    }

    @JvmStatic
    fun dapper(
        dapper: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return VampireArmorItemNeoForge(dapper, chestplate, properties)
    }

    @JvmStatic
    fun tryEnableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            player.abilities.flying = true
            player.abilities.mayfly = true
            player.onUpdateAbilities()
        }
    }

    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            player.abilities.flying = false
            player.abilities.mayfly = false
            player.onUpdateAbilities()
        }
    }
}