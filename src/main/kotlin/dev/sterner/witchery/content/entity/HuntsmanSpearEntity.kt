package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class HuntsmanSpearEntity : AbstractArrow {

    private var dealtDamage = false
    var clientSideReturnSpearTickCount = 0
    private var thrownByBoss = false

    companion object {
        private val ID_LOYALTY = SynchedEntityData.defineId(HuntsmanSpearEntity::class.java, EntityDataSerializers.BYTE)
        private val ID_FOIL = SynchedEntityData.defineId(HuntsmanSpearEntity::class.java, EntityDataSerializers.BOOLEAN)
        val ID_THROWN_BY_HUNTSMAN: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(HuntsmanSpearEntity::class.java, EntityDataSerializers.BOOLEAN)

        private const val BASE_DAMAGE = 12.0f
        private const val HUNTSMAN_DAMAGE_BONUS = 6.0f
    }

    constructor(level: Level) : super(WitcheryEntityTypes.HUNTSMAN_SPEAR.get(), level) {
        this.pickup = Pickup.DISALLOWED
    }

    constructor(level: Level, owner: LivingEntity, pickupItemStack: ItemStack) : super(
        WitcheryEntityTypes.HUNTSMAN_SPEAR.get(),
        owner,
        level,
        pickupItemStack,
        null
    ) {
        this.pickup = if (owner is Player && owner.abilities.instabuild) Pickup.CREATIVE_ONLY else Pickup.DISALLOWED
        this.thrownByBoss = owner is HornedHuntsmanEntity
        entityData.set(ID_THROWN_BY_HUNTSMAN, thrownByBoss)
        entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pickupItemStack))
        entityData.set(ID_FOIL, pickupItemStack.hasFoil())
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(ID_LOYALTY, 0.toByte())
        builder.define(ID_FOIL, false)
        builder.define(ID_THROWN_BY_HUNTSMAN, false)
    }

    override fun tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true
        }

        val entity = this.owner
        val loyalty = this.entityData.get(ID_LOYALTY).toInt()

        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.pickupItem, 0.1f)
                }

                this.discard()
            } else {
                this.setNoPhysics(true)
                val vec3 = entity.eyePosition.subtract(this.position())
                this.setPosRaw(this.x, this.y + vec3.y * 0.015 * loyalty.toDouble(), this.z)

                if (this.level().isClientSide) {
                    this.yOld = this.y
                }

                val d = 0.05 * loyalty
                this.deltaMovement = this.deltaMovement.scale(0.95).add(vec3.normalize().scale(d))

                if (this.clientSideReturnSpearTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f)
                }

                ++this.clientSideReturnSpearTickCount

                if (this.level().isClientSide) {
                    this.level().addParticle(
                        ParticleTypes.SMOKE,
                        this.x, this.y, this.z,
                        0.0, 0.0, 0.0
                    )
                }
            }
        }

        if (entity is HornedHuntsmanEntity && entityData.get(ID_THROWN_BY_HUNTSMAN) &&
            (this.dealtDamage || this.isNoPhysics) && loyalty <= 0
        ) {

            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.pickupItem, 0.1f)
                }

                this.discard()
            } else {
                this.setNoPhysics(true)
                val vec3 = entity.eyePosition.subtract(this.position())

                val returnSpeed = 0.03
                this.deltaMovement = this.deltaMovement.scale(0.95).add(vec3.normalize().scale(returnSpeed))

                if (this.clientSideReturnSpearTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f)
                }

                ++this.clientSideReturnSpearTickCount
            }
        }

        super.tick()
    }

    private fun isAcceptibleReturnOwner(): Boolean {
        val entity = this.owner
        if (entity != null && entity.isAlive) {
            return entity !is ServerPlayer || !entity.isSpectator
        }
        return false
    }

    override fun findHitEntity(startVec: Vec3, endVec: Vec3): EntityHitResult? {
        return if (this.dealtDamage) null else super.findHitEntity(startVec, endVec)
    }

    override fun onHitEntity(result: EntityHitResult) {
        val target = result.entity
        var damage = BASE_DAMAGE

        if (entityData.get(ID_THROWN_BY_HUNTSMAN)) {
            damage += HUNTSMAN_DAMAGE_BONUS
        }

        val owner = this.owner
        val damageSource =
            if (owner != null) target.level().damageSources().trident(this, owner)
            else target.level().damageSources().trident(this, this)

        val level = this.level()
        if (level is ServerLevel) {
            if (target is LivingEntity) {
                damage = EnchantmentHelper.modifyDamage(level, this.weaponItem, target, damageSource, damage)
            }
        }

        this.dealtDamage = true

        if (target.hurt(damageSource, damage)) {

            if (level is ServerLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, damageSource, this.weaponItem)
            }

            if (target is LivingEntity) {
                this.doKnockback(target, damageSource)

                if (owner is HornedHuntsmanEntity) {
                    val random = owner.random
                    if (random.nextFloat() < 0.3f) {
                        val effect = when (random.nextInt(3)) {
                            0 -> MobEffects.WEAKNESS
                            1 -> MobEffects.MOVEMENT_SLOWDOWN
                            else -> MobEffects.POISON
                        }

                        target.addEffect(MobEffectInstance(effect, 120, 1))
                    }
                }

                this.doPostHurtEffects(target)
            }
        }

        this.deltaMovement = this.deltaMovement.multiply(-0.01, -0.1, -0.01)

        this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f)
    }

    override fun hitBlockEnchantmentEffects(level: ServerLevel, hitResult: BlockHitResult, stack: ItemStack) {
        val vec3 = hitResult.blockPos.clampLocationWithin(hitResult.location)

        val livingOwner = if (this.owner is LivingEntity) this.owner as LivingEntity else null

        EnchantmentHelper.onHitBlock(
            level,
            stack,
            livingOwner,
            this,
            null,
            vec3,
            level.getBlockState(hitResult.blockPos)
        ) { this.kill() }
    }

    override fun getWeaponItem(): ItemStack {
        return this.pickupItemStackOrigin
    }

    override fun tryPickup(player: Player): Boolean {
        return super.tryPickup(player) ||
                this.isNoPhysics && this.ownedBy(player) &&
                player.inventory.add(this.pickupItem)
    }

    override fun getDefaultPickupItem(): ItemStack {
        return ItemStack(WitcheryItems.HUNTSMAN_SPEAR.get())
    }

    override fun getDefaultHitGroundSoundEvent(): SoundEvent {
        return SoundEvents.TRIDENT_HIT_GROUND
    }

    override fun playerTouch(player: Player) {
        if (this.ownedBy(player) || this.owner == null) {
            super.playerTouch(player)
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.dealtDamage = compound.getBoolean("DealtDamage")
        this.thrownByBoss = compound.getBoolean("ThrownByHuntsman")

        entityData.set(ID_THROWN_BY_HUNTSMAN, thrownByBoss)
        entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.pickupItemStackOrigin))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("DealtDamage", this.dealtDamage)
        compound.putBoolean("ThrownByHuntsman", this.thrownByBoss)
    }

    private fun getLoyaltyFromItem(stack: ItemStack): Byte {
        val level = this.level()
        if (level is ServerLevel) {
            val enchantmentLoyalty = EnchantmentHelper.getTridentReturnToOwnerAcceleration(level, stack, this)

            val loyaltyValue = if (enchantmentLoyalty <= 0) 1 else enchantmentLoyalty

            return Mth.clamp(loyaltyValue, 1, 127).toByte()
        }
        return 1.toByte()
    }

    override fun tickDespawn() {
        val loyalty = this.entityData.get(ID_LOYALTY).toInt()
        if (this.pickup != Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn()
        }
    }

    override fun getWaterInertia(): Float {
        return 0.99f
    }

    override fun shouldRender(x: Double, y: Double, z: Double): Boolean {
        return true
    }
}