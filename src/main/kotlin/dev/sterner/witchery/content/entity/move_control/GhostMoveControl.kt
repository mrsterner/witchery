package dev.sterner.witchery.content.entity.move_control

import dev.sterner.witchery.content.entity.AbstractSpectralEntity
import dev.sterner.witchery.content.entity.SpectreEntity
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.control.MoveControl.Operation
import net.minecraft.world.phys.Vec3

class GhostMoveControl(private val ghost: AbstractSpectralEntity) : MoveControl(
    ghost
) {
    private var floatDuration = 0

    override fun tick() {
        if (this.operation == Operation.MOVE_TO) {
            if (floatDuration-- <= 0) {
                this.floatDuration = this.floatDuration + ghost.random.nextInt(5) + 2
                var vec3 = Vec3(
                    this.wantedX - ghost.x,
                    this.wantedY - ghost.y,
                    this.wantedZ - ghost.z
                )
                val d = vec3.length()
                vec3 = vec3.normalize()
                if (this.canReach(vec3, Mth.ceil(d))) {
                    ghost.deltaMovement = ghost.deltaMovement.add(vec3.scale(0.1))
                } else {
                    this.operation = Operation.WAIT
                }

                if (d > 0.1) {
                    val targetYaw = (Mth.atan2(vec3.z, vec3.x) * (180 / Math.PI)).toFloat() - 90.0f
                    ghost.yRot = this.rotateTowards(ghost.yRot, targetYaw, 25.0f)
                }
            }
        }
    }

    private fun canReach(pos: Vec3, length: Int): Boolean {
        var aABB = ghost.boundingBox

        for (i in 1 until length) {
            aABB = aABB.move(pos)
            if (!ghost.level().noCollision(this.ghost, aABB)) {
                return false
            }
        }

        return true
    }

    private fun rotateTowards(currentYaw: Float, targetYaw: Float, maxTurn: Float): Float {
        val deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw)
        return currentYaw + Mth.clamp(deltaYaw, -maxTurn, maxTurn)
    }
}