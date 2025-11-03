package dev.sterner.witchery.content.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.HunterArmorModel
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.jetbrains.annotations.NotNull
import javax.annotation.Nullable


open class HunterArmorItem(material: Holder<ArmorMaterial>, type: Type, properties: Properties) :
    ArmorItem(material, type, properties) {
    override fun isRepairable(arg: ItemStack): Boolean {
        return false
    }


    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.literal("Hunter's Protection")
                .withStyle(ChatFormatting.GOLD)
        )
        if (!Screen.hasShiftDown()) {

            tooltipComponents.add(
                Component.literal("Hold ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" for more info"))
            )
        } else {
            tooltipComponents.add(
                Component.literal("Set Bonus:").withStyle(ChatFormatting.GRAY)
            )
            tooltipComponents.add(
                Component.literal(" - Reduced harmful potion duration").withStyle(ChatFormatting.BLUE)
            )
            tooltipComponents.add(
                Component.literal(" - Reduced curse duration").withStyle(ChatFormatting.BLUE)
            )
            tooltipComponents.add(
                Component.literal(" - Magic resistance").withStyle(ChatFormatting.BLUE)
            )
            tooltipComponents.add(
                Component.literal(" - Possible Curse reflection").withStyle(ChatFormatting.BLUE)
            )
            tooltipComponents.add(
                Component.literal("Bonuses scale with pieces worn").withStyle(ChatFormatting.DARK_GRAY)
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }


    @Nullable
    override fun getArmorTexture(
        stack: ItemStack,
        entity: Entity,
        slot: EquipmentSlot,
        layer: ArmorMaterial.Layer,
        innerModel: Boolean
    ): ResourceLocation? {
        return if (layer.dyeable()) {
            ResourceLocation.fromNamespaceAndPath(
                Witchery.MODID,
                "textures/models/armor/witch_hunter_armor.png"
            )
        } else {
            ResourceLocation.fromNamespaceAndPath(
                Witchery.MODID,
                "textures/models/armor/witch_hunter_armor_overlay.png"
            )
        }
    }

    class ArmorRender : IClientItemExtensions {
        @NotNull
        override fun getHumanoidArmorModel(
            living: LivingEntity,
            stack: ItemStack,
            slot: EquipmentSlot,
            model: HumanoidModel<*>
        ): HumanoidModel<*> {
            val models = Minecraft.getInstance().entityModels
            val root = models.bakeLayer(HunterArmorModel.LAYER_LOCATION)
            val armor = HunterArmorModel(root)
            armor.setAllVisible(false)


            armor.head.visible = slot == EquipmentSlot.HEAD
            armor.body.visible = slot == EquipmentSlot.CHEST
            armor.leftArm.visible = slot == EquipmentSlot.CHEST
            armor.rightArm.visible = slot == EquipmentSlot.CHEST
            armor.leftLeg.visible = slot == EquipmentSlot.FEET
            armor.rightLeg.visible = slot == EquipmentSlot.FEET

            return armor
        }

        override fun getArmorLayerTintColor(
            stack: ItemStack,
            entity: LivingEntity,
            layer: ArmorMaterial.Layer,
            layerIdx: Int,
            fallbackColor: Int
        ): Int {
            if (layer.dyeable()) {
                return DyeColor.BLACK.textureDiffuseColor
            }
            return -0x1
        }


        companion object {
            val INSTANCE: ArmorRender = ArmorRender()
        }
    }
}