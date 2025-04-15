package dev.sterner.witchery.handler.ability

enum class VampireAbility(override val unlockLevel: Int) : PlayerAbilityType {
    DRINK_BLOOD(1),
    TRANSFIX(2),
    SPEED(4),
    BAT_FORM(7);

    override val id: String get() = name.lowercase()
}