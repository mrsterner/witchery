package dev.sterner.witchery.content.block.cauldron

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.core.data.PotionDataReloadListener
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.network.*
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryItems.WITCHERY_POTION
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.PointedDripstoneBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.network.PacketDistributor
import org.joml.Vector3d

class CauldronBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.CAULDRON.get(), CauldronBlock.STRUCTURE.get(),
    pos, state
), Container, WorldlyContainer, AltarPowerConsumer {

    private var cauldronCraftingRecipe: CauldronCraftingRecipe? = null
    private var cauldronBrewingRecipe: CauldronBrewingRecipe? = null
    private var witcheryPotionItemCache: MutableList<WitcheryPotionIngredient> = mutableListOf()
    private var inputItems: NonNullList<ItemStack> = NonNullList.withSize(12, ItemStack.EMPTY)
    private var craftingProgressTicker = 0
    private var brewItemOutput: ItemStack = ItemStack.EMPTY
    private var cachedAltarPos: BlockPos? = null

    var color = WATER_COLOR

    var fluidTank: FluidTank = object : FluidTank(FluidType.BUCKET_VOLUME) {
        override fun onContentsChanged() {
            setChanged()
        }

        override fun isFluidValid(stack: FluidStack): Boolean {
            return stack.fluid == Fluids.WATER
        }
    }

    private var complete = false

    private fun refreshCraftingAndBrewingRecipe(level: Level) {
        val allRecipesOfType = level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())
        val nonEmptyItems = inputItems.filter { !it.isEmpty }

        val possibleRecipe =
            allRecipesOfType.firstOrNull { it.value.matches(MultipleItemRecipeInput(nonEmptyItems), level) }

        possibleRecipe?.let { recipe ->
            val isOrderCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)

            if (isOrderCorrect) {
                cauldronCraftingRecipe = recipe.value
                complete = nonEmptyItems.size == recipe.value.inputItems.size
            } else {
                refreshBrewingRecipe(level)
            }
        } ?: run {
            refreshBrewingRecipe(level)
        }

        setChanged()
    }

    private fun refreshBrewingRecipe(level: Level) {
        val allRecipesOfType = level.recipeManager
            .getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())
            .filter { recipe ->
                val dimensions = recipe.value.dimensionKey
                val noRequirement = dimensions.isEmpty() || (dimensions.size == 1 && dimensions.contains(""))
                val currentDimension = level.dimension().location().toString()

                noRequirement || dimensions.contains(currentDimension)
            }

        val nonEmptyItems = inputItems.filter { !it.isEmpty }
        val possibleRecipe =
            allRecipesOfType.firstOrNull { it.value.matches(MultipleItemRecipeInput(nonEmptyItems), level) }

        possibleRecipe?.let { recipe ->
            val isOrderCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)

            if (isOrderCorrect) {
                cauldronBrewingRecipe = recipe.value
                complete = nonEmptyItems.size == recipe.value.inputItems.size
            } else {
                cauldronBrewingRecipe = null
                complete = false
            }
        } ?: run {
            cauldronBrewingRecipe = null
            complete = false
        }

        setChanged()
    }

    private fun handleDripstone(level: Level, pos: BlockPos) {
        if (level !is ServerLevel) return
        val dripstone = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, pos) ?: return
        val fluid = PointedDripstoneBlock.getCauldronFillFluidType(level, dripstone)
        if (fluid == Fluids.EMPTY) return

        this.fluidTank.fill(FluidStack(fluid, 10), IFluidHandler.FluidAction.EXECUTE)
    }

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {

        if (level.random.nextFloat() < 0.005)
            handleDripstone(level, blockPos)

        if (level.isClientSide || !blockState.getValue(BlockStateProperties.LIT)) {
            return
        }

        if (level.gameTime % 4 == 0L && !complete && !fluidTank.isEmpty) {
            consumeItem(level, blockPos)
        }

        if (witcheryPotionItemCache.isNotEmpty()) {
            val randX = blockPos.x + 0.5 + Mth.nextDouble(level.random, -0.1, 0.1)
            val randY = (blockPos.y + 1.0)
            val randZ = blockPos.z + 0.5 + Mth.nextDouble(level.random, -0.1, 0.1)
            if (level is ServerLevel) {
                PacketDistributor.sendToPlayersTrackingChunk(
                    level,
                    ChunkPos(blockPos),
                    CauldronPotionBrewParticleS2CPayload(Vector3d(randX, randY, randZ), color)
                )
            }
        }

        if (!brewItemOutput.isEmpty) {
            val randX = blockPos.x + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25)
            val randY = (blockPos.y + 1.0)
            val randZ = blockPos.z + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25)
            if (level is ServerLevel) {
                PacketDistributor.sendToPlayersTrackingChunk(
                    level,
                    ChunkPos(blockPos),
                    CauldronEffectParticleS2CPayload(Vector3d(randX, randY, randZ), color)
                )
            }
        }

        if (cauldronCraftingRecipe != null || cauldronBrewingRecipe != null) {
            if (complete) {
                if (craftingProgressTicker < PROGRESS_TICKS) {
                    craftingProgressTicker++
                    setChanged()
                } else {
                    craftingProgressTicker = 0
                    craft(level, blockPos)
                }
            }
        }
    }

    private fun hasEnoughAltarPower(level: Level, ingredient: WitcheryPotionIngredient): Boolean {
        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }
        val requiredAltarPower = ingredient.altarPower
        if (requiredAltarPower > 0 && cachedAltarPos != null) {
            return tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, true)
        }
        return requiredAltarPower == 0
    }

    private fun consumeAltarPower(level: Level, ingredient: WitcheryPotionIngredient): Boolean {
        if (cachedAltarPos != null && level.getBlockEntity(cachedAltarPos!!) !is AltarBlockEntity) {
            cachedAltarPos = null
            setChanged()
            return false
        }

        val requiredAltarPower = ingredient.altarPower
        if (requiredAltarPower > 0 && cachedAltarPos != null) {
            return tryConsumeAltarPower(level, cachedAltarPos!!, requiredAltarPower, false)
        }
        return requiredAltarPower == 0
    }

    private fun consumeItem(level: Level, pos: BlockPos) {
        if (cachedAltarPos == null && level is ServerLevel) {
            cachedAltarPos = getAltarPos(level, blockPos)
        }

        level.getEntities(EntityType.ITEM, AABB(blockPos)) { true }.forEach { entity ->
            val item = entity.item
            val cacheForColorItem = item.copy()

            if (item.`is`(WitcheryItems.WOOD_ASH.get())) {
                fullReset()
                if (level is ServerLevel) {
                    PacketDistributor.sendToPlayersTrackingChunk(
                        level,
                        ChunkPos(pos),
                        SyncCauldronS2CPayload(pos, true)
                    )
                }
                item.shrink(1)
                setChanged()
            } else if (item.`is`(Items.NETHER_WART) &&
                cauldronCraftingRecipe == null &&
                cauldronBrewingRecipe == null &&
                inputItems.all { it.isEmpty }
            ) {
                PotionDataReloadListener.getIngredientFromItem(item)?.let { witcheryPotionItemCache.add(it) }
                forceColor(item)
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)
                item.shrink(1)
            } else {
                if (witcheryPotionItemCache.isNotEmpty()) {
                    PotionDataReloadListener.getIngredientFromItem(item)?.let { it ->
                        if (hasEnoughAltarPower(level, it) && WitcheryPotionItem.tryAddItemToPotion(
                                witcheryPotionItemCache,
                                it
                            )
                        ) {
                            consumeAltarPower(level, it)
                            forceColor(item)
                            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)
                            item.shrink(1)
                        } else {
                            level.playSound(
                                null,
                                pos,
                                SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR,
                                SoundSource.BLOCKS,
                                0.25f,
                                1f
                            )
                            level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.95f, 1f)
                            spawnFailParticle(level, pos)

                            fun randomSmallOffset(): Double = (level.getRandom()
                                .nextDouble() * 0.1 - 0.05).let { if (it < 0.001 && it > -0.001) 0.01 else it }

                            val dx = randomSmallOffset()
                            val dz = randomSmallOffset()
                            entity.deltaMovement = entity.deltaMovement.add(dx, 0.75, dz)
                        }
                    }
                } else {
                    val firstEmpty = getFirstEmptyIndex()
                    if (firstEmpty != -1) {
                        setItem(firstEmpty, item.split(1))
                        level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)
                        refreshCraftingAndBrewingRecipe(level)
                        updateColor(level, cacheForColorItem)
                    }
                }
            }

            setChanged()
        }
    }

    private fun forceColor(potionIngredientStack: ItemStack) {
        color = PotionDataReloadListener.getIngredientFromItem(potionIngredientStack)?.color ?: 0x5a2d0d
    }

    private fun updateColor(level: Level, cacheForColorItem: ItemStack) {
        var colorSet = false

        val allCraftingRecipes = level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())
        val allBrewingRecipes =
            level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())
        val nonEmptyItems = inputItems.filter { !it.isEmpty }

        allCraftingRecipes.forEach { recipe ->
            recipe.value.inputItems.forEach { ingredientWithColor ->
                val orderIsCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)
                if (ItemStack.isSameItem(ingredientWithColor.itemStack, cacheForColorItem) && orderIsCorrect) {
                    color = ingredientWithColor.color
                    colorSet = true
                }
            }
        }

        if (!colorSet) {
            allBrewingRecipes.forEach { recipe ->
                recipe.value.inputItems.forEach { ingredientWithColor ->
                    val orderIsCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)
                    if (ItemStack.isSameItem(ingredientWithColor.itemStack, cacheForColorItem) && orderIsCorrect) {
                        color = ingredientWithColor.color
                        colorSet = true
                    }
                }
            }
        }

        if (!colorSet) {
            color = 0x5a2d0d
        }
    }

    private fun craft(level: Level, pos: BlockPos) {
        val itemsToCraft = cauldronCraftingRecipe?.outputItems

        for (item in itemsToCraft ?: emptyList()) {
            val list = NonNullList.create<ItemStack>()
            list.add(item.copy())

            for (drop in list) {
                Containers.dropItemStack(level, pos.x + 0.5, pos.y + 1.1, pos.z + 0.5, drop)
            }
        }

        if (level is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos(pos), SyncCauldronS2CPayload(pos, false))
        }

        if (cauldronCraftingRecipe != null) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5f, 1.0f)
            spawnSmokeParticle(level, pos)
        }

        resetCauldronPartial()
    }

    fun resetCauldronPartial() {
        if (cauldronCraftingRecipe != null) {
            fluidTank.setFluid(FluidStack.EMPTY)
            brewItemOutput = ItemStack.EMPTY
        }

        if (cauldronBrewingRecipe != null) {
            brewItemOutput = cauldronBrewingRecipe!!.outputItem
        } else {
            color = WATER_COLOR
        }

        witcheryPotionItemCache = mutableListOf()
        clearContent()
        cauldronCraftingRecipe = null
        cauldronBrewingRecipe = null
        complete = false
        setChanged()
    }

    fun fullReset() {
        color = WATER_COLOR
        clearContent()
        witcheryPotionItemCache = mutableListOf()
        cauldronCraftingRecipe = null
        cauldronBrewingRecipe = null
        complete = false
        fluidTank.setFluid(FluidStack.EMPTY)
        brewItemOutput = ItemStack.EMPTY
        setChanged()
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        tag.put("FluidTank", fluidTank.writeToNBT(registries, CompoundTag()))
        return tag
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        return when {
            pStack.`is`(Items.FLINT_AND_STEEL) -> {
                playSound(level, pPlayer, blockPos, SoundEvents.FLINTANDSTEEL_USE)
                level!!.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, true), 11)
                level!!.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, blockPos)
                pStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pHand))
                ItemInteractionResult.SUCCESS
            }

            pStack.`is`(Items.GLASS_BOTTLE) -> handleGlassBottleInteraction(pPlayer, pStack)

            else -> {
                handleFluidInteraction(pPlayer, pStack, pHand)
                    ?: super.onUseWithItem(pPlayer, pStack, pHand)
            }
        }
    }

    private fun handleFluidInteraction(
        pPlayer: Player,
        pStack: ItemStack,
        pHand: InteractionHand
    ): ItemInteractionResult? {
        if (fluidTank.fluidAmount == fluidTank.capacity) return null

        val fluidHandler = level?.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, blockState, this, null)
        if (fluidHandler != null) {
            val result = FluidUtil.interactWithFluidHandler(pPlayer, pHand, fluidHandler)
            if (result) {
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        }

        when {
            pStack.`is`(Items.WATER_BUCKET) -> {
                playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_FILL)
                fluidTank.fill(FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE)
                if (!pPlayer.isCreative) {
                    pPlayer.setItemInHand(pHand, Items.BUCKET.defaultInstance)
                }
                setChanged()
                return ItemInteractionResult.SUCCESS
            }

            pStack.`is`(Items.POTION) -> {
                val potionContents = pStack.get(DataComponents.POTION_CONTENTS)
                if (potionContents?.`is`(Potions.WATER) == true) {
                    playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_FILL)
                    if (!pPlayer.isCreative) {
                        pPlayer.setItemInHand(pHand, Items.GLASS_BOTTLE.defaultInstance)
                    }

                    fluidTank.fill(
                        FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 3),
                        IFluidHandler.FluidAction.EXECUTE
                    )
                    setChanged()
                    return ItemInteractionResult.SUCCESS
                }
            }
        }

        return null
    }

    private fun handleGlassBottleInteraction(pPlayer: Player, pStack: ItemStack): ItemInteractionResult {
        val potionAmount = FluidType.BUCKET_VOLUME / 3

        if (witcheryPotionItemCache.isNotEmpty() && fluidTank.fluidAmount >= potionAmount) {
            pStack.shrink(1)
            WitcheryApi.makePlayerWitchy(pPlayer)
            val potion = WITCHERY_POTION.get().defaultInstance.apply {
                set(WITCHERY_POTION_CONTENT.get(), witcheryPotionItemCache)
            }
            val witchesPotion = createOutput(pPlayer, potion)

            if (level!!.random.nextFloat() < witchesPotion.first) {
                Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, witchesPotion.second)
            }
            if (level!!.random.nextFloat() < witchesPotion.third) {
                Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, witchesPotion.second)
            }
            Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, witchesPotion.second)

            fluidTank.drain(potionAmount, IFluidHandler.FluidAction.EXECUTE)
            playSound(level, pPlayer, blockPos, SoundEvents.ITEM_PICKUP, 0.5f)
            playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_EMPTY)
            if (fluidTank.fluidAmount < potionAmount) fullReset()
            return ItemInteractionResult.SUCCESS
        }
        if (!brewItemOutput.isEmpty && fluidTank.fluidAmount >= potionAmount) {
            WitcheryApi.makePlayerWitchy(pPlayer)
            pStack.shrink(1)
            val brewOutput = createOutput(pPlayer, brewItemOutput.copy())

            if (level != null) {
                if (level!!.random.nextFloat() < brewOutput.first) {
                    Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, brewOutput.second)
                }
                if (level!!.random.nextFloat() < brewOutput.third) {
                    Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, brewOutput.second)
                }
                Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, brewOutput.second)
            }

            fluidTank.drain(potionAmount, IFluidHandler.FluidAction.EXECUTE)
            playSound(level, pPlayer, blockPos, SoundEvents.ITEM_PICKUP, 0.5f)
            playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_EMPTY)
            if (fluidTank.fluidAmount < potionAmount) fullReset()
            setChanged()
            return ItemInteractionResult.SUCCESS
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private fun createOutput(pPlayer: Player, itemStack: ItemStack): Triple<Float, ItemStack, Float> {
        var bonus = 0f
        var thirdBonus = 0f
        if (pPlayer.getItemBySlot(EquipmentSlot.HEAD).`is`(WitcheryItems.WITCHES_HAT.get())) bonus += 0.35f
        if (pPlayer.getItemBySlot(EquipmentSlot.CHEST).`is`(WitcheryItems.WITCHES_ROBES.get())) bonus += 0.35f
        if (pPlayer.getItemBySlot(EquipmentSlot.CHEST).`is`(WitcheryItems.BABA_YAGAS_HAT.get())) {
            bonus += 0.25f
            thirdBonus += 0.25f
        }

        return Triple(bonus, itemStack, thirdBonus)
    }

    private fun playSound(level: Level?, player: Player, blockPos: BlockPos, sound: SoundEvent, volume: Float = 1.0f) {
        level!!.playSound(
            player,
            blockPos,
            sound,
            SoundSource.BLOCKS,
            volume,
            level.getRandom().nextFloat() * 0.4f + 0.8f
        )
    }

    private fun spawnSmokeParticle(level: Level, pos: BlockPos) {
        if (level is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos(pos), CauldronPoofS2CPayload(pos, color))
        }
    }

    private fun spawnFailParticle(level: Level, pos: BlockPos) {
        if (level is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(
                level,
                ChunkPos(pos),
                SpawnSmokePoofParticlesS2CPayload(pos.center)
            )
        }
    }

    private fun getFirstEmptyIndex(): Int {
        for (i in 0 until containerSize) {
            if (getItem(i).isEmpty) {
                return i
            }
        }
        return -1
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)

        if (pTag.contains("FluidTank")) {
            fluidTank.readFromNBT(pRegistries, pTag.getCompound("FluidTank"))
        }

        craftingProgressTicker = pTag.getInt("craftingProgressTicker")
        color = pTag.getInt("color")
        complete = pTag.getBoolean("complete")

        if (pTag.contains("Item", 10)) {
            val compoundTag: CompoundTag = pTag.getCompound("Item")
            brewItemOutput = ItemStack.parse(pRegistries, compoundTag).orElse(ItemStack.EMPTY) as ItemStack
        } else {
            brewItemOutput = ItemStack.EMPTY
        }

        this.inputItems = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(pTag, inputItems, pRegistries)

        if (pTag.contains("witcheryPotionItemCache", 9)) {
            val listTag = pTag.getList("witcheryPotionItemCache", 10)
            val decodeResult = WitcheryPotionIngredient.CODEC.listOf().parse(NbtOps.INSTANCE, listTag)

            decodeResult.resultOrPartial { _ ->
            }?.let {
                witcheryPotionItemCache = it.get().toMutableList()
            }
        }

        if (pTag.contains("altarPos")) {
            cachedAltarPos = NbtUtils.readBlockPos(pTag, "altarPos").get()
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("FluidTank", fluidTank.writeToNBT(registries, CompoundTag()))

        tag.putInt("craftingProgressTicker", craftingProgressTicker)
        tag.putInt("color", color)
        tag.putBoolean("complete", complete)

        if (!brewItemOutput.isEmpty) {
            tag.put("Item", brewItemOutput.save(registries))
        }

        ContainerHelper.saveAllItems(tag, inputItems, registries)

        val listResult = WitcheryPotionIngredient.CODEC.listOf().encodeStart(NbtOps.INSTANCE, witcheryPotionItemCache)
        listResult.resultOrPartial { _ -> }?.let { tag.put("witcheryPotionItemCache", it.get()) }

        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }
    }


    //INVENTORY IMPL
    override fun clearContent() {
        inputItems.clear()
    }

    override fun getContainerSize(): Int {
        return inputItems.size
    }

    override fun isEmpty(): Boolean {
        return inputItems.isEmpty()
    }

    override fun getItem(slot: Int): ItemStack {
        return inputItems[slot]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        return ContainerHelper.removeItem(inputItems, slot, amount)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return ContainerHelper.takeItem(inputItems, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        inputItems[slot] = stack
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    override fun getSlotsForFace(side: Direction) =
        (0..<inputItems.size).toList().toIntArray()

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?) = true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction) = false

    companion object {
        const val WATER_COLOR = 0x3f76e4
        const val PROGRESS_TICKS = 20 * 3

        private fun isOrderRight(inputItems: List<ItemStack>, recipeItems: List<ItemStackWithColor>?): Boolean {
            if (recipeItems == null) return false

            if (inputItems.size > recipeItems.size) return false

            for (index in inputItems.indices) {
                val inputItem = inputItems[index]

                val recipeItem = recipeItems.find { it.order == index }

                if (recipeItem == null || !ItemStack.isSameItem(recipeItem.itemStack, inputItem)) {
                    return false
                }
            }

            return true
        }
    }
}