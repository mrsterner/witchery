package dev.sterner.witchery.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorItem.Type
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.jetbrains.annotations.NotNull
import java.awt.Color
import javax.annotation.Nullable


open class WitchesRobesItem(material: Holder<ArmorMaterial>, type: Type, properties: Properties) :
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

    @Nullable
    override fun getArmorTexture(
        stack: ItemStack,
        entity: Entity,
        slot: EquipmentSlot,
        layer: ArmorMaterial.Layer,
        innerModel: Boolean
    ): ResourceLocation? {
        if (slot == EquipmentSlot.HEAD && stack.`is`(WitcheryItems.BABA_YAGAS_HAT.get())) {
            return ResourceLocation.fromNamespaceAndPath(
                Witchery.MODID,
                "textures/models/armor/baba_yagas_hat.png"
            )
        }

        return ResourceLocation.fromNamespaceAndPath(
            Witchery.MODID,
            "textures/models/armor/witches_robes.png"
        )
    }


    class ArmorRender : IClientItemExtensions {
        @NotNull
        override fun getHumanoidArmorModel(
            living: LivingEntity,
            stack: ItemStack,
            slot: EquipmentSlot,
            model: HumanoidModel<*>?
        ): HumanoidModel<*> {
            val models = Minecraft.getInstance().entityModels
            val root = models.bakeLayer(WitchesRobesModel.LAYER_LOCATION)
            val armor = WitchesRobesModel(root)
            armor.setAllVisible(false)

            if (living.hasEffect(MobEffects.INVISIBILITY)) {
                armor.head.visible = false
                armor.body.visible = false
                armor.leftArm.visible = false
                armor.rightArm.visible = false
                armor.leftLeg.visible = false
                armor.rightLeg.visible = false
            } else {
                armor.head.visible = slot == EquipmentSlot.HEAD
                armor.body.visible = slot == EquipmentSlot.CHEST
                armor.leftArm.visible = slot == EquipmentSlot.CHEST
                armor.rightArm.visible = slot == EquipmentSlot.CHEST
                armor.leftLeg.visible = slot == EquipmentSlot.FEET
                armor.rightLeg.visible = slot == EquipmentSlot.FEET
            }

            return armor
        }

        override fun getArmorLayerTintColor(
            stack: ItemStack,
            entity: LivingEntity,
            layer: ArmorMaterial.Layer,
            layerIdx: Int,
            fallbackColor: Int
        ): Int {
            return if (stack.`is`(WitcheryItems.BABA_YAGAS_HAT.get())) -0x1 else DyeColor.BLACK.textureDiffuseColor
        }

        companion object {
            val INSTANCE: ArmorRender = ArmorRender()
        }
    }
}