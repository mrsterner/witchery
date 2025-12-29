package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.WerewolfEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.phys.Vec3
import java.util.EnumSet


class WerewolfBiteGoal(val werewolf: WerewolfEntity) : Goal() {

    init {
        setFlags(EnumSet.of(Flag.MOVE))
    }

    override fun canUse(): Boolean {
        val target = werewolf.target ?: return false
        return werewolf.canBite() &&
                werewolf.distanceToSqr(target) < 9.0 &&
                werewolf.random.nextFloat() < 0.05f
    }

    override fun start() {
        val target = werewolf.target ?: return

        val direction = Vec3(
            target.x - werewolf.x,
            0.0,
            target.z - werewolf.z
        ).normalize()

        werewolf.deltaMovement = werewolf.deltaMovement.add(
            direction.x * 0.8,
            0.3,
            direction.z * 0.8
        )

        werewolf.hasImpulse = true
        werewolf.setBiteCooldown(100)

        if (werewolf.distanceToSqr(target) < 4.0) {
            werewolf.doHurtTarget(target)
            target.addEffect(net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
                40,
                1
            ))
        }
    }
}