package dev.sterner.witchery.block.censer

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.item.potion.WitcheryPotionItem.Companion.getMergedEffectModifier
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import java.util.Optional

class CenserBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.CENSER.get(), blockPos, blockState), PotionSpreader {

    override var potionContents: PotionContents
        get() = PotionContents.EMPTY
        set(value) {
            potionContents = value
        }
    override var activePotionSpecialEffects: MutableList<Pair<ResourceLocation, Int>>
        get() = mutableListOf()
        set(value) {
            activePotionSpecialEffects = value
        }
    override var potionEffectRadius: Double
        get() = 8.0
        set(value) {
            potionEffectRadius = value
        }
    override var potionEffectRemainingTicks: Int
        get() = 0
        set(value) {
            potionEffectRemainingTicks = value
        }
    override var isInfinite: Boolean
        get() = false
        set(value) {
            isInfinite = value
        }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }

        if (potionEffectRemainingTicks > 0 || isInfinite) {
            potionEffectRemainingTicks--

            if (potionEffectRemainingTicks % 20 == 0) {
                applyPotionEffectsToNearbyEntities(level, pos, potionContents, activePotionSpecialEffects, potionEffectRadius)
            }

            if (level.random.nextFloat() < 0.2f) {
                val offsetX = (level.random.nextDouble() - 0.5) * 2
                val offsetZ = (level.random.nextDouble() - 0.5) * 2

                val color = potionContents.color

                if (level is ServerLevel) {
                    level.sendParticles(
                        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                        pos.x + 0.5 + offsetX,
                        pos.y + 0.5 + level.random.nextDouble(),
                        pos.z + 0.5 + offsetZ,
                        1,
                        0.0, 0.0, 0.0,
                        1.0
                    )
                }
            }

            if (potionEffectRemainingTicks <= 0 && !isInfinite) {
                potionContents = PotionContents.EMPTY
                activePotionSpecialEffects.clear()
            }
            setChanged()
        }
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (level != null && (pStack.`is`(Items.POTION) || pStack.`is`(Items.SPLASH_POTION) || pStack.`is`(Items.LINGERING_POTION))) {

            if (pStack.has(DataComponents.POTION_CONTENTS) && pStack.get(DataComponents.POTION_CONTENTS) != PotionContents.EMPTY) {
                isInfinite = InfiniteCenserReloadListener.isPotionInfinite(pStack.get(DataComponents.POTION_CONTENTS)!!)
                potionContents = pStack.get(DataComponents.POTION_CONTENTS)!!
                potionEffectRemainingTicks = 20 * 300
                potionEffectRadius = 8.0

                if (pStack.`is`(Items.LINGERING_POTION)) {
                    potionEffectRadius = 12.0
                    potionEffectRemainingTicks = 20 * 400
                }

                level?.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)

                val color = potionContents.color

                if (level is ServerLevel) {
                    (level as ServerLevel).sendParticles(
                        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                        blockPos.x + 0.5,
                        blockPos.y + 0.8,
                        blockPos.z + 0.5,
                        10,
                        0.3, 0.3, 0.3,
                        0.5
                    )
                }

                if (!pPlayer.isCreative) {
                    pPlayer.setItemInHand(pHand, ItemStack(Items.GLASS_BOTTLE))
                }

                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        if (level != null && pStack.has(WITCHERY_POTION_CONTENT.get())) {
            val potionContentList: List<WitcheryPotionIngredient>? = pStack.get(WITCHERY_POTION_CONTENT.get())

            if (potionContentList != null && potionContentList.isNotEmpty()) {
                val globalModifier = getMergedEffectModifier(potionContentList)

                potionContents = PotionContents.EMPTY
                activePotionSpecialEffects.clear()

                var shouldInvertNext = false

                for (potionContent in potionContentList) {
                    if (potionContent.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                        shouldInvertNext = true
                        continue
                    }

                    val effect = if (shouldInvertNext) {
                        shouldInvertNext = false
                        WitcheryMobEffects.invertEffect(potionContent.effect)
                    } else {
                        potionContent.effect
                    }

                    val duration = ((potionContent.baseDuration + globalModifier.durationAddition) *
                            globalModifier.durationMultiplier).coerceAtLeast(20)
                    val amplifier = globalModifier.powerAddition

                    if (effect != WitcheryMobEffects.EMPTY) {
                        val mobEffectInstance = MobEffectInstance(effect, duration, amplifier)
                        potionContents = potionContents.withEffectAdded(mobEffectInstance)
                    }

                    if (potionContent.specialEffect.isPresent) {
                        activePotionSpecialEffects.add(
                            Pair(potionContent.specialEffect.get(), amplifier)
                        )
                    }
                }

                val dispersalModifier = WitcheryPotionItem.getMergedDisperseModifier(potionContentList)
                potionEffectRadius = 8.0 * dispersalModifier.rangeModifier
                potionEffectRemainingTicks = 20 * 300 * dispersalModifier.lingeringDurationModifier

                level?.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)

                val color = potionContents.color
                if (level is ServerLevel) {
                    (level as ServerLevel).sendParticles(
                        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                        blockPos.x + 0.5,
                        blockPos.y + 0.8,
                        blockPos.z + 0.5,
                        15,
                        0.3, 0.3, 0.3,
                        0.5
                    )
                }

                if (!pPlayer.isCreative) {
                    pPlayer.setItemInHand(pHand, ItemStack(Items.GLASS_BOTTLE))
                }

                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.level?.let { loadPotionHolder(pTag, it) }
    }

    override fun saveAdditional(pTag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(pTag, registries)
        this.level?.let { savePotionHolder(pTag, it) }
    }

    companion object {

        fun applyPotionEffectsToNearbyEntities(
            level: Level,
            pos: BlockPos,
            potionContents: PotionContents,
            activePotionSpecialEffects: MutableList<Pair<ResourceLocation, Int>>,
            potionEffectRadius: Double
        ) {
            if (potionContents == PotionContents.EMPTY && activePotionSpecialEffects.isEmpty()) return

            val effectBox = AABB(
                pos.x - potionEffectRadius, pos.y - 1.0, pos.z - potionEffectRadius,
                pos.x + potionEffectRadius, pos.y + 3.0, pos.z + potionEffectRadius
            )

            val entities = level.getEntitiesOfClass(LivingEntity::class.java, effectBox)

            for (entity in entities) {
                if (potionContents != PotionContents.EMPTY) {
                    for (effect in potionContents.allEffects) {
                        val shortenedEffect = MobEffectInstance(
                            effect.effect,
                            effect.duration / 10,
                            effect.amplifier,
                            effect.isAmbient,
                            true
                        )
                        entity.addEffect(shortenedEffect)
                    }
                }

                for ((specialEffect, amplifier) in activePotionSpecialEffects) {
                    val special = WitcherySpecialPotionEffects.SPECIALS.get(specialEffect)
                    special?.onActivated(
                        level,
                        entity,
                        EntityHitResult(entity, entity.position()),
                        mutableListOf(),
                        WitcheryPotionIngredient.DispersalModifier(1, 1),
                        300,
                        amplifier
                    )
                }
            }
        }
    }

        /*
    companion object {
        private const val EFFECT_RADIUS = 8.0
        private const val TICK_INTERVAL = 20
        private const val DEFAULT_DURATION = 20 * 60 * 5
        private const val INFINITE_DURATION = -1
    }

    private var storedPotion: ItemStack = ItemStack.EMPTY
    private var remainingTicks: Int = 0
    private var tickCounter: Int = 0
    private var potionContents: PotionContents = PotionContents.EMPTY
    private var specialPotionEffect: SpecialPotion? = null
    private var specialPotionAmplifier: Int = 0
    private var specialPotionDuration: Int = 0
    private var isInfinite: Boolean = false

    override fun tick(
        level: Level,
        pos: BlockPos,
        blockState: BlockState
    ) {
        super.tick(level, pos, blockState)

        if (level.isClientSide) return

        if (remainingTicks > 0 || isInfinite) {
            if (!isInfinite) {
                remainingTicks--
            }
            tickCounter++

            if (tickCounter >= TICK_INTERVAL) {
                tickCounter = 0
                applyAreaEffects(level, pos)
            }

            if (level.random.nextFloat() < 0.3f) {
                addParticles(level, pos)
            }

            if (!isInfinite && remainingTicks <= 0) {
                clearPotion()
            }

            setChanged()
        }
    }

    override fun onUseWithItem(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult {
        val level = level ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        if (pStack.item is PotionItem || pStack.item is WitcheryPotionItem) {
            val contents = if (pStack.item is PotionItem) {
                pStack.get(DataComponents.POTION_CONTENTS) ?: PotionContents.EMPTY
            } else {
                convertWitcheryPotionToContents(pStack)
            }

            if (!contents.hasEffects()) {
                return ItemInteractionResult.FAIL
            }

            if (remainingTicks > 0 || isInfinite) {
                clearPotion()
            }

            storedPotion = pStack.copy()
            storedPotion.count = 1
            potionContents = contents

            isInfinite = InfiniteCenserReloadListener.isPotionInfinite(contents)
            remainingTicks = if (isInfinite) INFINITE_DURATION else DEFAULT_DURATION

            extractSpecialPotionEffect(pStack)

            tickCounter = 0

            updateLitState(true)

            if (!pPlayer.isCreative) {
                pStack.shrink(1)
                val emptyBottle = ItemStack(Items.GLASS_BOTTLE)
                if (!pPlayer.inventory.add(emptyBottle)) {
                    pPlayer.drop(emptyBottle, false)
                }
            }

            level.playSound(
                null,
                blockPos,
                SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            )

            if (!level.isClientSide) {
                val durationText = if (isInfinite) "indefinitely" else "for 5 minutes"
                pPlayer.displayClientMessage(
                    Component.literal("Censer will burn $durationText"),
                    true
                )
            }

            setChanged()
            return ItemInteractionResult.SUCCESS
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private fun clearPotion() {
        storedPotion = ItemStack.EMPTY
        potionContents = PotionContents.EMPTY
        specialPotionEffect = null
        specialPotionAmplifier = 0
        specialPotionDuration = 0
        remainingTicks = 0
        isInfinite = false
        tickCounter = 0

        updateLitState(false)
    }

    private fun updateLitState(lit: Boolean) {
        level?.let { level ->
            val state = level.getBlockState(blockPos)
            if (state.block is CenserBlock) {
                (state.block as CenserBlock).setLit(level, blockPos, lit)
            }
        }
    }

    private fun extractSpecialPotionEffect(potionStack: ItemStack) {
        val customData = potionStack.get(DataComponents.CUSTOM_DATA)
        customData?.let { data ->
            val tag = data.copyTag()
            if (tag.contains("witchery_special_effect")) {
                val effectId = ResourceLocation.tryParse(tag.getString("witchery_special_effect"))
                effectId?.let {
                    specialPotionEffect = WitcherySpecialPotionEffects.SPECIALS.get(it)
                    specialPotionAmplifier = tag.getInt("witchery_amplifier")
                    specialPotionDuration = tag.getInt("witchery_duration")
                }
            }
        }
    }

    private fun applyAreaEffects(level: Level, pos: BlockPos) {
        val box = AABB(pos).inflate(EFFECT_RADIUS)
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, box)

        potionContents.forEachEffect { effect ->
            for (entity in entities) {

                val shortEffect = MobEffectInstance(
                    effect.effect,
                    TICK_INTERVAL + 20,
                    effect.amplifier,
                    true, // Ambient
                    effect.isVisible,
                    effect.showIcon()
                )
                entity.addEffect(shortEffect)
            }
        }

        specialPotionEffect?.let { special ->
            val hitResult = BlockHitResult(
                Vec3.atCenterOf(pos),
                Direction.UP,
                pos,
                false
            )

            if (tickCounter == 0) {
                special.onActivated(
                    level,
                    null,
                    hitResult,
                    entities.toMutableList(),
                    WitcheryPotionIngredient.DispersalModifier(), // Default dispersal modifier
                    specialPotionDuration,
                    specialPotionAmplifier
                )
            }

            when (special) {
                WitcherySpecialPotionEffects.GROW.get() -> {
                    entities.forEach { entity ->
                        entity.addEffect(
                            MobEffectInstance(
                                WitcheryMobEffects.GROW,
                                TICK_INTERVAL + 20,
                                specialPotionAmplifier,
                                true,
                                false
                            )
                        )
                    }
                }
                WitcherySpecialPotionEffects.SHRINK.get() -> {
                    entities.forEach { entity ->
                        entity.addEffect(
                            MobEffectInstance(
                                WitcheryMobEffects.SHRINK,
                                TICK_INTERVAL + 20,
                                specialPotionAmplifier,
                                true,
                                false
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addParticles(level: Level, pos: BlockPos) {
        val color = potionContents.color
        val particleType = when {
            specialPotionEffect != null -> {
                when (specialPotionEffect) {
                    WitcherySpecialPotionEffects.GROW_FLOWERS.get() -> ParticleTypes.HAPPY_VILLAGER
                    WitcherySpecialPotionEffects.FERTILE.get() -> ParticleTypes.HAPPY_VILLAGER
                    WitcherySpecialPotionEffects.LOVE.get() -> ParticleTypes.HEART
                    WitcherySpecialPotionEffects.EXTINGUISH.get() -> ParticleTypes.SPLASH
                    else -> ParticleTypes.EFFECT
                }
            }
            else -> ParticleTypes.EFFECT
        }

        val x = pos.x + 0.5 + level.random.nextGaussian() * 0.2
        val y = pos.y + 0.8
        val z = pos.z + 0.5 + level.random.nextGaussian() * 0.2

        if (level is ServerLevel) {
            if (particleType == ParticleTypes.EFFECT) {
                level.sendParticles(
                    DustParticleOptions(
                        Vec3.fromRGB24(color).toVector3f(),
                        1.0f
                    ),
                    x, y, z,
                    1,
                    0.0, 0.1, 0.0,
                    0.02
                )
            } else {
                level.sendParticles(
                    particleType,
                    x, y, z,
                    1,
                    0.0, 0.1, 0.0,
                    0.02
                )
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.putInt("RemainingTicks", remainingTicks)
        tag.putInt("TickCounter", tickCounter)
        tag.putBoolean("IsInfinite", isInfinite)

        if (!storedPotion.isEmpty) {
            tag.put("StoredPotion", storedPotion.save(registries))
        }

        if (potionContents != PotionContents.EMPTY) {
            val contentsTag = CompoundTag()

            potionContents.potion().ifPresent { potion ->
                contentsTag.putString("Potion", potion.unwrapKey().get().location().toString())
            }

            potionContents.customColor().ifPresent { color ->
                contentsTag.putInt("CustomColor", color)
            }

            if (!potionContents.customEffects().isEmpty()) {
                val effectsList = ListTag()
                for (effect in potionContents.customEffects()) {
                    effectsList.add(effect.save())
                }
                contentsTag.put("CustomEffects", effectsList)
            }

            tag.put("PotionContents", contentsTag)
        }

        specialPotionEffect?.let {
            tag.putString("SpecialEffect", it.id.toString())
            tag.putInt("SpecialAmplifier", specialPotionAmplifier)
            tag.putInt("SpecialDuration", specialPotionDuration)
        }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        remainingTicks = tag.getInt("RemainingTicks")
        tickCounter = tag.getInt("TickCounter")
        isInfinite = tag.getBoolean("IsInfinite")

        if (tag.contains("StoredPotion")) {
            storedPotion = ItemStack.parseOptional(registries, tag.getCompound("StoredPotion"))
        }

        if (tag.contains("PotionContents")) {
            val contentsTag = tag.getCompound("PotionContents")

            val potion: Optional<Holder<Potion>> = if (contentsTag.contains("Potion")) {
                val potionId = ResourceLocation.tryParse(contentsTag.getString("Potion"))
                potionId?.let {
                    val key = ResourceKey.create(Registries.POTION, it)
                    val holder = registries.lookupOrThrow(Registries.POTION).get(key)
                    if (holder.isPresent) {
                        Optional.of(holder.get() as Holder<Potion>)
                    } else {
                        Optional.empty()
                    }
                } ?: Optional.empty()
            } else {
                Optional.empty()
            }

            val customColor = if (contentsTag.contains("CustomColor")) {
                Optional.of(contentsTag.getInt("CustomColor"))
            } else {
                Optional.empty()
            }

            val customEffects = mutableListOf<MobEffectInstance>()
            if (contentsTag.contains("CustomEffects")) {
                val effectsList = contentsTag.getList("CustomEffects", 10)
                for (i in 0 until effectsList.size) {
                    MobEffectInstance.load(effectsList.getCompound(i))?.let {
                        customEffects.add(it)
                    }
                }
            }

            potionContents = PotionContents(potion, customColor, customEffects)
        }

        if (tag.contains("SpecialEffect")) {
            val effectId = ResourceLocation.tryParse(tag.getString("SpecialEffect"))
            effectId?.let {
                specialPotionEffect = WitcherySpecialPotionEffects.SPECIALS.get(it)
                specialPotionAmplifier = tag.getInt("SpecialAmplifier")
                specialPotionDuration = tag.getInt("SpecialDuration")
            }
        }
    }

    private fun convertWitcheryPotionToContents(potionStack: ItemStack): PotionContents {
        val witcheryContent = potionStack.get(WITCHERY_POTION_CONTENT.get())
            ?: return PotionContents.EMPTY

        val effects = mutableListOf<MobEffectInstance>()
        val globalModifier = WitcheryPotionItem.getMergedEffectModifier(witcheryContent)
        var shouldInvertNext = false

        for (ingredient in witcheryContent) {
            if (ingredient.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                shouldInvertNext = true
                continue
            }

            if (ingredient.effect == WitcheryMobEffects.EMPTY) continue

            val effect = if (shouldInvertNext) {
                shouldInvertNext = false
                WitcheryMobEffects.invertEffect(ingredient.effect)
            } else {
                ingredient.effect
            }

            val baseDuration = (ingredient.baseDuration + globalModifier.durationAddition) * globalModifier.durationMultiplier
            val amplifier = globalModifier.powerAddition

            effects.add(MobEffectInstance(effect, baseDuration, amplifier))

            ingredient.specialEffect.ifPresent { specialId ->
                val customData = potionStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                val tag = customData.copyTag()
                tag.putString("witchery_special_effect", specialId.toString())
                tag.putInt("witchery_amplifier", amplifier)
                tag.putInt("witchery_duration", baseDuration)
                potionStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
            }
        }

        val color = if (witcheryContent.isNotEmpty()) {
            Optional.of(witcheryContent.first().color)
        } else {
            Optional.empty()
        }

        return PotionContents(Optional.empty(), color, effects)
    }

         */
}