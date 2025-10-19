package dev.sterner.witchery.core.api.interfaces


import dev.sterner.witchery.content.entity.ChainEntity

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