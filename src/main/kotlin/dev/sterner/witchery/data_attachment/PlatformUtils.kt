package dev.sterner.witchery.data_attachment

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.item.accessories.*
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType

object PlatformUtils {

    @JvmStatic
    fun isDevEnv(): Boolean {
        throw AssertionError()
    }

    @JvmStatic
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
    val moonCharmItem: MoonCharmItem
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
    fun witchesRobes(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    fun hunterArmor(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    fun dapper(
        dapper: RegistrySupplier<ArmorMaterial>,
        type: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        throw AssertionError()
    }

    @JvmStatic
    fun tryEnableBatFlight(player: Player) {
        throw AssertionError()
    }

    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        throw AssertionError()
    }

    @JvmStatic
    fun allEquippedAccessories(livingEntity: Player): List<ItemStack> {
        throw AssertionError()
    }

    @JvmStatic
    fun registerWoodType(woodType: WoodType): WoodType {
        throw AssertionError()
    }

    @JvmStatic
    fun getByClass(): MutableMap<Class<*>, ArgumentTypeInfo<*, *>> {
        throw AssertionError()
    }

    @JvmStatic
    fun registerMobEffect(name: String, effect: MobEffect): Holder<MobEffect> {
        throw AssertionError()
    }
}