package dev.sterner.witchery.block.brazier

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.*
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.block.TintedGlassBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

class BrazierBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BRAZIER.get(), blockPos, blockState),
    Container, AltarPowerConsumer, RecipeCraftingHolder, WorldlyContainer {

    var items: NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)
    private val quickCheck = RecipeManager.createCheck(WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get())
    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation>()
    private var cachedAltarPos: BlockPos? = null

    var active = false
    private var summoningTicker = 0

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }

        if (items.isNotEmpty()) {
            val brazierSummonRecipe =
                quickCheck.getRecipeFor(MultipleItemRecipeInput(items), level).orElse(null)

            if (brazierSummonRecipe != null && active) {
                summoningTicker++
                if (summoningTicker >= 20 * 5) {
                    summoningTicker = 0
                    for (entity in brazierSummonRecipe.value.outputEntities) {
                        val summonPos = findRandomPositionAround(level, pos)
                        summonPos?.let { validPos ->
                            val summon = entity.create(level)
                            summon?.moveTo(Vec3(validPos.x + 0.5, validPos.y.toDouble(), validPos.z + 0.5))
                            summon?.let {
                                val bl = level.addFreshEntity(it)
                                if (!bl) {
                                    Containers.dropContents(level, pos, this)
                                }
                            }
                        }
                    }
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                    level.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, false))
                    items.clear()
                    summoningTicker = 0
                    active = false
                    setChanged()
                }
            }
        }
    }

    private fun findRandomPositionAround(level: Level, centerPos: BlockPos): BlockPos? {
        val radiusRange = (3..5)
        val random = level.random

        for (attempt in 1..10) {
            val offsetX = (random.nextDouble() * 2 - 1) * radiusRange.random()
            val offsetZ = (random.nextDouble() * 2 - 1) * radiusRange.random()

            val targetPos = centerPos.offset(offsetX.toInt(), 0, offsetZ.toInt())

            if (level.isEmptyBlock(targetPos) && level.isEmptyBlock(targetPos.above())) {
                return targetPos
            }
        }
        return centerPos.north()
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {

        if (level != null && pPlayer.isShiftKeyDown && !active) {
            Containers.dropContents(level!!, blockPos, items)
            return ItemInteractionResult.SUCCESS
        }

        if (level != null && pPlayer.mainHandItem.`is`(Items.FLINT_AND_STEEL) || pPlayer.mainHandItem.`is`(Items.FIRE_CHARGE)) {
            val brazierSummonRecipe = quickCheck.getRecipeFor(MultipleItemRecipeInput(items), level!!).orElse(null)
            if (brazierSummonRecipe != null) {
                level?.addParticle(
                    ParticleTypes.SMOKE,
                    blockPos.x + 0.5,
                    blockPos.y + 0.65,
                    blockPos.z + 0.5,
                    0.0,
                    0.0,
                    0.0
                )
                level?.addParticle(
                    ParticleTypes.FLAME,
                    blockPos.x + 0.5,
                    blockPos.y + 0.65,
                    blockPos.z + 0.5,
                    0.0,
                    0.0,
                    0.0
                )
                level?.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    blockPos.x + 0.5,
                    blockPos.y + 0.65,
                    blockPos.z + 0.5,
                    0.0,
                    0.0,
                    0.0
                )

                pStack.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND)
                level?.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS)
                active = true
                level!!.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, true))
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        for (i in items.indices) {
            if (items[i].isEmpty) {
                items[i] = pStack.copy()
                items[i].count = 1
                pStack.shrink(1)
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries)
        if (pTag.contains("altarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }
        this.active = pTag.getBoolean("active")
        this.summoningTicker = pTag.getInt("summoning")
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.items, registries)
        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }
        tag.putBoolean("active", active)
        tag.putInt("summoning", this.summoningTicker)
    }

    override fun isEmpty(): Boolean {
        for (itemStack in this.items) {
            if (!itemStack.isEmpty) {
                return false
            }
        }

        return true
    }

    override fun getItem(slot: Int): ItemStack {
        return this.items[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(this.items, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }

        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(this.items, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        stack.limitSize(this.getMaxStackSize(stack))
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }

    override fun getSlotsForFace(side: Direction) =
        (0..<items.size).toList().toIntArray()

    override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, direction: Direction?): Boolean {
        return true
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean {
        return !active
    }

    override fun clearContent() {
        this.items.clear()
    }

    override fun getContainerSize(): Int {
        return items.size
    }

    override fun setRecipeUsed(recipe: RecipeHolder<*>?) {
        if (recipe != null) {
            val resourceLocation = recipe.id()
            recipesUsed.addTo(resourceLocation, 1)
        }
    }

    override fun getRecipeUsed(): RecipeHolder<*>? {
        return null
    }

    companion object {
        fun registerEvents() {
            InteractionEvent.RIGHT_CLICK_BLOCK.register(::makeSoulCage)
        }

        private fun makeSoulCage(player: Player, interactionHand: InteractionHand?, blockPos: BlockPos, direction: Direction?): EventResult? {
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
                    return EventResult.interruptTrue()
                }
            }

            return EventResult.pass()
        }
    }
}