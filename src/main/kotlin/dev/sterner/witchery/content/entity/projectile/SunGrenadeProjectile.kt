package dev.sterner.witchery.content.entity.projectile

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling
import net.minecraft.core.particles.DustColorTransitionOptions
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.*


class SunGrenadeProjectile : ThrowableItemProjectile {
    constructor(entityType: EntityType<out SunGrenadeProjectile?>, level: Level) : super(entityType, level)

    constructor(level: Level) : super(WitcheryEntityTypes.SUN_GRENADE.get(), level)

    constructor(level: Level, shooter: LivingEntity) : super(WitcheryEntityTypes.SUN_GRENADE.get(), shooter, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(WitcheryEntityTypes.SUN_GRENADE.get(), x, y, z, level)

    override fun getDefaultItem(): Item {
        return WitcheryItems.QUARTZ_SPHERE.get()
    }

    private val particle: ParticleOptions
        get() {
            val itemstack = this.item
            return (if (!itemstack.isEmpty && !itemstack.`is`(this.defaultItem))
                ItemParticleOption(ParticleTypes.ITEM, itemstack)
            else
                ParticleTypes.ITEM_SNOWBALL) as ParticleOptions
        }

    override fun handleEntityEvent(id: Byte) {
        if (id.toInt() == 3) {
            val baseParticle = this.particle

            val paleYellow = Vec3(1.0, 1.0, 0.6)
            val white = Vec3(1.0, 1.0, 1.0)

            val sunParticle = DustColorTransitionOptions(
                paleYellow.toVector3f(),
                white.toVector3f(),
                1.0f
            )

            fun spread() = (this.level().random.nextDouble() - 0.5) * 1.0

            this.level().playLocalSound(
                this.x, this.y, this.z,
                SoundEvents.GLASS_BREAK,
                SoundSource.NEUTRAL,
                0.8f,
                1.2f + (Math.random().toFloat() * 0.3f),
                false
            )

            repeat(4) {
                this.level().addParticle(
                    sunParticle,
                    this.x + spread(),
                    this.y + spread(),
                    this.z + spread(),
                    0.0, 0.01, 0.0
                )
            }

            repeat(8) {
                this.level().addParticle(
                    baseParticle,
                    this.x + spread(),
                    this.y + spread(),
                    this.z + spread(),
                    0.0, 0.0, 0.0
                )
            }

            repeat(4) {
                this.level().addParticle(
                    ParticleTypes.END_ROD,
                    this.x + spread(),
                    this.y + spread(),
                    this.z + spread(),
                    0.0, 0.01, 0.0
                )
            }
        }
    }

    override fun onHitEntity(result: EntityHitResult) {
        super.onHitEntity(result)
        val e = result.entity

        e.hurt(this.damageSources().thrown(this, this.owner), 1f)

        e.remainingFireTicks = 20 * 4

        if (e is ServerPlayer) {
            VampireLeveling.increaseUsedSunGrenades(e)
        }
    }

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)
        val hitPos = result.blockPos.relative(result.direction)

        val radius = 1.5
        val aabb = AABB(
            hitPos.x - radius, hitPos.y - radius, hitPos.z - radius,
            hitPos.x + radius, hitPos.y + radius, hitPos.z + radius
        )

        val entities = this.level().getEntities(this, aabb)

        for (e in entities) {
            e.hurt(this.damageSources().thrown(this, this.owner), 1f)

            e.remainingFireTicks = 20 * 4

            if (e is ServerPlayer) {
                VampireLeveling.increaseUsedSunGrenades(e)
            }
        }
    }

    override fun onHit(result: HitResult) {
        super.onHit(result)
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, 3.toByte())
            this.discard()
        }
    }
}