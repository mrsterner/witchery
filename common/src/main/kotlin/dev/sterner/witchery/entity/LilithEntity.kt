package dev.sterner.witchery.entity

import dev.sterner.witchery.handler.vampire.VampireLeveling
import dev.sterner.witchery.payload.SpawnSmokeParticlesS2CPayload
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.BossEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class LilithEntity(level: Level) : Monster(WitcheryEntityTypes.LILITH.get(), level) {

    var onceOnDefeat = true

    var hasUsedLilith = false
    var despawnTicks = 0

    private val bossEvent = ServerBossEvent(
        this.displayName, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
    )
        .setDarkenScreen(true) as ServerBossEvent

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, if (entityData.get(IS_DEFEATED)) 0.5 else 1.0))
        goalSelector.addGoal(
            2, LookAtPlayerGoal(
                this,
                Player::class.java, 15.0f, 1.0f
            )
        )
        //targetSelector.addGoal(0, HurtByTargetGoal(this))
        targetSelector.addGoal(1, TargetWhenNotDefeated(this, LivingEntity::class.java))
        super.registerGoals()
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(IS_DEFEATED, true)
        super.defineSynchedData(builder)
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        if (player is ServerPlayer && hand == InteractionHand.MAIN_HAND) {
            if (VampirePlayerAttachment.getData(player).getVampireLevel() == 6) {
                if (player.mainHandItem.`is`(Items.POPPY)) {
                    VampireLeveling.givePoppy(player)
                    hasUsedLilith = true
                    return InteractionResult.SUCCESS
                }
            }
        }

        return super.mobInteract(player, hand)
    }

    override fun canUsePortal(allowPassengers: Boolean): Boolean {
        return false
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun checkFallDamage(y: Double, onGround: Boolean, state: BlockState, pos: BlockPos) {

    }

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource): Boolean {
        return false
    }

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return super.isInvulnerableTo(source) || entityData.get(IS_DEFEATED)
    }

    override fun baseTick() {
        super.baseTick()

        if (hasUsedLilith) {
            despawnTicks++
            if (despawnTicks > 20 * 5) {
                WitcheryPayloads.sendToPlayers(level(), SpawnSmokeParticlesS2CPayload(position()))
                discard()
            }
        }

        if (health / maxHealth <= 0.05) {
            entityData.set(IS_DEFEATED, true)
        }

        if (!entityData.get(IS_DEFEATED)) {
            if (level().gameTime % 20 == 0L) {
                heal(5f)
            }
        } else {
            if (onceOnDefeat) {
                onceOnDefeat = false
                WitcheryPayloads.sendToPlayers(level(), SpawnSmokeParticlesS2CPayload(position()))
            }
        }
    }

    public override fun getDefaultDimensions(pose: Pose): EntityDimensions {
        return super.getDefaultDimensions(pose).scale(if (entityData.get(IS_DEFEATED)) 0.75f else 1f)
    }

    override fun onSyncedDataUpdated(dataAccessor: EntityDataAccessor<*>) {
        if (IS_DEFEATED == dataAccessor) {
            this.refreshDimensions()
        }

        super.onSyncedDataUpdated(dataAccessor)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (health == 1f) {
            return false
        }
        if (amount > health) {
            health = 1f
            return false
        }

        return super.hurt(source, amount)
    }

    override fun customServerAiStep() {
        super.customServerAiStep()
        if (entityData.get(IS_DEFEATED)) {
            bossEvent.removeAllPlayers()
        } else {
            bossEvent.progress = this.health / this.maxHealth
        }
    }

    override fun startSeenByPlayer(serverPlayer: ServerPlayer) {
        super.startSeenByPlayer(serverPlayer)
        if (!entityData.get(IS_DEFEATED)) {
            bossEvent.addPlayer(serverPlayer)
        }
    }

    override fun stopSeenByPlayer(serverPlayer: ServerPlayer) {
        super.stopSeenByPlayer(serverPlayer)
        bossEvent.removePlayer(serverPlayer)
    }

    override fun setCustomName(name: Component?) {
        super.setCustomName(name)
        bossEvent.name = this.displayName
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("IsDefeated", entityData.get(IS_DEFEATED))
        compound.putBoolean("HasUsedLilith", hasUsedLilith)
        compound.putInt("DespawnTicks", despawnTicks)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        entityData.set(IS_DEFEATED, compound.getBoolean("IsDefeated"))
        hasUsedLilith = compound.getBoolean("HasUsedLilith")
        despawnTicks = compound.getInt("DespawnTicks")
        if (this.hasCustomName()) {
            bossEvent.name = this.displayName
        }
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val IS_DEFEATED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            LilithEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }

    class TargetWhenNotDefeated(
        val lilithEntity: LilithEntity,
        targetType: Class<LivingEntity>
    ) : NearestAttackableTargetGoal<LivingEntity>(lilithEntity, targetType, 10, true, true, { it is Player }) {

        override fun canUse(): Boolean {
            return super.canUse() && !lilithEntity.entityData.get(IS_DEFEATED)
        }

        override fun canContinueToUse(): Boolean {
            return super.canContinueToUse() && !lilithEntity.entityData.get(IS_DEFEATED)
        }
    }
}