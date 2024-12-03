package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

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
    fun barkBelt(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun batwingPendant(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun bitingBelt(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun bloodstonePendant(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun sunstonePendant(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun dreamweaverCharm(
        properties: Item.Properties
    ): Item {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getAllEquippedAccessories(living: LivingEntity): List<ItemStack> {
        throw AssertionError()
    }
}