package dev.sterner.witchery.api

import net.minecraft.world.phys.Vec3
import java.util.*

/**
 * Interface used in VillagerMixin to modify behaviour when a vampire player interacts with the villager
 */
interface VillagerTransfix {

    /**
     * Forces the Villager to look in this direction when transfixed
     */
    fun setTransfixedLookVector(vec3: Vec3)

    /**
     * Returns true if there's an active transfix going on
     */
    fun isTransfixed(): Boolean

    /**
     * A more powerful version of Transfix, this will make the Villager follow the vampire player too
     */
    fun setMesmerized(uuid: UUID)

    /**
     * Returns true if there's an active mesmer going on
     */
    fun isMesmerized(): Boolean

    /**
     * Get the player vampire which is currently mesmerising the villager
     */
    fun getMesmerized(): UUID
}