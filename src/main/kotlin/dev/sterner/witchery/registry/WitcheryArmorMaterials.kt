package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

object WitcheryArmorMaterials {

    val MATERIALS: DeferredRegister<ArmorMaterial> = DeferredRegister.create(Witchery.MODID, Registries.ARMOR_MATERIAL)

    val WITCHES_ROBES: RegistrySupplier<ArmorMaterial> = MATERIALS.register("witches_robes") {
        ArmorMaterial(
            java.util.Map.of(
                ArmorItem.Type.HELMET, 1,
                ArmorItem.Type.CHESTPLATE, 3,
                ArmorItem.Type.LEGGINGS, 2,
                ArmorItem.Type.BOOTS, 1
            ),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            { Ingredient.of(WitcheryItems.GOLDEN_THREAD.get()) },
            listOf(
                ArmorMaterial.Layer(Witchery.id("witches_robes"))
            ),
            1f,
            0f
        )
    }

    val HUNTER: RegistrySupplier<ArmorMaterial> = MATERIALS.register("hunter") {
        ArmorMaterial(
            java.util.Map.of(
                ArmorItem.Type.HELMET, 2,
                ArmorItem.Type.CHESTPLATE, 4,
                ArmorItem.Type.LEGGINGS, 3,
                ArmorItem.Type.BOOTS, 2
            ),
            10,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            { Ingredient.of(Items.LEATHER) },
            listOf(
                ArmorMaterial.Layer(Witchery.id("hunter"), "", true),
                ArmorMaterial.Layer(Witchery.id("hunter"), "_overlay", false)
            ),
            3f,
            0f
        )
    }

    val DAPPER: RegistrySupplier<ArmorMaterial> = MATERIALS.register("dapper") {
        ArmorMaterial(
            java.util.Map.of(
                ArmorItem.Type.HELMET, 1,
                ArmorItem.Type.CHESTPLATE, 3,
                ArmorItem.Type.LEGGINGS, 2,
                ArmorItem.Type.BOOTS, 1
            ),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            { Ingredient.of(WitcheryItems.WOVEN_CRUOR.get()) },
            listOf(
                ArmorMaterial.Layer(Witchery.id("dapper"))
            ),
            1f,
            0f
        )
    }
}