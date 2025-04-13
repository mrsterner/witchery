package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.item.accessories.*
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
    @get:ExpectPlatform
    val batwingPendantItem: BatwingPendantItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val sunstonePendantItem: SunstonePendantItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val bloodstonePendantItem: BloodstonePendantItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val barkBeltItem: BarkBeltItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val bitingBeltItem: BitingBeltItem
        get() {
            throw AssertionError()
        }

    @JvmStatic
    @get:ExpectPlatform
    val dreamweaverCharmItem: DreamweaverCharmItem
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