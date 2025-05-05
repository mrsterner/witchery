package dev.sterner.witchery.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

interface VampireEvent {
    companion object {

        val ON_LEVEL_UP: Event<LevelUp> = EventFactory.createEventResult()

        val ON_SUN_DAMAGE: Event<SunDamage> = EventFactory.createEventResult()

        val ON_BLOOD_DRINK: Event<BloodDrink> = EventFactory.createEventResult()
    }
}

typealias LevelUp = (Player, Int, Int) -> EventResult
typealias SunDamage = (Player) -> EventResult
typealias BloodDrink = (Player, LivingEntity) -> EventResult
