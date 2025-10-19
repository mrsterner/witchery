package dev.sterner.witchery.content.entity

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.mojang.logging.LogUtils
import dev.sterner.witchery.features.brewing.potion.WitcheryPotionIngredient
import dev.sterner.witchery.features.brewing.potion.WitcheryPotionItem
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryMobEffects
import dev.sterner.witchery.core.registry.WitcherySpecialPotionEffects
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.FastColor
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.HitResult
import org.slf4j.Logger
import java.util.*


class WitcheryAreaEffectCloud(
    entityType: EntityType<out WitcheryAreaEffectCloud?>,
    level: Level,
    var hitResult: HitResult?
) :
    Entity(entityType, level), TraceableEntity {

    private var potionContents: MutableList<WitcheryPotionIngredient> = mutableListOf()
    private val victims: MutableMap<Entity, Int> = Maps.newHashMap()
    var duration: Int = 600
    var waitTime: Int = 20
    private var reapplicationDelay = 20
    var durationOnUse: Int = 0
    var radiusOnUse: Float = 0f
    var radiusPerTick: Float = 0f
    private var owner: LivingEntity? = null
    private var ownerUUID: UUID? = null

    init {
        this.noPhysics = true
    }

    constructor(level: Level) : this(WitcheryEntityTypes.AREA_EFFECT_CLOUD.get(), level, null)

    constructor(level: Level, x: Double, y: Double, z: Double, hitResult: HitResult) : this(
        WitcheryEntityTypes.AREA_EFFECT_CLOUD.get(),
        level,
        hitResult
    ) {
        this.setPos(x, y, z)
        this.hitResult = hitResult
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DATA_RADIUS, 3.0f)
        builder.define(DATA_WAITING, false)
        builder.define(DATA_PARTICLE, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1))
    }

    override fun refreshDimensions() {
        val d = this.x
        val e = this.y
        val f = this.z
        super.refreshDimensions()
        this.setPos(d, e, f)
    }

    var radius: Float
        get() = getEntityData().get(DATA_RADIUS)
        set(radius) {
            if (!level().isClientSide) {
                getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0f, 32.0f))
            }
        }

    fun setPotionContents(potionContents: MutableList<WitcheryPotionIngredient>) {
        this.potionContents = potionContents
        this.updateColor()
    }

    private fun updateColor() {
        val particleOptions = entityData.get(DATA_PARTICLE)
        if (particleOptions is ColorParticleOption) {
            val i = if (this.potionContents.isEmpty()) 0 else potionContents.last().color
            entityData.set(DATA_PARTICLE, ColorParticleOption.create(particleOptions.type, FastColor.ARGB32.opaque(i)))
        }
    }

    var particle: ParticleOptions
        get() = getEntityData().get(DATA_PARTICLE)
        set(particleOption) {
            getEntityData().set(DATA_PARTICLE, particleOption)
        }

    var isWaiting: Boolean
        /**
         * Returns `true` if the cloud is waiting. While waiting, the radius is ignored and the cloud shows fewer particles in its area.
         */
        get() = getEntityData().get(DATA_WAITING)
        /**
         * Sets if the cloud is waiting. While waiting, the radius is ignored and the cloud shows fewer particles in its area.
         */
        protected set(waiting) {
            getEntityData().set(DATA_WAITING, waiting)
        }

    override fun tick() {
        super.tick()
        val bl = this.isWaiting
        var f = this.radius
        if (level().isClientSide) {
            if (bl && random.nextBoolean()) {
                return
            }

            val particleOptions = this.particle
            val i: Int
            val g: Float
            if (bl) {
                i = 2
                g = 0.2f
            } else {
                i = Mth.ceil(Math.PI.toFloat() * f * f)
                g = f
            }

            (0 until i).forEach { j ->
                val h = random.nextFloat() * (Math.PI * 2).toFloat()
                val k = Mth.sqrt(random.nextFloat()) * g
                val d = this.x + (Mth.cos(h) * k).toDouble()
                val e = this.y
                val l = this.z + (Mth.sin(h) * k).toDouble()
                if (particleOptions.type === ParticleTypes.ENTITY_EFFECT) {
                    if (bl && random.nextBoolean()) {
                        level().addAlwaysVisibleParticle(
                            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1),
                            d,
                            e,
                            l,
                            0.0,
                            0.0,
                            0.0
                        )
                    } else {
                        level().addAlwaysVisibleParticle(particleOptions, d, e, l, 0.0, 0.0, 0.0)
                    }
                } else if (bl) {
                    level().addAlwaysVisibleParticle(particleOptions, d, e, l, 0.0, 0.0, 0.0)
                } else {
                    level().addAlwaysVisibleParticle(
                        particleOptions, d, e, l,
                        (0.5 - random.nextDouble()) * 0.15, 0.01,
                        (0.5 - random.nextDouble()) * 0.15
                    )
                }
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.discard()
                return
            }

            val bl2 = this.tickCount < this.waitTime
            if (bl != bl2) {
                this.isWaiting = bl2
            }

            if (bl2) {
                return
            }

            if (this.radiusPerTick != 0.0f) {
                f += this.radiusPerTick
                if (f < 0.5f) {
                    this.discard()
                    return
                }

                this.radius = f
            }

            if (this.tickCount % 5 == 0) {
                victims.entries.removeIf { entry: Map.Entry<Entity, Int> -> this.tickCount >= entry.value }

                val list: MutableList<MobEffectInstance> = Lists.newArrayList()
                val visible = !potionContents.any {
                    it.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.NO_PARTICLE)
                }

                var shouldInvertNext = false

                for (ingredient in potionContents) {

                    val duration =
                        (ingredient.baseDuration + ingredient.effectModifier.durationAddition) * ingredient.effectModifier.durationMultiplier
                    val amplifier = ingredient.effectModifier.powerAddition

                    if (ingredient.specialEffect.isPresent) {
                        hitResult?.let { hitResult1 ->
                            WitcherySpecialPotionEffects.SPECIAL_REGISTRY.get(ingredient.specialEffect.get())
                                ?.onActivated(
                                    level(),
                                    owner,
                                    hitResult = hitResult1,
                                    victims.map { it.key }.toMutableList(),
                                    WitcheryPotionItem.getMergedDisperseModifier(potionContents),
                                    duration,
                                    amplifier
                                )
                        }
                    }

                    if (ingredient.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                        shouldInvertNext = true
                    }

                    val effect = if (shouldInvertNext) {
                        shouldInvertNext = false
                        WitcheryMobEffects.invertEffect(ingredient.effect)
                    } else {
                        ingredient.effect
                    }

                    list.add(
                        MobEffectInstance(
                            effect,
                            duration,
                            amplifier,
                            false,
                            visible
                        )
                    )
                }

                val list2 = level().getEntitiesOfClass(LivingEntity::class.java, this.boundingBox)
                if (list2.isNotEmpty()) {
                    for (livingEntity in list2) {
                        if (!victims.containsKey(livingEntity) && livingEntity.isAffectedByPotions &&
                            list.any { livingEntity.canBeAffected(it) }
                        ) {
                            val m = livingEntity.x - this.x
                            val n = livingEntity.z - this.z
                            val o = m * m + n * n
                            if (o <= (f * f).toDouble()) {
                                victims[livingEntity] = this.tickCount + this.reapplicationDelay

                                for (mobEffectInstance2 in list) {
                                    if (mobEffectInstance2.effect.value().isInstantenous) {
                                        mobEffectInstance2.effect.value().applyInstantenousEffect(
                                            this,
                                            this.getOwner(), livingEntity, mobEffectInstance2.amplifier, 0.5
                                        )
                                    } else {
                                        if (mobEffectInstance2 != WitcheryMobEffects.EMPTY) {
                                            livingEntity.addEffect(MobEffectInstance(mobEffectInstance2), this)
                                        }
                                    }
                                }

                                if (this.radiusOnUse != 0.0f) {
                                    f += this.radiusOnUse
                                    if (f < 0.5f) {
                                        this.discard()
                                        return
                                    }
                                    this.radius = f
                                }

                                if (this.durationOnUse != 0) {
                                    this.duration += this.durationOnUse
                                    if (this.duration <= 0) {
                                        this.discard()
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    fun setOwner(owner: LivingEntity?) {
        this.owner = owner
        this.ownerUUID = owner?.uuid
    }

    override fun getOwner(): LivingEntity? {
        if (this.owner == null && (this.ownerUUID != null) && level() is ServerLevel) {
            val entity = (level() as ServerLevel).getEntity(this.ownerUUID!!)
            if (entity is LivingEntity) {
                this.owner = entity
            }
        }

        return this.owner
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.tickCount = compound.getInt("Age")
        this.duration = compound.getInt("Duration")
        this.waitTime = compound.getInt("WaitTime")
        this.reapplicationDelay = compound.getInt("ReapplicationDelay")
        this.durationOnUse = compound.getInt("DurationOnUse")
        this.radiusOnUse = compound.getFloat("RadiusOnUse")
        this.radiusPerTick = compound.getFloat("RadiusPerTick")
        this.radius = compound.getFloat("Radius")
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner")
        }

        val registryOps = registryAccess().createSerializationContext(NbtOps.INSTANCE)
        if (compound.contains("Particle", 10)) {
            ParticleTypes.CODEC
                .parse(registryOps, compound["Particle"])
                .resultOrPartial { string: String? ->
                    LOGGER.warn(
                        "Failed to parse area effect cloud particle options: '{}'",
                        string
                    )
                }
                .ifPresent { particleOption: ParticleOptions ->
                    this.particle =
                        particleOption
                }
        }

        if (compound.contains("witcheryPotionItemCache", 9)) {
            val listTag = compound.getList("witcheryPotionItemCache", 10)
            val decodeResult = WitcheryPotionIngredient.CODEC.listOf().parse(NbtOps.INSTANCE, listTag)

            decodeResult.resultOrPartial { _ ->
            }?.let {
                potionContents = it.get().toMutableList()
            }
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("Age", this.tickCount)
        compound.putInt("Duration", this.duration)
        compound.putInt("WaitTime", this.waitTime)
        compound.putInt("ReapplicationDelay", this.reapplicationDelay)
        compound.putInt("DurationOnUse", this.durationOnUse)
        compound.putFloat("RadiusOnUse", this.radiusOnUse)
        compound.putFloat("RadiusPerTick", this.radiusPerTick)
        compound.putFloat("Radius", this.radius)
        val registryOps = registryAccess().createSerializationContext(NbtOps.INSTANCE)
        compound.put(
            "Particle", ParticleTypes.CODEC.encodeStart(
                registryOps,
                particle
            ).getOrThrow()
        )
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID!!)
        }

        val listResult = WitcheryPotionIngredient.CODEC.listOf().encodeStart(NbtOps.INSTANCE, potionContents)
        listResult.resultOrPartial { _ -> }?.let { compound.put("witcheryPotionItemCache", it.get()) }
    }

    override fun onSyncedDataUpdated(dataAccessor: EntityDataAccessor<*>) {
        if (DATA_RADIUS == dataAccessor) {
            this.refreshDimensions()
        }

        super.onSyncedDataUpdated(dataAccessor)
    }

    override fun getPistonPushReaction(): PushReaction {
        return PushReaction.IGNORE
    }

    override fun getDimensions(pose: Pose): EntityDimensions {
        return EntityDimensions.scalable(this.radius * 2.0f, 0.5f)
    }

    companion object {
        private val LOGGER: Logger = LogUtils.getLogger()
        private val DATA_RADIUS: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            WitcheryAreaEffectCloud::class.java, EntityDataSerializers.FLOAT
        )
        private val DATA_WAITING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WitcheryAreaEffectCloud::class.java, EntityDataSerializers.BOOLEAN
        )
        private val DATA_PARTICLE: EntityDataAccessor<ParticleOptions> = SynchedEntityData.defineId(
            WitcheryAreaEffectCloud::class.java, EntityDataSerializers.PARTICLE
        )
    }
}
