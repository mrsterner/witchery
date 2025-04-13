package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item

object PlatformUtils {

    @JvmStatic
    @ExpectPlatform
    fun isModLoaded(modId: String?): Boolean {
        throw AssertionError()
    }

    @JvmStatic
    @get:ExpectPlatform
    val boneNeedle: BoneNeedleItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @ExpectPlatform
    fun witchesRobes(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun hunterArmor(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun dapper(
        dapper: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun tryEnableBatFlight(player: Player) {
       throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun tryDisableBatFlight(player: Player) {
        throw AssertionError()
    }
}