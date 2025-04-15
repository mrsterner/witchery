package dev.sterner.witchery.handler.ability

enum class WerewolfAbility(override val unlockLevel: Int) : PlayerAbilityType {
    FREE_WOLF_TRANSFORM(5),
    FREE_WEREWOLF_TRANSITION(5);

    override val id: String get() = name.lowercase()
}