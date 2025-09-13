package dev.sterner.witchery.handler.ability

enum class VampireAbility(override val unlockLevel: Int, override val cooldown: Int) : PlayerAbilityType {
    DRINK_BLOOD(1, 0),
    TRANSFIX(2, 20 * 5),
    SPEED(4, 20 * 2),
    BAT_FORM(7, 20 * 5);

    override val id: String get() = name.lowercase()
}