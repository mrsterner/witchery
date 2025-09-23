package dev.sterner.witchery.block.censer

import dev.sterner.witchery.api.block.ActiveEffect
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.PotionDisperser
import dev.sterner.witchery.api.block.PotionDisperserHelper
import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.Vec3
import java.util.*


class CenserBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.CENSER.get(), blockPos, blockState),
    AltarPowerConsumer,
    PotionDisperser {

    private var potionContents: List<PotionContents> = listOf(PotionContents.EMPTY)
    private var specialPotions: List<WitcheryPotionIngredient> = listOf()
    private val activeEffects: MutableList<ActiveEffect> = mutableListOf()
    private var owner: Optional<UUID> = Optional.empty()
    private var infiniteMode: Boolean = true
    private var dispersalRadius: Double = 16.0

    private var deferredNbtData: CompoundTag? = null
    private var cachedAltarPos: BlockPos? = null

    override fun getPotionContents() = potionContents
    override fun setPotionContents(contents: List<PotionContents>) {
        potionContents = contents
        setChanged()
    }

    override fun getSpecialPotions() = specialPotions
    override fun setSpecialPotions(potions: List<WitcheryPotionIngredient>) {
        specialPotions = potions
        setChanged()
    }

    override fun getActiveEffects() = activeEffects

    override fun getOwner() = owner
    override fun setOwner(owner: Optional<UUID>) {
        this.owner = owner
        setChanged()
    }

    override fun isInfiniteMode() = infiniteMode
    override fun setInfiniteMode(infinite: Boolean) {
        infiniteMode = infinite
        setChanged()
    }

    override fun getDispersalRadius() = dispersalRadius

    override fun shouldConsumePower() = true

    fun tickPotionEffects(level: Level, pos: BlockPos) {
        val iterator = activeEffects.iterator()
        val currentTime = level.gameTime

        while (iterator.hasNext()) {
            val effect = iterator.next()

            if (effect.remainingTicks == -1) {
                var hasAltarPower = true
                if (currentTime % 20 == 0L) {
                    hasAltarPower = consumeAltarPower(level)
                }

                if (!hasAltarPower) {
                    iterator.remove()
                    continue
                }

                if (effect.isSpecial) {
                    if (currentTime - effect.lastSpecialActivation >= 40) {
                        PotionDisperserHelper.applyEffects(this, level, pos, effect)
                        effect.lastSpecialActivation = currentTime
                    }
                } else {
                    PotionDisperserHelper.applyEffects(this, level, pos, effect)
                }
            } else if (effect.remainingTicks > 0) {
                if (effect.isSpecial) {
                    if (currentTime - effect.lastSpecialActivation >= 40) {
                        PotionDisperserHelper.applyEffects(this, level, pos, effect)
                        effect.lastSpecialActivation = currentTime
                    }
                } else {
                    PotionDisperserHelper.applyEffects(this, level, pos, effect)
                }

                effect.remainingTicks--

                if (effect.remainingTicks <= 0) {
                    iterator.remove()
                }
            } else {
                iterator.remove()
            }
        }
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

    private fun spawnClientParticles(level: Level, pos: BlockPos, blockState: BlockState) {
        val random = level.random
        val blockX = pos.x.toDouble()
        val blockY = pos.y.toDouble()
        val blockZ = pos.z.toDouble()

        val minBound = 4.5 / 16.0
        val maxBound = 11.5 / 16.0
        val bottomY = 6.0 / 16.0

        val isLit = blockState.properties.find { it.name == "lit" }?.let {
            blockState.getValue(it as BooleanProperty)
        } ?: false

        val face = random.nextInt(4)

        val (particleX, particleZ) = when (face) {
            0 -> {
                val x = blockX + minBound + random.nextDouble() * (maxBound - minBound)
                val z = blockZ + minBound - 0.02
                x to z
            }

            1 -> {
                val x = blockX + minBound + random.nextDouble() * (maxBound - minBound)
                val z = blockZ + maxBound + 0.02
                x to z
            }

            2 -> {
                val x = blockX + maxBound + 0.02
                val z = blockZ + minBound + random.nextDouble() * (maxBound - minBound)
                x to z
            }

            else -> {
                val x = blockX + minBound - 0.02
                val z = blockZ + minBound + random.nextDouble() * (maxBound - minBound)
                x to z
            }
        }

        val particleY = blockY + bottomY

        if (isLit && random.nextFloat() < 0.3f) {
            level.addParticle(
                ParticleTypes.SOUL_FIRE_FLAME,
                particleX,
                particleY,
                particleZ,
                0.0,
                0.01 - random.nextDouble() * 0.01,
                0.0
            )
        }

        if (isLit && random.nextFloat() < 0.15f) {

            level.addParticle(
                ParticleTypes.SOUL,
                particleX + (random.nextDouble() - 0.5) * 0.05,
                particleY,
                particleZ + (random.nextDouble() - 0.5) * 0.05,
                0.0,
                0.01 + random.nextDouble() * 0.01,
                0.0
            )
        }

        if (activeEffects.isNotEmpty()) {
            PotionDisperserHelper.spawnPotionParticles(level, pos, this, 0.4f)
            spawnSpecialEffectParticles(level, blockX + 0.5, particleY, blockZ + 0.5)
        }

        if (isLit && random.nextFloat() < 0.2f) {
            level.addParticle(
                ParticleTypes.SMOKE,
                particleX,
                particleY + 0.05,
                particleZ,
                0.0,
                0.01,
                0.0
            )
        }
    }

    private fun spawnSpecialEffectParticles(level: Level, x: Double, y: Double, z: Double) {
        val random = level.random

        activeEffects.forEach { effect ->
            if (effect.isSpecial && random.nextFloat() < 0.1f) {
                when (effect.id.path) {
                    "grow_flowers" -> level.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        x + (random.nextDouble() - 0.5) * 0.5, y, z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.0, 0.0
                    )

                    "fertile" -> level.addParticle(
                        ParticleTypes.COMPOSTER,
                        x + (random.nextDouble() - 0.5) * 0.5, y + 0.2, z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.0, 0.0
                    )

                    "love" -> level.addParticle(
                        ParticleTypes.HEART,
                        x + (random.nextDouble() - 0.5) * 0.8, y + 0.5, z + (random.nextDouble() - 0.5) * 0.8,
                        0.0, 0.0, 0.0
                    )

                    "extinguish" -> level.addParticle(
                        ParticleTypes.SPLASH,
                        x + (random.nextDouble() - 0.5) * 0.6, y + 0.1, z + (random.nextDouble() - 0.5) * 0.6,
                        0.0, -0.1, 0.0
                    )

                    "grow", "shrink" -> level.addParticle(
                        ParticleTypes.WITCH,
                        x + (random.nextDouble() - 0.5) * 0.5, y + 0.3, z + (random.nextDouble() - 0.5) * 0.5,
                        0.0, 0.02, 0.0
                    )

                    "harvest" -> level.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        x + (random.nextDouble() - 0.5) * 0.6, y, z + (random.nextDouble() - 0.5) * 0.6,
                        0.0, 0.0, 0.0
                    )
                }
            }
        }
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (pStack.`is`(Items.POTION) || pStack.`is`(Items.SPLASH_POTION) || pStack.`is`(Items.LINGERING_POTION)) {
            val contents = pStack.get(DataComponents.POTION_CONTENTS)
                ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            if (contents == PotionContents.EMPTY) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

            setPotionContents(listOf(contents))
            configureDispersalRadius(pStack)

            consumePotionItem(pPlayer, pHand)
            playPotionSound()

            findAndCacheAltar(level as? ServerLevel)
            PotionDisperserHelper.refreshActiveEffects(this)

            return ItemInteractionResult.SUCCESS
        }

        if (pStack.has(WITCHERY_POTION_CONTENT.get())) {
            val potionList = pStack.get(WITCHERY_POTION_CONTENT.get())
                ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

            setSpecialPotions(potionList)
            configureSpecialDispersalRadius(potionList)

            consumePotionItem(pPlayer, pHand)
            playPotionSound()

            findAndCacheAltar(level as? ServerLevel)
            PotionDisperserHelper.refreshActiveEffects(this)

            return ItemInteractionResult.SUCCESS
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    private fun configureDispersalRadius(stack: ItemStack) {
        dispersalRadius = when {
            stack.`is`(Items.LINGERING_POTION) -> 20.0
            stack.`is`(Items.SPLASH_POTION) -> 18.0
            else -> 16.0
        }
    }

    private fun configureSpecialDispersalRadius(potionList: List<WitcheryPotionIngredient>) {
        val maxRangeModifier = potionList.maxOfOrNull { it.dispersalModifier.rangeModifier } ?: 1
        dispersalRadius = 16.0 * maxRangeModifier
    }

    private fun findAndCacheAltar(serverLevel: ServerLevel?) {
        if (cachedAltarPos == null && serverLevel != null) {
            cachedAltarPos = getAltarPos(serverLevel, blockPos)
            setChanged()
        }
    }

    private fun consumePotionItem(player: Player, hand: InteractionHand) {
        if (!player.isCreative) {
            WitcheryUtil.addItemToInventoryAndConsume(player, hand, ItemStack(Items.GLASS_BOTTLE))
        }
    }

    private fun playPotionSound() {
        level?.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)
        spawnPotionAddedParticles()
    }

    private fun spawnPotionAddedParticles() {
        if (level is ServerLevel) {
            val color = PotionDisperserHelper.calculatePotionColor(this)
            val serverLevel = level as ServerLevel

            repeat(20) {
                val offsetX = (level!!.random.nextDouble() - 0.5) * 0.6
                val offsetZ = (level!!.random.nextDouble() - 0.5) * 0.6

                serverLevel.sendParticles(
                    DustParticleOptions(
                        Vec3.fromRGB24(color).toVector3f(), 0.8f
                    ),
                    blockPos.x + 0.5 + offsetX,
                    blockPos.y + 0.8,
                    blockPos.z + 0.5 + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.1
                )
            }
        }
    }


    override fun saveAdditional(tag: CompoundTag, holder: HolderLookup.Provider) {
        super.saveAdditional(tag, holder)

        cachedAltarPos?.let { tag.put("AltarPos", NbtUtils.writeBlockPos(it)) }

        tag.putDouble("DispersalRadius", dispersalRadius)
        tag.putBoolean("InfiniteMode", infiniteMode)

        level?.let {
            PotionDisperserHelper.savePotionData(tag, this, it)
        }
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)

        if (pTag.contains("AltarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "AltarPos").orElse(null)
        }

        dispersalRadius = pTag.getDouble("DispersalRadius").takeIf { it > 0.0 } ?: 16.0
        infiniteMode = pTag.getBoolean("InfiniteMode")

        level?.let {
            PotionDisperserHelper.loadPotionData(pTag, this, it)


            if (activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
                PotionDisperserHelper.refreshActiveEffects(this)
            }
        } ?: run {
            deferredNbtData = pTag.copy()
        }
    }


    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)

        deferredNbtData?.let { nbtData ->

            PotionDisperserHelper.loadPotionData(nbtData, this, level)

            if (activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
                PotionDisperserHelper.refreshActiveEffects(this)
            }

            deferredNbtData = null
        }

        if (level.gameTime % 20 == 0L && activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
            PotionDisperserHelper.refreshActiveEffects(this)
        }

        if (level.gameTime % 5 == 0L) {
            spawnClientParticles(level, pos, blockState)
        }

        if (level.isClientSide) return

        updateLitState(level, pos, blockState)
        tickPotionEffects(level, pos)
    }


    private fun updateLitState(level: Level, pos: BlockPos, blockState: BlockState) {
        val litProperty = blockState.properties.find { it.name == "lit" } as? BooleanProperty

        if (litProperty != null) {
            val shouldBeLit = activeEffects.isNotEmpty()
            val currentlyLit = blockState.getValue(litProperty)

            if (currentlyLit != shouldBeLit) {
                level.setBlockAndUpdate(pos, blockState.setValue(litProperty, shouldBeLit))
            }
        }
    }

}