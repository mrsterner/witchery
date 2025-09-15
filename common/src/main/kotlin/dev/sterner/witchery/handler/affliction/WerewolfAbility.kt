package dev.sterner.witchery.handler.affliction

enum class WerewolfAbility(
    override val requiredLevel: Int,
    override val cooldown: Int,
    override val affliction: AfflictionTypes = AfflictionTypes.LYCANTHROPY
) : AfflictionAbility {
    //TODO add abilities later
}