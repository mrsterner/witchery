package dev.sterner.witchery.api

import dev.sterner.witchery.entity.ChainEntity

// EntityChainInterface.kt
interface EntityChainInterface {
    /**
     * Called when the entity is being restrained by a chain
     */
    fun `witchery$restrainMovement`(chainEntity: ChainEntity)

    /**
     * Check if this entity is currently restrained by any chains
     */
    fun `witchery$isRestrained`(): Boolean

    /**
     * Get the chains that are currently restraining this entity
     */
    fun `witchery$getRestrainingChains`(): List<ChainEntity>
}