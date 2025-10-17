package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player

class TheLoversEffect : TarotEffect(7) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Lovers (Reversed)" else "The Lovers"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Creatures turn hostile" else "Creatures are drawn to you"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed && player.level().gameTime % 20 == 0L) {
            val nearbyAnimals = player.level().getEntitiesOfClass(
                Animal::class.java,
                player.boundingBox.inflate(16.0)
            )

            for (animal in nearbyAnimals) {
                if (animal is TamableAnimal && animal.isTame) continue

                if (player.level().random.nextFloat() < 0.1f) {
                    if (animal is Mob) {
                        val targets = nearbyAnimals.filter { it != animal }
                        if (targets.isNotEmpty() && player.level().random.nextBoolean()) {
                            animal.target = targets.random() as? LivingEntity
                        } else {
                            animal.target = player
                        }
                    }
                }
            }
        } else if (!isReversed) {
            val nearbyAnimals = player.level().getEntitiesOfClass(
                Animal::class.java,
                player.boundingBox.inflate(8.0)
            )

            for (animal in nearbyAnimals) {
                if (animal.brain.hasMemoryValue(MemoryModuleType.IS_PANICKING)) {
                    animal.brain.eraseMemory(MemoryModuleType.IS_PANICKING)
                }
            }
        }
    }
}