package dev.sterner.witchery.api.block

import dev.sterner.witchery.data.InfiniteCenserReloadListener
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

object PotionDisperserHelper {

    private fun processRegularPotion(disperser: PotionDisperser, potion: PotionContents) {
        for (effect in potion.customEffects()) {
            val rl = BuiltInRegistries.MOB_EFFECT.getKey(effect.effect.value()) ?: continue
            if (isEmptyEffect(rl)) continue

            val isInfinite = disperser.isInfiniteMode() &&
                    InfiniteCenserReloadListener.INFINITE_POTIONS.contains(effect.effect)
            val remainingTicks = if (isInfinite) -1 else effect.duration

            disperser.getActiveEffects() += ActiveEffect(
                rl,
                false,
                effect.amplifier,
                remainingTicks,
                effect.duration,
                WitcheryPotionIngredient.DispersalModifier(),
                0L
            )
        }

        potion.potion.ifPresent { potionHolder ->
            potionHolder.value().effects.forEach { effect ->
                val rl = BuiltInRegistries.MOB_EFFECT.getKey(effect.effect.value()) ?: return@forEach
                if (isEmptyEffect(rl)) return@forEach

                val isInfinite = disperser.isInfiniteMode() &&
                        InfiniteCenserReloadListener.INFINITE_POTIONS.contains(effect.effect)
                val remainingTicks = if (isInfinite) -1 else effect.duration

                disperser.getActiveEffects() += ActiveEffect(
                    rl, false, effect.amplifier, remainingTicks, effect.duration
                )
            }
        }
    }

    private fun processSpecialPotions(disperser: PotionDisperser) {
        val specialPotions = disperser.getSpecialPotions()
        if (specialPotions.isEmpty()) return

        val compoundPower = specialPotions.maxOfOrNull { it.effectModifier.powerAddition } ?: 0
        val compoundDurationAdd = specialPotions.maxOfOrNull { it.effectModifier.durationAddition } ?: 0
        val compoundDurationMult = specialPotions.maxOfOrNull { it.effectModifier.durationMultiplier } ?: 1

        for (special in specialPotions) {
            special.specialEffect.ifPresent { rl ->
                if (isEmptyEffect(rl)) return@ifPresent

                val baseDuration = (special.baseDuration + compoundDurationAdd) * compoundDurationMult
                val isInfinite = disperser.isInfiniteMode() &&
                        InfiniteCenserReloadListener.INFINITE_SPECIAL_POTIONS.contains(rl)
                val remainingTicks = if (isInfinite) -1 else baseDuration

                disperser.getActiveEffects() += ActiveEffect(
                    rl, true, compoundPower, remainingTicks, baseDuration, special.dispersalModifier, 0L
                )
            }

            if (special.effect != WitcheryMobEffects.EMPTY) {
                val rl = BuiltInRegistries.MOB_EFFECT.getKey(special.effect.value()) ?: continue
                if (isEmptyEffect(rl)) continue

                val baseDuration = (special.baseDuration + compoundDurationAdd) * compoundDurationMult
                val isInfinite = disperser.isInfiniteMode() &&
                        InfiniteCenserReloadListener.INFINITE_POTIONS.contains(special.effect)
                val remainingTicks = if (isInfinite) -1 else baseDuration

                disperser.getActiveEffects() += ActiveEffect(
                    rl, false, compoundPower, remainingTicks, baseDuration, special.dispersalModifier
                )
            }
        }
    }

    private fun isEmptyEffect(rl: ResourceLocation): Boolean {
        return rl.path == "empty" && rl.namespace == "witchery"
    }

    fun applyEffects(
        disperser: PotionDisperser,
        level: Level,
        pos: BlockPos,
        effect: ActiveEffect
    ) {
        val radius = disperser.getDispersalRadius()
        val aabb = AABB.ofSize(pos.center, radius * 2, radius * 2, radius * 2)

        val mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(effect.id).orElse(null)
        if (mobEffect != null) {
            level.getEntitiesOfClass(LivingEntity::class.java, aabb).forEach { entity ->
                val effectDuration = if (effect.remainingTicks == -1) {
                    effect.originalDuration
                } else {
                    effect.remainingTicks
                }
                entity.addEffect(MobEffectInstance(mobEffect, effectDuration, effect.amplifier, true, true))
            }
        }

        if (effect.isSpecial) {
            val specialEffect = WitcherySpecialPotionEffects.SPECIAL_REGISTRY[effect.id]
            if (specialEffect != null) {
                val entities = level.getEntitiesOfClass(Entity::class.java, aabb)
                val owner = disperser.getOwner()
                    .flatMap { Optional.ofNullable(level.getPlayerByUUID(it)) }
                    .orElse(null)

                val duration = if (effect.remainingTicks == -1) {
                    effect.originalDuration
                } else {
                    effect.remainingTicks
                }

                val hitResult = net.minecraft.world.phys.BlockHitResult(
                    pos.center,
                    net.minecraft.core.Direction.UP,
                    pos,
                    false
                )

                specialEffect.onActivated(
                    level, owner, hitResult, entities,
                    effect.dispersal, duration, effect.amplifier
                )
            }
        }
    }

    fun calculatePotionColor(disperser: PotionDisperser): Int {
        val potionContents = disperser.getPotionContents()
        if (potionContents.isNotEmpty() && potionContents[0] != PotionContents.EMPTY) {
            return potionContents[0].color
        }

        val specialPotions = disperser.getSpecialPotions()
        if (specialPotions.isNotEmpty()) {
            var r = 0
            var g = 0
            var b = 0
            var count = 0

            specialPotions.forEach { potion ->
                if (potion.color != 0) {
                    r += (potion.color shr 16) and 0xFF
                    g += (potion.color shr 8) and 0xFF
                    b += potion.color and 0xFF
                    count++
                }
            }

            return if (count > 0) {
                ((r / count) shl 16) or ((g / count) shl 8) or (b / count)
            } else {
                0x7B2FBE
            }
        }

        return disperser.getActiveEffects().firstOrNull()?.let { effect ->
            val mobEffect = BuiltInRegistries.MOB_EFFECT.get(effect.id)
            mobEffect?.color ?: 0x7B2FBE
        } ?: 0x7B2FBE
    }

    fun spawnPotionParticles(
        level: Level,
        pos: BlockPos,
        disperser: PotionDisperser,
        density: Float = 0.2f
    ) {
        if (disperser.getActiveEffects().isEmpty()) return
        if (level.random.nextFloat() >= density) return

        val color = calculatePotionColor(disperser)
        val centerX = pos.x + 0.5
        val centerY = pos.y + 0.5
        val centerZ = pos.z + 0.5

        val angle = level.random.nextDouble() * Math.PI * 2
        val radius = 0.3 + level.random.nextDouble() * 0.2
        val particleX = centerX + Math.cos(angle) * radius
        val particleZ = centerZ + Math.sin(angle) * radius

        level.addParticle(
            DustParticleOptions(Vec3.fromRGB24(color).toVector3f(), 0.8f),
            particleX,
            centerY + level.random.nextDouble() * 0.3,
            particleZ,
            0.0, 0.01, 0.0
        )
    }

    fun savePotionData(tag: CompoundTag, disperser: PotionDisperser, level: Level) {
        val potionList = ListTag()
        for (potion in disperser.getPotionContents()) {
            if (potion != PotionContents.EMPTY) {
                val registryOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE)
                PotionContents.CODEC.encodeStart(registryOps, potion)
                    .resultOrPartial { }
                    .ifPresent { potionList.add(it) }
            }
        }
        if (!potionList.isEmpty()) {
            tag.put("PotionContents", potionList)
        }

        val specialPotions = disperser.getSpecialPotions()
        if (specialPotions.isNotEmpty()) {
            val encodeResult = WitcheryPotionIngredient.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, specialPotions)
            encodeResult.resultOrPartial { error ->
            }.ifPresent {
                tag.put("SpecialPotions", it)
            }
        }

        val effectsList = ListTag()
        for (effect in disperser.getActiveEffects()) {
            val effectTag = CompoundTag()
            effectTag.putString("Id", effect.id.toString())
            effectTag.putBoolean("IsSpecial", effect.isSpecial)
            effectTag.putInt("Remaining", effect.remainingTicks)
            effectTag.putInt("Original", effect.originalDuration)
            effectTag.putInt("Amplifier", effect.amplifier)
            effectTag.putLong("LastActivation", effect.lastSpecialActivation)
            effectsList.add(effectTag)
        }
        tag.put("ActiveEffects", effectsList)

        disperser.getOwner().ifPresent { tag.putUUID("Owner", it) }
        tag.putBoolean("InfiniteMode", disperser.isInfiniteMode())
    }

    fun loadPotionData(tag: CompoundTag, disperser: PotionDisperser, level: Level) {
        val potionContents = mutableListOf<PotionContents>()
        if (tag.contains("PotionContents")) {
            val potionList = tag.getList("PotionContents", Tag.TAG_COMPOUND.toInt())
            val registryOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE)
            for (i in 0 until potionList.size) {
                PotionContents.CODEC.parse(registryOps, potionList[i])
                    .resultOrPartial { }
                    .ifPresent { potionContents.add(it) }
            }
        }
        disperser.setPotionContents(potionContents)

        if (tag.contains("SpecialPotions")) {
            val decodeResult = WitcheryPotionIngredient.CODEC.listOf()
                .parse(NbtOps.INSTANCE, tag.get("SpecialPotions")!!)
            decodeResult.resultOrPartial { error ->
            }.ifPresent {
                disperser.setSpecialPotions(it)
            }
        }

        disperser.getActiveEffects().clear()
        if (tag.contains("ActiveEffects")) {
            val effectsList = tag.getList("ActiveEffects", Tag.TAG_COMPOUND.toInt())
            for (i in 0 until effectsList.size) {
                val effectTag = effectsList.getCompound(i)
                val rl = ResourceLocation.parse(effectTag.getString("Id"))
                val isSpecial = effectTag.getBoolean("IsSpecial")
                val remaining = effectTag.getInt("Remaining")
                val original = effectTag.getInt("Original")
                val amplifier = effectTag.getInt("Amplifier")
                val lastActivation = effectTag.getLong("LastActivation")

                disperser.getActiveEffects() += ActiveEffect(
                    rl,
                    isSpecial,
                    amplifier,
                    remaining,
                    original,
                    WitcheryPotionIngredient.DispersalModifier(),
                    lastActivation
                )
            }
        }

        if (tag.contains("Owner")) {
            disperser.setOwner(Optional.of(tag.getUUID("Owner")))
        }
        disperser.setInfiniteMode(tag.getBoolean("InfiniteMode"))

    }

    fun refreshActiveEffects(disperser: PotionDisperser) {
        disperser.getActiveEffects().clear()
        for (potion in disperser.getPotionContents()) {
            processRegularPotion(disperser, potion)
        }

        processSpecialPotions(disperser)
    }
}