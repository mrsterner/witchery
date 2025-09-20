package dev.sterner.witchery.block.censer

import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import dev.sterner.witchery.entity.WitcheryThrownPotion
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import dev.sterner.witchery.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID

class CenserBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.CENSER.get(), blockPos, blockState), AltarPowerConsumer {

    var potionContents: List<PotionContents> = listOf(PotionContents.EMPTY)
    var specialPotions: List<WitcheryPotionIngredient> = listOf()
    val activeEffects: MutableList<ActiveEffect> = mutableListOf()
    val owner: Optional<UUID> = Optional.empty()

    private var cachedAltarPos: BlockPos? = null

    override fun onUseWithItem(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult {
        when {
            pStack.`is`(Items.POTION) || pStack.`is`(Items.SPLASH_POTION) || pStack.`is`(Items.LINGERING_POTION) -> {
                pStack.get(DataComponents.POTION_CONTENTS)?.takeIf { it != PotionContents.EMPTY }?.let {
                    potionContents = listOf(it)
                }
            }
            pStack.has(WITCHERY_POTION_CONTENT.get()) -> {
                specialPotions = pStack.get(WITCHERY_POTION_CONTENT.get())!!
            }
        }

        if (cachedAltarPos == null && level is ServerLevel) {

            cachedAltarPos = getAltarPos(level as ServerLevel, blockPos)
            setChanged()
        }

        refreshActiveEffects()
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    private fun consumeAltarPower(level: Level): Boolean {
        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }

        val requiredAltarPower = 2
        if (cachedAltarPos != null) {
            return tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, false)
        }
        return false
    }

    private fun refreshActiveEffects() {
        activeEffects.clear()

        for (potion in potionContents) {
            for (effect in potion.customEffects()) {
                val rl = BuiltInRegistries.MOB_EFFECT.getKey(effect.effect.value()) ?: continue
                if (rl.path == "empty" && rl.namespace == "witchery") continue

                val isEffectInfinite = InfiniteCenserReloadListener.INFINITE_POTIONS.contains(effect.effect)
                val remainingTicks = if (isEffectInfinite) -1 else effect.duration
                val originalDuration = effect.duration

                activeEffects += ActiveEffect(rl, false, effect.amplifier, remainingTicks, originalDuration)
            }

            potion.potion.ifPresent { potionHolder ->
                potionHolder.value().effects.forEach { effect ->
                    val rl = BuiltInRegistries.MOB_EFFECT.getKey(effect.effect.value()) ?: return@forEach
                    if (rl.path == "empty" && rl.namespace == "witchery") return@forEach

                    val isEffectInfinite = InfiniteCenserReloadListener.INFINITE_POTIONS.contains(effect.effect)
                    val remainingTicks = if (isEffectInfinite) -1 else effect.duration
                    val originalDuration = effect.duration

                    activeEffects += ActiveEffect(rl, false, effect.amplifier, remainingTicks, originalDuration)
                }
            }
        }

        val compoundPower: Int = specialPotions.maxOfOrNull { it.effectModifier.powerAddition } ?: 0
        val compoundDurationAdd: Int = specialPotions.maxOfOrNull { it.effectModifier.durationAddition } ?: 0
        val compoundDurationMult: Int = specialPotions.maxOfOrNull { it.effectModifier.durationMultiplier } ?: 1

        for (special in specialPotions) {
            special.specialEffect.ifPresent { rl ->
                if (rl.path == "empty" && rl.namespace == "witchery") return@ifPresent

                val baseDuration = (special.baseDuration + compoundDurationAdd) * compoundDurationMult
                val isInfinite = InfiniteCenserReloadListener.INFINITE_SPECIAL_POTIONS.contains(rl)
                val remainingTicks = if (isInfinite) -1 else baseDuration

                activeEffects += ActiveEffect(rl, true, compoundPower, remainingTicks, baseDuration, special.dispersalModifier)
            }

            if (special.effect != WitcheryMobEffects.EMPTY) {
                val rl = BuiltInRegistries.MOB_EFFECT.getKey(special.effect.value()) ?: continue
                if (rl.path == "empty" && rl.namespace == "witchery") continue

                val baseDuration = (special.baseDuration + compoundDurationAdd) * compoundDurationMult
                val isEffectInfinite = InfiniteCenserReloadListener.INFINITE_POTIONS.contains(special.effect)
                val remainingTicks = if (isEffectInfinite) -1 else baseDuration

                activeEffects += ActiveEffect(rl, false, compoundPower, remainingTicks, baseDuration, special.dispersalModifier)
            }
        }

        setChanged()
    }

    private fun runEffect(level: Level, pos: BlockPos, effect: ActiveEffect) {
        val hitResult = BlockHitResult(blockPos.center, Direction.UP, pos, false)

        val mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(effect.id).orElse(null)
        if (mobEffect != null) {
            val aABB = AABB.ofSize(blockPos.center, 16.0, 16.0, 16.0)
            this.level!!.getEntitiesOfClass(LivingEntity::class.java, aABB).forEach { entity ->
                val effectDuration = if (effect.remainingTicks == -1) {
                    effect.originalDuration
                } else {
                    effect.remainingTicks
                }

                entity.addEffect(MobEffectInstance(mobEffect, effectDuration, effect.amplifier, true, true))
            }
        } else {
            val specialEffect = WitcherySpecialPotionEffects.SPECIALS[effect.id]
            if (specialEffect != null) {
                val scaleBonus = WitcheryThrownPotion.getRangeBonus(specialPotions)
                val aABB = AABB.ofSize(blockPos.center, 16.0 * scaleBonus, 16.0 * scaleBonus, 16.0 * scaleBonus)
                val list = this.level!!.getEntitiesOfClass(Entity::class.java, aABB)

                val duration = if (effect.remainingTicks == -1) {
                    effect.originalDuration
                } else {
                    effect.remainingTicks
                }

                specialEffect.onActivated(
                    level,
                    owner.flatMap { Optional.ofNullable(level.getPlayerByUUID(it)) }.orElse(null),
                    hitResult,
                    list,
                    effect.disp,
                    duration,
                    effect.amplifier
                )
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, holder: HolderLookup.Provider) {
        super.saveAdditional(tag, holder)

        val potionList = ListTag()
        for (potion in potionContents) {
            if (potion != PotionContents.EMPTY) {
                val potionTag = CompoundTag()

                potion.potion.ifPresent { potionHolder ->
                    val potionKey = BuiltInRegistries.POTION.getKey(potionHolder.value())
                    potionTag.putString("Potion", potionKey.toString())
                }

                potion.customColor.ifPresent { color ->
                    potionTag.putInt("CustomColor", color)
                }

                if (potion.customEffects.isNotEmpty()) {
                    val effectsList = ListTag()
                    for (effect in potion.customEffects) {
                        effectsList.add(effect.save())
                    }
                    potionTag.put("CustomEffects", effectsList)
                }

                potionList.add(potionTag)
            }
        }
        if (!potionList.isEmpty()) {
            tag.put("PotionContents", potionList)
        }

        val specialList = ListTag()
        for (special in specialPotions) {
            val specialTag = CompoundTag()

            specialTag.put("Item", special.item.save(holder))

            val effectKey = BuiltInRegistries.MOB_EFFECT.getKey(special.effect.value())
            if (effectKey != null) {
                specialTag.putString("Effect", effectKey.toString())
            }

            special.specialEffect.ifPresent { rl ->
                specialTag.putString("SpecialEffect", rl.toString())
            }

            specialTag.putInt("BaseDuration", special.baseDuration)
            specialTag.putInt("AltarPower", special.altarPower)
            specialTag.putInt("CapacityCost", special.capacityCost)
            specialTag.putInt("Color", special.color)

            specialTag.putInt("PowerAdd", special.effectModifier.powerAddition)
            specialTag.putInt("DurationAdd", special.effectModifier.durationAddition)
            specialTag.putInt("DurationMult", special.effectModifier.durationMultiplier)
            specialTag.putInt("Range", special.dispersalModifier.rangeModifier)
            specialTag.putInt("LingeringDuration", special.dispersalModifier.lingeringDurationModifier)

            specialList.add(specialTag)
        }
        if (!specialList.isEmpty()) {
            tag.put("SpecialPotions", specialList)
        }

        val listTag = ListTag()
        for (effect in activeEffects) {
            val effectTag = CompoundTag()
            effectTag.putString("Id", effect.id.toString())
            effectTag.putBoolean("IsSpecial", effect.isSpecial)
            effectTag.putInt("Remaining", effect.remainingTicks)
            effectTag.putInt("Original", effect.originalDuration)
            effectTag.putInt("Amplifier", effect.amplifier)
            effectTag.putInt("Range", effect.disp.rangeModifier)
            listTag.add(effectTag)
        }
        tag.put("ActiveEffects", listTag)

        if (owner.isPresent) {
            tag.putUUID("Owner", owner.get())
        }
        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        if (pTag.contains("altarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }
        potionContents = mutableListOf()
        if (pTag.contains("PotionContents")) {
            val potionList = pTag.getList("PotionContents", Tag.TAG_COMPOUND.toInt())
            for (i in 0 until potionList.size) {
                val potionTag = potionList.getCompound(i)

                val potionHolder: Optional<Holder<Potion>> = if (potionTag.contains("Potion")) {
                    val potionKey = ResourceLocation.parse(potionTag.getString("Potion"))
                    BuiltInRegistries.POTION.getHolder(potionKey).map { it as Holder<Potion> }
                } else {
                    Optional.empty()
                }

                val customColor: Optional<Int> = if (potionTag.contains("CustomColor")) {
                    Optional.of(potionTag.getInt("CustomColor"))
                } else {
                    Optional.empty()
                }

                val customEffects = mutableListOf<MobEffectInstance>()
                if (potionTag.contains("CustomEffects")) {
                    val effectsList = potionTag.getList("CustomEffects", Tag.TAG_COMPOUND.toInt())
                    for (j in 0 until effectsList.size) {
                        MobEffectInstance.load(effectsList.getCompound(j))?.let {
                            customEffects.add(it)
                        }
                    }
                }

                potionContents += PotionContents(potionHolder, customColor, customEffects)
            }
        }

        specialPotions = mutableListOf()
        if (pTag.contains("SpecialPotions")) {
            val specialList = pTag.getList("SpecialPotions", Tag.TAG_COMPOUND.toInt())
            for (i in 0 until specialList.size) {
                val specialTag = specialList.getCompound(i)

                val item = ItemStack.parseOptional(pRegistries, specialTag.getCompound("Item"))

                val effect: Holder<MobEffect> = if (specialTag.contains("Effect")) {
                    val effectKey = ResourceLocation.parse(specialTag.getString("Effect"))
                    BuiltInRegistries.MOB_EFFECT.getHolder(effectKey)
                        .map { it as Holder<MobEffect> }
                        .orElse(WitcheryMobEffects.EMPTY)
                } else {
                    WitcheryMobEffects.EMPTY
                }

                val specialEffect = if (specialTag.contains("SpecialEffect")) {
                    Optional.of(ResourceLocation.parse(specialTag.getString("SpecialEffect")))
                } else {
                    Optional.empty()
                }

                val baseDuration = specialTag.getInt("BaseDuration")
                val altarPower = specialTag.getInt("AltarPower")
                val capacityCost = specialTag.getInt("CapacityCost")
                val color = specialTag.getInt("Color")

                val effectModifier = WitcheryPotionIngredient.EffectModifier(
                    specialTag.getInt("PowerAdd"),
                    specialTag.getInt("DurationAdd"),
                    specialTag.getInt("DurationMult")
                )

                val dispersalModifier = WitcheryPotionIngredient.DispersalModifier(
                    specialTag.getInt("Range"),
                    specialTag.getInt("LingeringDuration")
                )

                specialPotions += WitcheryPotionIngredient(
                    item, effect, specialEffect, baseDuration, altarPower, capacityCost,
                    listOf(), effectModifier, dispersalModifier,
                    WitcheryPotionIngredient.Type.CONSUMABLE, color
                )
            }
        }

        activeEffects.clear()
        val listTag = pTag.getList("ActiveEffects", Tag.TAG_COMPOUND.toInt())
        for (i in 0 until listTag.size) {
            val effectTag = listTag.getCompound(i)
            val rl = ResourceLocation.parse(effectTag.getString("Id"))
            val isSpecial = effectTag.getBoolean("IsSpecial")
            val remaining = effectTag.getInt("Remaining")
            val original = effectTag.getInt("Original")
            val amp = effectTag.getInt("Amplifier")
            val range = effectTag.getInt("Range")
            activeEffects += ActiveEffect(rl, isSpecial, amp, remaining, original,
                WitcheryPotionIngredient.DispersalModifier(range, range))
        }

        owner.ifPresent {
            pTag.getUUID("Owner")
        }

        if (activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
            refreshActiveEffects()
        }
    }

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)

        if (level.gameTime % 5 == 0L) {
            spawnParticles(level, pos, blockState)
        }

        if (level.isClientSide) return

        val litProperty = blockState.properties.find { it.name == "lit" } as? BooleanProperty

        if (litProperty != null) {
            val shouldBeLit = activeEffects.isNotEmpty()
            if (blockState.getValue(litProperty) != shouldBeLit) {
                level.setBlockAndUpdate(pos, blockState.setValue(litProperty, shouldBeLit))
            }
        }

        val iterator = activeEffects.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()

            if (effect.remainingTicks == -1) {
                var bl = true
                if (level.gameTime % 20 == 0L) {
                    bl = consumeAltarPower(level)
                }
                if (!bl) {
                    iterator.remove()
                }
                runEffect(level, pos, effect)
            } else if (effect.remainingTicks > 0) {
                runEffect(level, pos, effect)
                effect.remainingTicks--

                if (effect.remainingTicks <= 0) {
                    iterator.remove()
                }
            } else {
                iterator.remove()
            }
        }
    }

    private fun spawnParticles(level: Level, pos: BlockPos, blockState: BlockState) {
        val random = level.random
        val centerX = pos.x + 0.5
        val centerY = pos.y + 0.5
        val centerZ = pos.z + 0.5

        val yOffset = 0.1

        val isLit = blockState.properties.find { it.name == "lit" }?.let {
            blockState.getValue(it as BooleanProperty)
        } ?: false

        if (isLit) {
            if (random.nextFloat() < 0.3f) {
                val offsetX = random.nextGaussian() * 0.2
                val offsetZ = random.nextGaussian() * 0.2

                level.addParticle(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    centerX + offsetX,
                    centerY + yOffset,
                    centerZ + offsetZ,
                    0.0,
                    0.02 + random.nextDouble() * 0.03,
                    0.0
                )
            }

            if (random.nextFloat() < 0.15f) {
                level.addParticle(
                    ParticleTypes.SOUL,
                    centerX + (random.nextDouble() - 0.5) * 0.3,
                    centerY + yOffset + 0.1,
                    centerZ + (random.nextDouble() - 0.5) * 0.3,
                    0.0,
                    0.01,
                    0.0
                )
            }
        }

        if (activeEffects.isNotEmpty() && random.nextFloat() < 0.4f) {
            val particleColor = calculatePotionParticleColor()

            if (particleColor != null) {
                val angle = random.nextDouble() * Math.PI * 2
                val radius = 0.3 + random.nextDouble() * 0.2
                val particleX = centerX + Math.cos(angle) * radius
                val particleZ = centerZ + Math.sin(angle) * radius

                level.addParticle(
                    DustParticleOptions(
                        Vec3.fromRGB24(particleColor).toVector3f(),
                        0.8f
                    ),
                    particleX,
                    centerY + yOffset + random.nextDouble() * 0.3,
                    particleZ,
                    0.0,
                    0.01,
                    0.0
                )
            }

            activeEffects.forEach { effect ->
                if (effect.isSpecial && random.nextFloat() < 0.1f) {
                    spawnSpecialEffectParticles(level, pos, effect, centerX, centerY + yOffset, centerZ)
                }
            }
        }

        if (isLit && random.nextFloat() < 0.2f) {
            level.addParticle(
                ParticleTypes.SMOKE,
                centerX + (random.nextDouble() - 0.5) * 0.4,
                centerY + yOffset + 0.3,
                centerZ + (random.nextDouble() - 0.5) * 0.4,
                0.0,
                0.005,
                0.0
            )
        }
    }

    private fun calculatePotionParticleColor(): Int? {
        if (potionContents.isNotEmpty() && potionContents[0] != PotionContents.EMPTY) {
            return potionContents[0].color
        }

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

        return activeEffects.firstOrNull()?.let { effect ->
            val mobEffect = BuiltInRegistries.MOB_EFFECT.get(effect.id)
            mobEffect?.color ?: 0x7B2FBE
        }
    }

    private fun spawnSpecialEffectParticles(
        level: Level,
        pos: BlockPos,
        effect: ActiveEffect,
        x: Double,
        y: Double,
        z: Double
    ) {
        val random = level.random

        when (effect.id.path) {
            "grow_flowers" -> {
                if (random.nextFloat() < 0.3f) {
                    level.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        x + (random.nextDouble() - 0.5) * 0.5,
                        y,
                        z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.0, 0.0
                    )
                }
            }
            "fertile" -> {
                if (random.nextFloat() < 0.2f) {
                    level.addParticle(
                        ParticleTypes.COMPOSTER,
                        x + (random.nextDouble() - 0.5) * 0.5,
                        y + 0.2,
                        z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.0, 0.0
                    )
                }
            }
            "love" -> {
                if (random.nextFloat() < 0.2f) {
                    level.addParticle(
                        ParticleTypes.HEART,
                        x + (random.nextDouble() - 0.5) * 0.8,
                        y + 0.5,
                        z + (random.nextDouble() - 0.5) * 0.8,
                        0.0, 0.0, 0.0
                    )
                }
            }
            "extinguish" -> {
                if (random.nextFloat() < 0.15f) {
                    level.addParticle(
                        ParticleTypes.SPLASH,
                        x + (random.nextDouble() - 0.5) * 0.6,
                        y + 0.1,
                        z + (random.nextDouble() - 0.5) * 0.6,
                        0.0, -0.1, 0.0
                    )
                }
            }
            "grow", "shrink" -> {
                if (random.nextFloat() < 0.2f) {
                    level.addParticle(
                        ParticleTypes.WITCH,
                        x + (random.nextDouble() - 0.5) * 0.5,
                        y + 0.3,
                        z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.02, 0.0
                    )
                }
            }
            "harvest" -> {
                if (random.nextFloat() < 0.15f) {
                    level.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        x + (random.nextDouble() - 0.5) * 0.6,
                        y,
                        z + (random.nextDouble() - 0.5) * 0.6,
                        0.0, 0.0, 0.0
                    )
                }
            }
        }
    }

    data class ActiveEffect(
        val id: ResourceLocation,
        val isSpecial: Boolean,
        val amplifier: Int = 0,
        var remainingTicks: Int, // -1 for infinite
        val originalDuration: Int,
        var disp: WitcheryPotionIngredient.DispersalModifier = WitcheryPotionIngredient.DispersalModifier()
    )
}