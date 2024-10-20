package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

object WitcheryArmorMaterials {

    val MATERIALS = DeferredRegister.create(Witchery.MODID, Registries.ARMOR_MATERIAL)

    val WITCHES_ROBES = MATERIALS.register("witches_robes") {
        ArmorMaterial(java.util.Map.of(
            ArmorItem.Type.HELMET, 3,
            ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.LEGGINGS, 6,
            ArmorItem.Type.BOOTS, 3
        ),
            5, SoundEvents.ARMOR_EQUIP_LEATHER,{Ingredient.of(Items.WHITE_WOOL)},
            listOf(
                ArmorMaterial.Layer(Witchery.id("witches_robes"))
            ),
            3f,
            0f
        )
    }
}