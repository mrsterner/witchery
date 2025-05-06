package dev.sterner.witchery.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.MoverType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

abstract class AbstractSpectralEntity(entityType: EntityType<out FlyingMob>, level: Level) : FlyingMob(entityType,
    level
) {

    override fun move(type: MoverType, pos: Vec3) {
        super.move(type, pos)
        this.checkInsideBlocks()
    }

    override fun tick() {
        this.noPhysics = true
        super.tick()
        this.noPhysics = false

    }
}