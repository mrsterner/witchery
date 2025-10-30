package dev.sterner.witchery.integration

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import tallestegg.guardvillagers.GuardEntityType
import tallestegg.guardvillagers.common.entities.Guard

object CompatHelper {

    fun isLoaded(): Boolean {
        return FMLLoader.getLoadingModList().getModFileById("guardvillagers") != null
    }

    fun isGuard(entity: Entity): Boolean {
        return isLoaded() && entity is Guard
    }

    fun getGuard(): EntityType<*>? {
        if (isLoaded()) {
            return GuardEntityType.GUARD.get()
        }
        return null
    }
}