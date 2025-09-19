package dev.sterner.witchery.block.censer

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.util.Optional

class CenserBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.CENSER.get(), blockPos, blockState) {

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

        if (pStack.item is PotionItem) {
            val contents = pStack.get(DataComponents.POTION_CONTENTS) ?: PotionContents.EMPTY

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
}