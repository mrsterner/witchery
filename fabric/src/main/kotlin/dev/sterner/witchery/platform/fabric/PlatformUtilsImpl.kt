package dev.sterner.witchery.platform.fabric

import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.*
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
}