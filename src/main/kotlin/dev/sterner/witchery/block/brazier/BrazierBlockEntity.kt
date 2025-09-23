package dev.sterner.witchery.block.brazier

import dev.sterner.witchery.api.block.ActiveEffect
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.PotionDisperser
import dev.sterner.witchery.api.block.PotionDisperserHelper
import dev.sterner.witchery.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.*
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.block.TintedGlassBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import java.util.*

class BrazierBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BRAZIER.get(), blockPos, blockState),
    Container,
    AltarPowerConsumer,
    RecipeCraftingHolder,
    WorldlyContainer,
    PotionDisperser {

    var items: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)
    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation>()
    private val quickCheck = RecipeManager.createCheck(WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get())

    private var summoningTicker = 0
    var active = false

    private var potionContents: List<PotionContents> = listOf(PotionContents.EMPTY)
    private var specialPotions: List<WitcheryPotionIngredient> = listOf()
    private val activeEffects: MutableList<ActiveEffect> = mutableListOf()
    private var owner: Optional<UUID> = Optional.empty()
    private var potionEffectRadius: Double = 8.0
    private var potionEffectDuration: Int = 20 * 300

    private var cachedAltarPos: BlockPos? = null
    private var deferredNbtData: CompoundTag? = null

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

    override fun isInfiniteMode() = false
    override fun setInfiniteMode(infinite: Boolean) {}

    override fun getDispersalRadius() = potionEffectRadius

    override fun shouldConsumePower() = false

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        super.tick(level, pos, blockState)

        deferredNbtData?.let { nbtData ->

            PotionDisperserHelper.loadPotionData(nbtData, this, level)

            if (activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
                PotionDisperserHelper.refreshActiveEffects(this)
            }

            deferredNbtData = null
        }

        if (level.isClientSide) {
            spawnClientParticles(level, pos)
            return
        }

        if (active && items.isNotEmpty()) {
            tickSummoning(level, pos)
        }

        if (active && activeEffects.isNotEmpty()) {
            tickPotionEffects(level, pos)
        }

        updateLitState(level, pos, blockState)
    }


    private fun tickSummoning(level: Level, pos: BlockPos) {
        val brazierSummonRecipe = quickCheck.getRecipeFor(MultipleItemRecipeInput(items), level).orElse(null)

        if (brazierSummonRecipe != null) {
            summoningTicker++

            if (summoningTicker >= 20 * 5) {
                performSummoning(level, pos, brazierSummonRecipe)
                completeSummoning(level, pos)
            }
        }
    }

    private fun performSummoning(level: Level, pos: BlockPos, recipe: RecipeHolder<*>) {
        val brazierRecipe = recipe.value as? BrazierSummoningRecipe ?: return

        brazierRecipe.outputEntities.forEach { entityType ->
            findSummonPosition(level, pos)?.let { summonPos ->
                entityType.create(level)?.let { entity ->
                    entity.moveTo(Vec3(summonPos.x + 0.5, summonPos.y.toDouble(), summonPos.z + 0.5))
                    if (!level.addFreshEntity(entity)) {
                        Containers.dropContents(level, pos, this)
                    }
                }
            }
        }
    }

    private fun findSummonPosition(level: Level, centerPos: BlockPos): BlockPos? {
        val radiusRange = (3..5)
        val random = level.random

        repeat(10) {
            val offsetX = (random.nextDouble() * 2 - 1) * radiusRange.random()
            val offsetZ = (random.nextDouble() * 2 - 1) * radiusRange.random()

            val targetPos = centerPos.offset(offsetX.toInt(), 0, offsetZ.toInt())

            if (level.isEmptyBlock(targetPos) && level.isEmptyBlock(targetPos.above())) {
                return targetPos
            }
        }
        return centerPos.north()
    }

    private fun completeSummoning(level: Level, pos: BlockPos) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
        items.clear()
        summoningTicker = 0
        active = false
        setChanged()
    }

    private fun tickPotionEffects(level: Level, pos: BlockPos) {
        val iterator = activeEffects.iterator()
        val currentTime = level.gameTime

        while (iterator.hasNext()) {
            val effect = iterator.next()

            val shouldApply = if (effect.isSpecial) {
                currentTime - effect.lastSpecialActivation >= 40
            } else {
                currentTime % 20 == 0L
            }

            if (shouldApply) {
                PotionDisperserHelper.applyEffects(this, level, pos, effect)
                if (effect.isSpecial) {
                    effect.lastSpecialActivation = currentTime
                }
            }

            if (effect.remainingTicks > 0) {
                effect.remainingTicks--
                if (effect.remainingTicks <= 0) {
                    iterator.remove()
                }
            }
        }

        if (activeEffects.isEmpty() && items.isEmpty()) {
            active = false
            setChanged()
        }
    }

    private fun updateLitState(level: Level, pos: BlockPos, blockState: BlockState) {
        val shouldBeLit = active
        val currentlyLit = blockState.getValue(BlockStateProperties.LIT)

        if (currentlyLit != shouldBeLit) {
            level.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.LIT, shouldBeLit))
        }
    }

    private fun spawnClientParticles(level: Level, pos: BlockPos) {
        if (!active || level.gameTime % 5 != 0L) return

        val centerX = pos.x + 0.5
        val centerY = pos.y + 0.5
        val centerZ = pos.z + 0.5

        if (level.random.nextFloat() < 0.3f) {
            level.addParticle(
                ParticleTypes.FLAME,
                centerX + (level.random.nextDouble() - 0.5) * 0.3,
                centerY,
                centerZ + (level.random.nextDouble() - 0.5) * 0.3,
                0.0, 0.02, 0.0
            )
        }

        if (items.isNotEmpty() && level.random.nextFloat() < 0.2f) {
            level.addParticle(
                ParticleTypes.SMOKE,
                centerX + (level.random.nextDouble() - 0.5) * 0.4,
                centerY + 0.1,
                centerZ + (level.random.nextDouble() - 0.5) * 0.4,
                0.0, 0.05, 0.0
            )
        }

        if (activeEffects.isNotEmpty()) {
            PotionDisperserHelper.spawnPotionParticles(level, pos, this, 0.3f)
        }
    }

    override fun onUseWithItem(player: Player, stack: ItemStack, hand: InteractionHand): ItemInteractionResult {
        if (player.isShiftKeyDown && !active) {
            Containers.dropContents(level!!, blockPos, items)
            return ItemInteractionResult.SUCCESS
        }

        if (items.firstOrNull()?.`is`(WitcheryItems.WOOD_ASH) == true) {
            val result = handlePotionAddition(player, stack, hand)
            if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) return result
        }

        if (canIgnite(stack)) {
            return handleIgnition(player, stack)
        }

        return handleItemAddition(stack)
    }

    private fun canIgnite(stack: ItemStack): Boolean {
        return stack.`is`(Items.FLINT_AND_STEEL) || stack.`is`(Items.FIRE_CHARGE)
    }

    private fun handlePotionAddition(player: Player, stack: ItemStack, hand: InteractionHand): ItemInteractionResult {
        if (stack.`is`(Items.POTION) || stack.`is`(Items.SPLASH_POTION) || stack.`is`(Items.LINGERING_POTION)) {
            val contents = stack.get(DataComponents.POTION_CONTENTS) ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            if (contents == PotionContents.EMPTY) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

            setPotionContents(listOf(contents))
            configurePotionEffect(stack)
            PotionDisperserHelper.refreshActiveEffects(this)

            consumePotionItem(player, hand)
            playPotionSound()
            return ItemInteractionResult.SUCCESS
        }

        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
            val potionList = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

            setSpecialPotions(potionList)
            configureSpecialPotionEffect(potionList)
            PotionDisperserHelper.refreshActiveEffects(this)

            consumePotionItem(player, hand)
            playPotionSound()
            return ItemInteractionResult.SUCCESS
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private fun configurePotionEffect(stack: ItemStack) {
        potionEffectRadius = if (stack.`is`(Items.LINGERING_POTION)) 12.0 else 8.0
        potionEffectDuration = if (stack.`is`(Items.LINGERING_POTION)) 20 * 400 else 20 * 300

        activeEffects.forEach { effect ->
            if (effect.remainingTicks > 0) {
                effect.remainingTicks = potionEffectDuration
            }
        }
    }

    private fun configureSpecialPotionEffect(potionList: List<WitcheryPotionIngredient>) {
        val dispersalModifier = WitcheryPotionItem.getMergedDisperseModifier(potionList)
        potionEffectRadius = 8.0 * dispersalModifier.rangeModifier
        potionEffectDuration = 20 * 300 * dispersalModifier.lingeringDurationModifier
    }

    private fun consumePotionItem(player: Player, hand: InteractionHand) {
        if (!player.isCreative) {
            player.setItemInHand(hand, ItemStack(Items.GLASS_BOTTLE))
        }
    }

    private fun playPotionSound() {
        level?.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)
        spawnPotionParticles()
    }

    private fun spawnPotionParticles() {
        if (level is ServerLevel) {
            val color = PotionDisperserHelper.calculatePotionColor(this)
            val serverLevel = level as ServerLevel

            repeat(15) {
                val offsetX = (level!!.random.nextDouble() - 0.5) * 0.6
                val offsetZ = (level!!.random.nextDouble() - 0.5) * 0.6

                serverLevel.sendParticles(
                    net.minecraft.core.particles.DustParticleOptions(
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

    private fun handleIgnition(player: Player, stack: ItemStack): ItemInteractionResult {
        val canSummon = items.isNotEmpty() && quickCheck.getRecipeFor(MultipleItemRecipeInput(items), level!!).isPresent
        val canActivatePotions = activeEffects.isNotEmpty()

        if (canSummon || canActivatePotions) {
            spawnIgnitionParticles()

            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
            level?.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS)

            active = true
            level?.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, true))
            setChanged()

            return ItemInteractionResult.SUCCESS
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private fun spawnIgnitionParticles() {
        repeat(3) {
            level?.addParticle(
                ParticleTypes.SMOKE,
                blockPos.x + 0.5,
                blockPos.y + 0.65,
                blockPos.z + 0.5,
                0.0, 0.0, 0.0
            )
            level?.addParticle(
                ParticleTypes.FLAME,
                blockPos.x + 0.5 + (level!!.random.nextDouble() - 0.5) * 0.2,
                blockPos.y + 0.65,
                blockPos.z + 0.5 + (level!!.random.nextDouble() - 0.5) * 0.2,
                0.0, 0.0, 0.0
            )
        }
    }

    private fun handleItemAddition(stack: ItemStack): ItemInteractionResult {
        for (i in items.indices) {
            if (items[i].isEmpty) {
                items[i] = stack.copyWithCount(1)
                stack.shrink(1)
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    override fun isEmpty(): Boolean = items.all { it.isEmpty }

    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(items, slot, amount)
        if (!itemStack.isEmpty) {
            setChanged()
        }
        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(items, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        stack.limitSize(maxStackSize)
        setChanged()
    }

    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

    override fun getSlotsForFace(side: Direction) = (0..<items.size).toList().toIntArray()

    override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, direction: Direction?): Boolean = true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean = !active

    override fun clearContent() {
        items.clear()
    }

    override fun getContainerSize(): Int = items.size

    override fun setRecipeUsed(recipe: RecipeHolder<*>?) {
        recipe?.let { recipesUsed.addTo(it.id(), 1) }
    }

    override fun getRecipeUsed(): RecipeHolder<*>? = null

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        ContainerHelper.saveAllItems(tag, items, registries)

        tag.putBoolean("Active", active)
        tag.putInt("SummoningTicker", summoningTicker)

        cachedAltarPos?.let { tag.put("AltarPos", NbtUtils.writeBlockPos(it)) }

        level?.let {
            PotionDisperserHelper.savePotionData(tag, this, it)
        }

        tag.putDouble("PotionRadius", potionEffectRadius)
        tag.putInt("PotionDuration", potionEffectDuration)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        items = NonNullList.withSize(containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, items, registries)

        active = tag.getBoolean("Active")
        summoningTicker = tag.getInt("SummoningTicker")

        if (tag.contains("AltarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(tag, "AltarPos").orElse(null)
        }

        level?.let {
            PotionDisperserHelper.loadPotionData(tag, this, it)

            if (activeEffects.isEmpty() && (potionContents.isNotEmpty() || specialPotions.isNotEmpty())) {
                PotionDisperserHelper.refreshActiveEffects(this)
            }
        } ?: run {
            deferredNbtData = tag.copy()
        }

        potionEffectRadius = tag.getDouble("PotionRadius").takeIf { it > 0.0 } ?: 8.0
        potionEffectDuration = tag.getInt("PotionDuration").takeIf { it > 0 } ?: (20 * 300)
    }


    companion object {

        fun makeSoulCage(
            event: PlayerInteractEvent.RightClickBlock,
            player: Player,
            blockPos: BlockPos
        ) {
            val level = player.level()
            if (level.getBlockState(blockPos).`is`(WitcheryBlocks.BRAZIER.get())) {
                val item = player.mainHandItem.item
                val bl = if (item is BlockItem) {
                    item.block is TintedGlassBlock || item.block is StainedGlassBlock
                } else false

                if (player.mainHandItem.`is`(Items.GLASS) || bl) {
                    if (!player.isCreative) {
                        player.mainHandItem.shrink(1)
                    }
                    level.setBlockAndUpdate(blockPos, WitcheryBlocks.SOUL_CAGE.get().defaultBlockState())
                    event.isCanceled = true
                    return
                }
            }
        }
    }
}