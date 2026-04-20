package dev.sterner.witchery.integration.sable

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.neoforged.fml.loading.FMLLoader

object SableCompat {

    val isLoaded: Boolean by lazy {
        FMLLoader.getLoadingModList().getModFileById("sable") != null
    }

    fun projectOutOfSubLevel(level: Level, pos: BlockPos): BlockPos {
        return if (isLoaded) SableCompatImpl.projectOutOfSubLevel(level, pos) else pos
    }

    fun distanceSquaredWithSubLevels(level: Level, a: BlockPos, b: BlockPos): Double {
        return if (isLoaded) SableCompatImpl.distanceSquaredWithSubLevels(level, a, b)
        else a.distSqr(b)
    }
}