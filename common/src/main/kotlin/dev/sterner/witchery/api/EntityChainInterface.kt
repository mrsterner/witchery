package dev.sterner.witchery.api

import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.world.phys.Vec3

interface EntityChainInterface {
    /**
     * Add a chain entity that is restraining this entity
     */
    fun `witchery$restrainMovement`(chainEntity: ChainEntity, totalRestrain: Boolean)

    /**
     * Check if this entity is currently restrained by chains
     */
    fun `witchery$isRestrained`(): Boolean

    /**
     * Get all chains currently restraining this entity
     */
    fun `witchery$getRestrainingChains`(): List<ChainEntity>
}