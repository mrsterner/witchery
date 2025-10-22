package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
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
                if (animal !is Mob) continue

                if (player.level().random.nextFloat() < 0.1f) {
                    ensureAttackGoals(animal)

                    val targets = nearbyAnimals.filter { it != animal && it is LivingEntity }
                    if (targets.isNotEmpty() && player.level().random.nextBoolean()) {
                        animal.target = targets.random() as LivingEntity
                        animal.setAggressive(true)
                    } else {
                        animal.target = player
                        animal.setAggressive(true)
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

    private fun ensureAttackGoals(mob: PathfinderMob) {
        val hasAttackGoal = mob.goalSelector.availableGoals.any {
            it.goal is MeleeAttackGoal
        }

        if (!hasAttackGoal) {
            mob.goalSelector.addGoal(1, MeleeAttackGoal(mob, 1.2, false))
        }

        val hasTargetGoal = mob.targetSelector.availableGoals.any {
            it.goal is NearestAttackableTargetGoal<*>
        }

        if (!hasTargetGoal) {
            mob.targetSelector.addGoal(1, NearestAttackableTargetGoal(mob, Player::class.java, true))
            mob.targetSelector.addGoal(2, NearestAttackableTargetGoal(mob, Animal::class.java, true))
        }
    }
}