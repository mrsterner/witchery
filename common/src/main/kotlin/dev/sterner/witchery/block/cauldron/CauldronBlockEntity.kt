package dev.sterner.witchery.block.cauldron

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.FluidStackHooks
import dev.architectury.platform.Platform
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.api.fluid.WitcheryFluidTank
import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.data.PotionDataHandler
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.item.potion.WitcheryPotionItem.Companion.getTotalEffectValues
import dev.sterner.witchery.payload.*
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryItems.WITCHERY_POTION
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
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
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.PointedDripstoneBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d


class CauldronBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.CAULDRON.get(), CauldronBlock.STRUCTURE.get(),
    pos, state
), Container, WorldlyContainer {

    private var cauldronCraftingRecipe: CauldronCraftingRecipe? = null
    private var cauldronBrewingRecipe: CauldronBrewingRecipe? = null
    private var witcheryPotionItemCache: MutableList<WitcheryPotionIngredient> = mutableListOf()
    private var inputItems: NonNullList<ItemStack> = NonNullList.withSize(12, ItemStack.EMPTY)
    private var craftingProgressTicker = 0
    private var brewItemOutput: ItemStack = ItemStack.EMPTY

    var color = WATER_COLOR
    var fluidTank = WitcheryFluidTank(this)
    private var complete = false

    override fun init(level: Level, pos: BlockPos, state: BlockState) {
        refreshCraftingAndBrewingRecipe(level)
    }

    private fun refreshCraftingAndBrewingRecipe(level: Level) {
        val allRecipesOfType = level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())
        val nonEmptyItems = inputItems.filter { !it.isEmpty }

        // Find the possible recipe based on current input items
        val possibleRecipe =
            allRecipesOfType.firstOrNull { it.value.matches(MultipleItemRecipeInput(nonEmptyItems), level) }

        // If a recipe is found and the order is correct, set cauldronCraftingRecipe
        possibleRecipe?.let { recipe ->
            val isOrderCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)

            if (isOrderCorrect) {
                cauldronCraftingRecipe = recipe.value // Set the recipe even if incomplete
                complete = nonEmptyItems.size == recipe.value.inputItems.size // Only complete if all items are matched
            } else {
                refreshBrewingRecipe(level)
            }
        } ?: run {
            // If no crafting recipe matches, try the brewing recipe
            refreshBrewingRecipe(level)
        }

        setChanged()
    }

    private fun refreshBrewingRecipe(level: Level) {
        val allRecipesOfType = level.recipeManager
            .getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())
            .filter { recipe ->
                recipe.value.dimensionKey.isNotEmpty() && recipe.value.dimensionKey.contains(level.dimension().location().toString())
            }
        val nonEmptyItems = inputItems.filter { !it.isEmpty }

        // Find the possible recipe based on current input items
        val possibleRecipe =
            allRecipesOfType.firstOrNull { it.value.matches(MultipleItemRecipeInput(nonEmptyItems), level) }

        // If a recipe is found and the order is correct, set cauldronBrewingRecipe
        possibleRecipe?.let { recipe ->
            val isOrderCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)

            if (isOrderCorrect) {
                cauldronBrewingRecipe = recipe.value // Set the recipe even if incomplete
                complete = nonEmptyItems.size == recipe.value.inputItems.size // Only complete if all items are matched
            } else {
                cauldronBrewingRecipe = null // Reset if the order is wrong
                complete = false
            }
        } ?: run {
            // If no brewing recipe matches, reset to null and incomplete
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

        val amount = 10L * if (Platform.isFabric()) 81 else 1
        this.fluidTank.fill(FluidStack.create(fluid, amount), false)
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.random.nextFloat() < 0.005)
            handleDripstone(level, pos)

        if (level.isClientSide || !state.getValue(BlockStateProperties.LIT)) {
            return
        }

        if (level.gameTime % 4 == 0L && !complete && !fluidTank.isEmpty()) {
            consumeItem(level, pos)
        }

        if (!brewItemOutput.isEmpty) {
            val randX = pos.x + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25)
            val randY = (pos.y + 1.0)
            val randZ = pos.z + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25)
            WitcheryPayloads.sendToPlayers(
                level,
                blockPos,
                CauldronEffectParticleS2CPayload(Vector3d(randX, randY, randZ), color)
            )
        }

        // Handle crafting progress and execution
        if (cauldronCraftingRecipe != null || cauldronBrewingRecipe != null) {
            // Only start ticking when the recipe is complete
            if (complete) {
                if (craftingProgressTicker < PROGRESS_TICKS) {
                    craftingProgressTicker++
                    setChanged()
                } else {
                    craftingProgressTicker = 0
                    craft(level, pos)
                }
            }
        }
    }

    /**
     * Checks within the cauldron if there's any item entities, adds them to the inventory,
     * check for recipes and applies appropriate color
     */
    private fun consumeItem(level: Level, pos: BlockPos) {
        level.getEntities(EntityType.ITEM, AABB(blockPos)) { true }.forEach { entity ->
            val item = entity.item
            val cacheForColorItem = item.copy()

            // Handle Wood Ash - Reset potion and recipes
            if (item.`is`(WitcheryItems.WOOD_ASH.get())) {
                fullReset()
                WitcheryPayloads.sendToPlayers(level, pos, SyncCauldronS2CPacket(pos, true))
                item.shrink(1)
                setChanged()
            }
            // Handle Slime Ball - Start potion brewing process
            else if (item.`is`(Items.SLIME_BALL) && cauldronCraftingRecipe == null && cauldronBrewingRecipe == null) {
                PotionDataHandler.getIngredientFromItem(item)?.let { witcheryPotionItemCache.add(it) }
                updateColor(level, item)
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)
                item.shrink(1)
            } else {
                // Handle other ingredients for potion brewing
                if (witcheryPotionItemCache.isNotEmpty()) {
                    PotionDataHandler.getIngredientFromItem(item)?.let { it ->
                        if(WitcheryPotionItem.tryAddItemToPotion(witcheryPotionItemCache, it)) {
                            updateColor(level, item)
                            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)
                            item.shrink(1)
                        } else {
                            level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR, SoundSource.BLOCKS, 0.25f, 1f)
                            level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.95f, 1f)
                            spawnFailParticle(level, pos)

                            fun randomSmallOffset(): Double = (level.getRandom().nextDouble() * 0.1 - 0.05).let { if (it < 0.001 && it > -0.001) 0.01 else it }

                            val dx = randomSmallOffset()
                            val dz = randomSmallOffset()
                            entity.deltaMovement = entity.deltaMovement.add(dx, 0.75, dz)

                        }
                    }

                } else {
                    // Handle normal cauldron recipe behavior (crafting or brewing)
                    val firstEmpty = getFirstEmptyIndex()
                    if (firstEmpty != -1) {
                        setItem(firstEmpty, item.split(1))
                        level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.35f, 1f)

                        // Refresh recipe to match current inputItems
                        refreshCraftingAndBrewingRecipe(level)

                        updateColor(level, cacheForColorItem)
                    }
                }
            }

            setChanged()
        }
    }

    private fun updateColor(level: Level, cacheForColorItem: ItemStack) {
        // Default color to brown (indicating no correct order match)
        var colorSet = false

        // Get all recipes for crafting and brewing
        val allCraftingRecipes = level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())
        val allBrewingRecipes =
            level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())
        val nonEmptyItems = inputItems.filter { !it.isEmpty }

        // Check crafting recipes
        allCraftingRecipes.forEach { recipe ->
            recipe.value.inputItems.forEach { ingredientWithColor ->
                // Check if the ingredient matches and the order is correct
                val orderIsCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)
                if (ItemStack.isSameItem(ingredientWithColor.itemStack, cacheForColorItem) && orderIsCorrect) {
                    color = ingredientWithColor.color // Set color based on the matched ingredient
                    colorSet = true // Flag that a color was successfully set
                }
            }
        }

        // Check brewing recipes if no crafting match was found
        if (!colorSet) {
            allBrewingRecipes.forEach { recipe ->
                recipe.value.inputItems.forEach { ingredientWithColor ->
                    // Check if the ingredient matches and the order is correct
                    val orderIsCorrect = isOrderRight(nonEmptyItems, recipe.value.inputItems)
                    if (ItemStack.isSameItem(ingredientWithColor.itemStack, cacheForColorItem) && orderIsCorrect) {
                        color = ingredientWithColor.color // Set color based on the matched ingredient
                        colorSet = true // Flag that a color was successfully set
                    }
                }
            }
        }

        // If no recipe fully or partially matches, set the color to brown
        if (!colorSet) {
            color = 0x5a2d0d // Set color to brown if no matching order is found
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

        WitcheryPayloads.sendToPlayers(level, pos, SyncCauldronS2CPacket(pos, false))

        if (cauldronCraftingRecipe != null) {
            level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5f, 1.0f)
            spawnSmokeParticle(level, pos)
        }

        resetCauldronPartial()
    }

    fun resetCauldronPartial() {
        if (cauldronCraftingRecipe != null) {
            fluidTank = WitcheryFluidTank(this)
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
        fluidTank = WitcheryFluidTank(this)
        fluidTank.fluid = FluidStack.empty()
        brewItemOutput = ItemStack.EMPTY
        setChanged()
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return fluidTank.getUpdateTag(super.getUpdateTag(registries), registries)
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        if (pStack.`is`(Items.FLINT_AND_STEEL)) {
            playSound(level, pPlayer, blockPos, SoundEvents.FLINTANDSTEEL_USE)
            level!!.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, true), 11)
            level!!.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, blockPos)
            pStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pHand))

            return ItemInteractionResult.SUCCESS
        }
        if (fluidTank.capacity != fluidTank.getFluidAmount()) {
            if (pStack.`is`(Items.WATER_BUCKET)) {
                playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_FILL)
                fluidTank.fill(FluidStack.create(Fluids.WATER, FluidStack.bucketAmount()), false)
                setChanged()
                return ItemInteractionResult.SUCCESS
            }

            if (pStack.`is`(Items.POTION)) {
                val potionContents = pStack.get(DataComponents.POTION_CONTENTS)
                if (potionContents?.`is`(Potions.WATER) == true) {
                    playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_FILL)
                    if (!pPlayer.isCreative) {
                        pPlayer.setItemInHand(pHand, Items.GLASS_BOTTLE.defaultInstance)
                    }
                    val currentFluidAmount = fluidTank.getFluidAmount()
                    fluidTank.fill(
                        FluidStack.create(
                            Fluids.WATER,
                            currentFluidAmount + FluidStack.bucketAmount() / 3
                        ),
                        false
                    )
                    setChanged()
                    return ItemInteractionResult.SUCCESS
                }
            }
        }
        if (pStack.`is`(Items.GLASS_BOTTLE)) {

            if (witcheryPotionItemCache.isNotEmpty() && fluidTank.getFluidAmount() >= (FluidStackHooks.bucketAmount() / 3)) {
                pStack.shrink(1)
                WitcheryApi.makePlayerWitchy(pPlayer)

                val witchesPotion = WITCHERY_POTION.get().defaultInstance

                val effectDurations = witcheryPotionItemCache
                    .map { ingredient ->
                        val (duration, amplifier) = getTotalEffectValues(ingredient, witcheryPotionItemCache)
                        WitcheryDataComponents.DurationAmplifier(duration, amplifier)
                    }

                val finalPotionDataList = effectDurations.zip(witcheryPotionItemCache) { durationAmplifier, ingredient ->
                    WitcheryDataComponents.FinalPotionData(durationAmplifier, ingredient)
                }

                witchesPotion.set(WITCHERY_POTION_CONTENT.get(), finalPotionDataList)


                Containers.dropItemStack(
                    level!!,
                    pPlayer.x,
                    pPlayer.y,
                    pPlayer.z,
                    witchesPotion
                )

                fluidTank.drain(FluidStackHooks.bucketAmount() / 3, false)
                playSound(level, pPlayer, blockPos, SoundEvents.ITEM_PICKUP, 0.5f)
                playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_EMPTY)
                if (fluidTank.getFluidAmount() < (FluidStackHooks.bucketAmount() / 3)) {
                    fullReset()
                }
                return ItemInteractionResult.SUCCESS
            }

            if (!brewItemOutput.isEmpty && fluidTank.getFluidAmount() >= (FluidStackHooks.bucketAmount() / 3)) {

                WitcheryApi.makePlayerWitchy(pPlayer)

                var thirdBonus = 0f
                var bonus = 0f
                if (pPlayer.getItemBySlot(EquipmentSlot.HEAD).`is`(WitcheryItems.WITCHES_HAT.get())) {
                    bonus += 0.35f
                }
                if (pPlayer.getItemBySlot(EquipmentSlot.CHEST).`is`(WitcheryItems.WITCHES_ROBES.get())) {
                    bonus += 0.35f
                }
                if (pPlayer.getItemBySlot(EquipmentSlot.CHEST).`is`(WitcheryItems.BABA_YAGAS_HAT.get())) {
                    bonus += 0.25f
                    thirdBonus += 0.25f
                }
                pStack.shrink(1)

                if (level != null) {
                    if (level!!.random.nextFloat() < bonus) {
                        Containers.dropItemStack(
                            level!!,
                            pPlayer.x,
                            pPlayer.y,
                            pPlayer.z,
                            ItemStack(brewItemOutput.copy().item)
                        )
                    }

                    if (level!!.random.nextFloat() < thirdBonus) {
                        Containers.dropItemStack(
                            level!!,
                            pPlayer.x,
                            pPlayer.y,
                            pPlayer.z,
                            ItemStack(brewItemOutput.copy().item)
                        )
                    }
                    Containers.dropItemStack(level!!, pPlayer.x, pPlayer.y, pPlayer.z, ItemStack(brewItemOutput.copy().item))
                }
                fluidTank.drain(FluidStackHooks.bucketAmount() / 3, false)
                playSound(level, pPlayer, blockPos, SoundEvents.ITEM_PICKUP, 0.5f)
                playSound(level, pPlayer, blockPos, SoundEvents.BUCKET_EMPTY)
                if (fluidTank.getFluidAmount() < (FluidStackHooks.bucketAmount() / 3)) {
                    fullReset()
                }
                setChanged()

                return ItemInteractionResult.SUCCESS
            }
        }

        return super.onUseWithItem(pPlayer, pStack, pHand)
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
        WitcheryPayloads.sendToPlayers(level, pos, CauldronPoofS2CPacket(pos, color))
    }

    private fun spawnFailParticle(level: Level, pos: BlockPos) {
        WitcheryPayloads.sendToPlayers(level, pos, SpawnSmokePoofParticles(pos.center))
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
        fluidTank.loadFluidAdditional(pTag, pRegistries)
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

    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        fluidTank.saveFluidAdditional(tag, registries)
        tag.putInt("craftingProgressTicker", craftingProgressTicker)
        tag.putInt("color", color)
        tag.putBoolean("complete", complete)
        if (!brewItemOutput.isEmpty) {
            tag.put("Item", brewItemOutput.save(registries))
        }
        ContainerHelper.saveAllItems(tag, inputItems, registries)

        val listResult = WitcheryPotionIngredient.CODEC.listOf().encodeStart(NbtOps.INSTANCE, witcheryPotionItemCache)
        listResult.resultOrPartial { _ -> }?.let { tag.put("witcheryPotionItemCache", it.get()) }
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

            // Check if the number of input items is larger than the recipe items
            if (inputItems.size > recipeItems.size) return false

            // Iterate through the input items
            for (index in inputItems.indices) {
                val inputItem = inputItems[index]

                // Check if the corresponding recipe item order matches the index
                val recipeItem = recipeItems.find { it.order == index }

                // If there's no recipe item at this order or the input item doesn't match the ingredient, return false
                if (recipeItem == null || !ItemStack.isSameItem(recipeItem.itemStack, inputItem)) {
                    return false
                }
            }

            // If all items match, return true
            return true
        }
    }
}