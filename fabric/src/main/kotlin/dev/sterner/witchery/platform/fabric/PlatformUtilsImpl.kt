package dev.sterner.witchery.platform.fabric

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.registry.WitcheryAttributes
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.*
import net.minecraft.world.item.component.ItemAttributeModifiers
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
}