package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.entity.goal.FocusSummonerGoal
import dev.sterner.witchery.content.entity.goal.PrioritizeSetTargetGoal
import dev.sterner.witchery.content.entity.goal.ScytheThrowGoal
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.BossEvent
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DeathEntity(level: Level) : Monster(WitcheryEntityTypes.DEATH.get(), level) {

    companion object {
        private val DATA_SUMMONER_UUID: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId(DeathEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
        private val DATA_PHASE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(DeathEntity::class.java, EntityDataSerializers.INT)

        const val PHASE_NORMAL = 0
        const val PHASE_ENRAGED = 1
        const val PHASE_DESPERATE = 2

        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.ARMOR, 10.0)
        }
    }

    private val bossEvent = ServerBossEvent(
        Component.translatable("entity.witchery.death"),
        BossEvent.BossBarColor.RED,
        BossEvent.BossBarOverlay.PROGRESS
    )

    private val focusSummonerGoal = FocusSummonerGoal(this, 20 * 60)

    private var teleportCooldown = 0
    var hasForcedTarget: Boolean = false

    private var scytheThrowCooldown = 0
    private var meleeCooldown = 0

    private var lastPhase = PHASE_NORMAL

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_SUMMONER_UUID, Optional.empty())
        builder.define(DATA_PHASE, PHASE_NORMAL)
    }

    fun getSummonerUUID(): UUID? = entityData.get(DATA_SUMMONER_UUID).orElse(null)

    private fun setSummonerUUID(uuid: UUID?) {
        entityData.set(DATA_SUMMONER_UUID, Optional.ofNullable(uuid))
    }

    fun getPhase(): Int = entityData.get(DATA_PHASE)

    private fun setPhase(phase: Int) {
        entityData.set(DATA_PHASE, phase)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, ScytheThrowGoal(this, 20.0, 100, 60))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Player::class.java, 8.0f))

        targetSelector.addGoal(0, focusSummonerGoal)
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    fun setForcedTarget(target: LivingEntity) {
        this.target = target
        this.hasForcedTarget = true
        setSummonerUUID(target.uuid)
        focusSummonerGoal.setSummoner(target)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnType: MobSpawnType,
        spawnGroupData: SpawnGroupData?
    ): SpawnGroupData? {
        this.populateDefaultEquipmentSlots(level.random, difficulty)
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData)
    }

    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(WitcheryItems.DEATH_SICKLE.get()))
    }

    override fun tick() {
        super.tick()

        if (level().gameTime % 20 == 0L) {
            if (health < maxHealth) {
                heal(1f)
            }
        }

        updatePhase()

        if (teleportCooldown > 0) {
            teleportCooldown--
        } else {
            val target = target
            if (target is LivingEntity && target.isAlive && random.nextFloat() < getTeleportChance()) {
                attemptTeleportNearTarget(target)
            }
        }

        updateBossBar()

        if (scytheThrowCooldown > 0) scytheThrowCooldown--
        if (meleeCooldown > 0) meleeCooldown--

        when (getPhase()) {
            PHASE_ENRAGED -> {
                if (random.nextFloat() < 0.1f && level() is ServerLevel) {
                    (level() as ServerLevel).sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        x, y + 1.0, z,
                        2, 0.3, 0.5, 0.3, 0.02
                    )
                }
            }
            PHASE_DESPERATE -> {
                if (random.nextFloat() < 0.2f && level() is ServerLevel) {
                    (level() as ServerLevel).sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        x, y + 1.0, z,
                        3, 0.4, 0.5, 0.4, 0.05
                    )
                }
            }
        }
    }

    private fun updatePhase() {
        val healthPercent = health / maxHealth
        val newPhase = when {
            healthPercent <= 0.25f -> PHASE_DESPERATE
            healthPercent <= 0.50f -> PHASE_ENRAGED
            else -> PHASE_NORMAL
        }

        if (newPhase != lastPhase) {
            onPhaseChange(lastPhase, newPhase)
            lastPhase = newPhase
            setPhase(newPhase)
        }
    }

    private fun onPhaseChange(oldPhase: Int, newPhase: Int) {
        if (level() is ServerLevel) {
            val serverLevel = level() as ServerLevel

            when (newPhase) {
                PHASE_ENRAGED -> {
                    serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        x, y + 1.0, z,
                        50, 1.0, 1.0, 1.0, 0.1
                    )
                    level().playSound(
                        null, blockPosition(),
                        SoundEvents.WITHER_AMBIENT,
                        SoundSource.HOSTILE,
                        2.0f, 0.5f
                    )
                    bossEvent.color = BossEvent.BossBarColor.YELLOW
                }
                PHASE_DESPERATE -> {
                    serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        x, y + 1.0, z,
                        100, 1.5, 1.5, 1.5, 0.15
                    )
                    level().playSound(
                        null, blockPosition(),
                        SoundEvents.WITHER_SPAWN,
                        SoundSource.HOSTILE,
                        2.0f, 0.3f
                    )
                    bossEvent.color = BossEvent.BossBarColor.RED
                }
            }
        }
    }

    private fun getTeleportChance(): Float {
        return when (getPhase()) {
            PHASE_DESPERATE -> 0.10f
            PHASE_ENRAGED -> 0.07f
            else -> 0.05f
        }
    }

    private fun getTeleportCooldown(): Int {
        return when (getPhase()) {
            PHASE_DESPERATE -> 20 * 5
            PHASE_ENRAGED -> 20 * 8
            else -> 20 * 10
        }
    }

    private fun attemptTeleportNearTarget(target: LivingEntity) {
        if (level() !is ServerLevel) return
        val random = RandomSource.create()

        for (i in 0 until 16) {
            val angle = random.nextDouble() * Math.PI * 2
            val distance = 2.0 + random.nextDouble() * 2.0
            val x = target.x + cos(angle) * distance
            val z = target.z + sin(angle) * distance
            val y = target.y + random.nextDouble() * 2.0 - 1.0

            if (teleportDeathTo(x, y, z)) {
                if (level() is ServerLevel) {
                    val serverLevel = level() as ServerLevel

                    serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        xo, yo + bbHeight / 2, zo,
                        30, 0.5, 0.5, 0.5, 0.5
                    )

                    serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        x, y + bbHeight / 2, z,
                        30, 0.5, 0.5, 0.5, 0.5
                    )
                }

                level().playSound(null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f)
                level().playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f)

                teleportCooldown = getTeleportCooldown() + random.nextInt(20 * 5)
                break
            }
        }
    }

    private fun teleportDeathTo(x: Double, y: Double, z: Double): Boolean {
        val serverLevel = level() as? ServerLevel ?: return false

        val blockPos = blockPosition().offset(
            Mth.floor(x - this.x),
            Mth.floor(y - this.y),
            Mth.floor(z - this.z)
        )

        if (!serverLevel.isEmptyBlock(blockPos) || !serverLevel.isEmptyBlock(blockPos.above())) {
            return false
        }

        teleportTo(x, y, z)
        return true
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (meleeCooldown > 0) return false

        if (target is LivingEntity) {
            val damage = target.maxHealth * 0.15f
            val result = target.hurt(level().damageSources().mobAttack(this), damage)

            if (result) {
                meleeCooldown = 10

                if (level() is ServerLevel) {
                    (level() as ServerLevel).sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        target.x, target.y + target.bbHeight / 2, target.z,
                        1, 0.0, 0.0, 0.0, 0.0
                    )
                }

                level().playSound(
                    null, target.blockPosition(),
                    SoundEvents.PLAYER_ATTACK_STRONG,
                    SoundSource.HOSTILE,
                    1.0f, 0.8f
                )
            }

            return result
        }
        return super.doHurtTarget(target)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val cappedDamage = min(amount, 16.0f)

        val result = super.hurt(source, cappedDamage)

        if (result && level() is ServerLevel) {
            (level() as ServerLevel).sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                x, y + bbHeight / 2, z,
                (cappedDamage / 2).toInt().coerceAtLeast(1),
                0.3, 0.3, 0.3, 0.0
            )
        }

        return result
    }

    fun throwScythe(target: LivingEntity) {
        if (scytheThrowCooldown > 0) return

        val scythe = ScytheThrownEntity(level(), this)

        val dx = target.x - x
        val dy = target.eyeY - eyeY
        val dz = target.z - z
        val horizontalDistance = kotlin.math.sqrt(dx * dx + dz * dz)

        val velocity = Vec3(dx, dy + horizontalDistance * 0.1, dz).normalize().scale(1.5)
        scythe.setDeltaMovement(velocity)

        level().addFreshEntity(scythe)

        level().playSound(
            null, blockPosition(),
            SoundEvents.TRIDENT_THROW.value(),
            SoundSource.HOSTILE,
            1.0f, 0.8f
        )

        scytheThrowCooldown = when (getPhase()) {
            PHASE_DESPERATE -> 20 * 3
            PHASE_ENRAGED -> 20 * 4
            else -> 20 * 5
        }
    }

    fun canThrowScythe(): Boolean = scytheThrowCooldown <= 0

    private fun updateBossBar() {
        bossEvent.progress = health / maxHealth

        if (level() is ServerLevel) {
            val nearbyPlayers = level().getEntitiesOfClass(
                ServerPlayer::class.java,
                boundingBox.inflate(64.0)
            )

            for (player in nearbyPlayers) {
                bossEvent.addPlayer(player)
            }

            for (player in bossEvent.players.toList()) {
                if (player.distanceToSqr(this) > 64.0 * 64.0) {
                    bossEvent.removePlayer(player)
                }
            }
        }
    }

    override fun startSeenByPlayer(player: ServerPlayer) {
        super.startSeenByPlayer(player)
        bossEvent.addPlayer(player)
    }

    override fun stopSeenByPlayer(player: ServerPlayer) {
        super.stopSeenByPlayer(player)
        bossEvent.removePlayer(player)
    }

    override fun die(damageSource: DamageSource) {
        super.die(damageSource)

        if (level() is ServerLevel) {
            val serverLevel = level() as ServerLevel

            serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                x, y + bbHeight / 2, z,
                100, 1.5, 1.5, 1.5, 0.2
            )

            serverLevel.sendParticles(
                ParticleTypes.LARGE_SMOKE,
                x, y + bbHeight / 2, z,
                50, 1.0, 1.0, 1.0, 0.1
            )

            level().playSound(
                null, blockPosition(),
                SoundEvents.WITHER_DEATH,
                SoundSource.HOSTILE,
                2.0f, 0.5f
            )
        }

        bossEvent.removeAllPlayers()
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.hasUUID("SummonerUUID")) {
            setSummonerUUID(compound.getUUID("SummonerUUID"))
        }

        hasForcedTarget = compound.getBoolean("HasForcedTarget")
        teleportCooldown = compound.getInt("TeleportCooldown")
        scytheThrowCooldown = compound.getInt("ScytheThrowCooldown")
        setPhase(compound.getInt("Phase"))
        lastPhase = getPhase()

        if (bossEvent != null) {
            bossEvent.name = customName ?: Component.translatable("entity.witchery.death")
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)

        getSummonerUUID()?.let { compound.putUUID("SummonerUUID", it) }
        compound.putBoolean("HasForcedTarget", hasForcedTarget)
        compound.putInt("TeleportCooldown", teleportCooldown)
        compound.putInt("ScytheThrowCooldown", scytheThrowCooldown)
        compound.putInt("Phase", getPhase())
    }

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean {
        return false
    }

    override fun isPersistenceRequired(): Boolean = true
}