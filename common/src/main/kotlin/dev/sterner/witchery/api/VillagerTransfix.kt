package dev.sterner.witchery.api

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

interface VillagerTransfix {
   fun setTransfixedLookVector(vec3: Vec3)

   fun isTransfixed(): Boolean
}