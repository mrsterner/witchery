package dev.sterner.witchery.block.brazier

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.sterner.witchery.api.block.AltarPowerConsumer
import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.item.potion.WitcheryPotionItem.Companion.getMergedEffectModifier
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.*
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
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
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.EntityHitResult
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

    private var potionContents: PotionContents = PotionContents.EMPTY
    private var activePotionSpecialEffects: MutableList<Pair<ResourceLocation, Int>> = mutableListOf()
    private var potionEffectRadius = 8.0
    private var potionEffectRemainingTicks = 0

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

        if (potionEffectRemainingTicks > 0 && active) {
            potionEffectRemainingTicks--

            if (potionEffectRemainingTicks % 20 == 0) {
                applyPotionEffectsToNearbyEntities(level, pos)
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

            if (potionEffectRemainingTicks <= 0) {
                potionContents = PotionContents.EMPTY
                activePotionSpecialEffects.clear()
                setChanged()
            }
        }
    }
    
    private fun applyPotionEffectsToNearbyEntities(level: Level, pos: BlockPos) {
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
        if (items[0].`is`(WitcheryItems.WOOD_ASH)) {
            if (level != null && (pStack.`is`(Items.POTION) || pStack.`is`(Items.SPLASH_POTION) || pStack.`is`(Items.LINGERING_POTION))) {

                if (pStack.has(DataComponents.POTION_CONTENTS) && pStack.get(DataComponents.POTION_CONTENTS) != PotionContents.EMPTY) {
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
                val potionContentList = pStack.get(WITCHERY_POTION_CONTENT.get())
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
        }

        if (level != null && (pPlayer.mainHandItem.`is`(Items.FLINT_AND_STEEL) || pPlayer.mainHandItem.`is`(Items.FIRE_CHARGE))) {
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
            } else if (potionContents != PotionContents.EMPTY) {
                pStack.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND)
                level?.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS)
                active = true
                level!!.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, true))
                setChanged()
                return ItemInteractionResult.SUCCESS
            }
        } else {
            for (i in items.indices) {
                if (items[i].isEmpty) {
                    items[i] = pStack.copy()
                    items[i].count = 1
                    pStack.shrink(1)
                    setChanged()
                    return ItemInteractionResult.SUCCESS
                }
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

        if (pTag.contains("potionEffectTicks")) {
            this.potionEffectRemainingTicks = pTag.getInt("potionEffectTicks")
            this.potionEffectRadius = pTag.getDouble("potionEffectRadius")

            if (pTag.contains("potionContents")) {
                val registryOps = this.level!!.registryAccess().createSerializationContext(NbtOps.INSTANCE)
                PotionContents.CODEC.parse(registryOps, pTag.get("potionContents"))
                    .resultOrPartial { error ->
                    }
                    .ifPresent { contents ->
                        this.potionContents = contents
                    }
            }

            if (pTag.contains("specialEffects")) {
                val specialEffectsTag = pTag.getList("specialEffects", 10)
                this.activePotionSpecialEffects.clear()
                
                for (i in 0 until specialEffectsTag.size) {
                    val specialEffectTag = specialEffectsTag.getCompound(i)
                    val resourceLocation = ResourceLocation.parse(specialEffectTag.getString("id"))
                    val amplifier = specialEffectTag.getInt("amplifier")
                    this.activePotionSpecialEffects.add(Pair(resourceLocation, amplifier))
                }
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, this.items, registries)
        if (cachedAltarPos != null) {
            tag.put("altarPos", NbtUtils.writeBlockPos(cachedAltarPos!!))
        }
        tag.putBoolean("active", active)
        tag.putInt("summoning", this.summoningTicker)

        if (potionEffectRemainingTicks > 0) {
            tag.putInt("potionEffectTicks", potionEffectRemainingTicks)
            tag.putDouble("potionEffectRadius", potionEffectRadius)

            if (potionContents != PotionContents.EMPTY) {
                val registryOps = this.level!!.registryAccess().createSerializationContext(NbtOps.INSTANCE)
                PotionContents.CODEC.encodeStart(registryOps, potionContents)
                    .resultOrPartial { error -> }
                    .ifPresent { encoded: Tag -> 
                        tag.put("potionContents", encoded)
                    }
            }

            if (activePotionSpecialEffects.isNotEmpty()) {
                val specialEffectsTag = CompoundTag()
                
                for ((index, pair) in activePotionSpecialEffects.withIndex()) {
                    val specialEffectTag = CompoundTag()
                    specialEffectTag.putString("id", pair.first.toString())
                    specialEffectTag.putInt("amplifier", pair.second)
                    specialEffectsTag.put(index.toString(), specialEffectTag)
                }
                
                tag.put("specialEffects", specialEffectsTag)
            }
        }
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