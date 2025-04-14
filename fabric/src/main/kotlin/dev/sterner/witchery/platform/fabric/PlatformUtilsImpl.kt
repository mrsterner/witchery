package dev.sterner.witchery.platform.fabric

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethodStage.Vanilla
import dev.architectury.registry.registries.RegistrySupplier
import dev.emi.trinkets.api.TrinketComponent
import dev.emi.trinkets.api.TrinketsApi
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.item.trinkets.*
import dev.sterner.witchery.fabric.mixin.ArgumentTypeInfosInvoker
import dev.sterner.witchery.fabric.mixin.WoodTypeInvoker
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.item.accessories.*
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.registry.WitcheryItems
import io.github.ladysnake.pal.AbilitySource
import io.github.ladysnake.pal.Pal
import io.github.ladysnake.pal.VanillaAbilities
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.level.block.state.properties.WoodType
import java.awt.Color


object PlatformUtilsImpl {

    @JvmStatic
    fun isModLoaded(modId: String?): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    @JvmStatic
    fun getBoneNeedle(): BoneNeedleItem {
        return BoneNeedleItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getBarkBeltItem(): BarkBeltItem {
        return BarkBeltItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getBatwingPendantItem(): BatwingPendantItem {
        return BatwingPendantItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getBitingBeltItem(): BitingBeltItem {
        return BitingBeltItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getBloodstonePendantItem(): BloodstonePendantItem {
        return BloodstonePendantItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getSunstonePendantItem(): SunstonePendantItem {
        return SunstonePendantItemFabric(Item.Properties())
    }

    @JvmStatic
    fun getDreamweaverCharmItem(): DreamweaverCharmItem {
        return DreamCharmItemFabric(Item.Properties())
    }

    @JvmStatic
    fun witchesRobes(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return object : ArmorItem(witchesRobes, chestplate, properties) {
            override fun appendHoverText(
                stack: ItemStack,
                context: TooltipContext,
                tooltipComponents: MutableList<Component>,
                tooltipFlag: TooltipFlag
            ) {
                if (stack.`is`(WitcheryItems.BABA_YAGAS_HAT.get())) {
                    tooltipComponents.add(
                        Component.translatable("witchery.secondbrewbonus.25")
                            .setStyle(Style.EMPTY.withColor(Color(255, 75, 255).rgb))
                    )
                    tooltipComponents.add(
                        Component.translatable("witchery.thirdbrewbonus.25")
                            .setStyle(Style.EMPTY.withColor(Color(255, 75, 255).rgb))
                    )
                } else if (stack.`is`(WitcheryItems.WITCHES_HAT.get())) {
                    tooltipComponents.add(
                        Component.translatable("witchery.secondbrewbonus.35")
                            .setStyle(Style.EMPTY.withColor(Color(255, 75, 255).rgb))
                    )
                } else if (stack.`is`(WitcheryItems.WITCHES_ROBES.get())) {
                    tooltipComponents.add(
                        Component.translatable("witchery.secondbrewbonus.35")
                            .setStyle(Style.EMPTY.withColor(Color(255, 75, 255).rgb))
                    )
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
            }
        }
    }

    @JvmStatic
    fun hunterArmor(
        witchesRobes: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return ArmorItem(witchesRobes, chestplate, properties)
    }

    @JvmStatic
    fun dapper(
        dapper: RegistrySupplier<ArmorMaterial>,
        chestplate: ArmorItem.Type,
        properties: Item.Properties
    ): ArmorItem {
        return object: ArmorItem(dapper, chestplate, properties) {

            fun createExtraAttributes(): List<ItemAttributeModifiers.Entry> {
                val attributes = ItemAttributeModifiers.builder()
                attributes.add(
                    WitcheryAttributes.VAMPIRE_DRINK_SPEED,
                    AttributeModifier(
                        Witchery.id("drink_speed_bonus"),
                        10.0,
                        AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.ARMOR
                )
                return attributes.build().modifiers()
            }


            override fun getDefaultAttributeModifiers(): ItemAttributeModifiers {
                val modifiers = super.getDefaultAttributeModifiers()
                val builder = ItemAttributeModifiers.builder()

                val entries = modifiers.modifiers()
                for (entry in entries) {
                    builder.add(entry.attribute(), entry.modifier(), entry.slot())
                }

                val extraEntries = createExtraAttributes()
                for (entry in extraEntries) {
                    builder.add(entry.attribute(), entry.modifier(), entry.slot())
                }

                return builder.build()
            }
        }
    }

    @JvmStatic
    fun allEquippedAccessories(livingEntity: Player): List<ItemStack> {
        return TrinketsApi.TRINKET_COMPONENT.get(livingEntity).allEquipped.map { it.b }
    }

    //PAL
    val BAT_FLIGHT_ABILITY_SOURCE_ID: ResourceLocation = Witchery.id("bat")
    val BAT_ABILITY_SOURCE: AbilitySource = Pal.getAbilitySource(BAT_FLIGHT_ABILITY_SOURCE_ID)

    @JvmStatic
    fun tryEnableBatFlight(player: Player) {
        BAT_ABILITY_SOURCE.grantTo(player, VanillaAbilities.FLYING)
        BAT_ABILITY_SOURCE.grantTo(player, VanillaAbilities.ALLOW_FLYING)
    }

    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        BAT_ABILITY_SOURCE.revokeFrom(player, VanillaAbilities.FLYING)
        BAT_ABILITY_SOURCE.revokeFrom(player, VanillaAbilities.ALLOW_FLYING)
    }

    @JvmStatic
    fun registerWoodType(woodType: WoodType): WoodType {
        return WoodTypeInvoker.invokeRegister(woodType)
    }

    @JvmStatic
    fun getByClass(): MutableMap<Class<*>, ArgumentTypeInfo<*, *>> {
        return ArgumentTypeInfosInvoker.getByClass()
    }
}