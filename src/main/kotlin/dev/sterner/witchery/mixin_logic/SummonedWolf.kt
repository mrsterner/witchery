package dev.sterner.witchery.mixin_logic

interface SummonedWolf {
    fun `witchery$setSummoned`(summoned: Boolean)
    fun `witchery$isSummoned`(): Boolean
    fun `witchery$setSummonDuration`(ticks: Int)
    fun `witchery$getRemainingTime`(): Int
}