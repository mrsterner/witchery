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
            ArmorItem.Type.HELMET, 1,
            ArmorItem.Type.CHESTPLATE, 3,
            ArmorItem.Type.LEGGINGS, 2,
            ArmorItem.Type.BOOTS, 1
        ),
            15, SoundEvents.ARMOR_EQUIP_LEATHER,{Ingredient.of(Items.WHITE_WOOL)}, //TODO replace with something better
            listOf(
                ArmorMaterial.Layer(Witchery.id("witches_robes"))
            ),
            1f,
            0f
        )
    }
}