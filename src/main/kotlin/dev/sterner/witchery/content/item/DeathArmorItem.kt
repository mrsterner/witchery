package dev.sterner.witchery.content.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.model.HunterArmorModel
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.jetbrains.annotations.NotNull
import javax.annotation.Nullable


open class DeathArmorItem(material: Holder<ArmorMaterial>, type: Type, properties: Properties) :
    ArmorItem(material, type, properties) {
    override fun isRepairable(arg: ItemStack): Boolean {
        return false
    }


    @Nullable
    override fun getArmorTexture(
        stack: ItemStack,
        entity: Entity,
        slot: EquipmentSlot,
        layer: ArmorMaterial.Layer,
        innerModel: Boolean
    ): ResourceLocation? {
        return ResourceLocation.fromNamespaceAndPath(
            Witchery.MODID,
            "textures/models/armor/death_robes.png"
        )
    }
/*TODO
    class ArmorRender : IClientItemExtensions {
        @NotNull
        override fun getHumanoidArmorModel(
            living: LivingEntity,
            stack: ItemStack,
            slot: EquipmentSlot,
            model: HumanoidModel<*>?
        ): HumanoidModel<*> {
            val models = Minecraft.getInstance().entityModels
            val root = models.bakeLayer(DeathArmorModel.LAYER_LOCATION)
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

        companion object {
            val INSTANCE: ArmorRender = ArmorRender()
        }
    }

 */
}