package dev.sterner.witchery.integration

import net.minecraft.world.entity.Entity
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import tallestegg.guardvillagers.common.entities.Guard

object CompatHelper {

    fun isGuard(entity: Entity): Boolean {
        return ModList.get().isLoaded("guard_villagers") && entity is Guard
    }
}