package dev.sterner.witchery.api

import net.minecraft.world.phys.Vec3
import java.util.*

interface VillagerTransfix {
   fun setTransfixedLookVector(vec3: Vec3)

   fun isTransfixed(): Boolean

   fun setMesmerized(uuid: UUID)

   fun isMesmerized(): Boolean

   fun getMesmerized(): UUID
}