package dev.sterner.witchery.block.ritual

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom.pos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB


class GoldenChalkBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.GOLDEN_CHALK.get(), blockPos, blockState) {

    var ritualRecipe: RitualRecipe? = null
    private var shouldRun: Boolean = false

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        super.tick(level, pos, state)

        if (level.isClientSide) {
            return
        }

        if (!shouldRun) {
            return
        }


    }

    override fun init(level: Level, pos: BlockPos, state: BlockState) {
        super.init(level, pos, state)
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        if (ritualRecipe == null) {
            val items: List<ItemEntity> = pPlayer.level().getEntities(EntityType.ITEM, AABB(blockPos).inflate(3.0, 0.0, 3.0)) { true }
            val entities = pPlayer.level().getEntitiesOfClass(LivingEntity::class.java, AABB(blockPos).inflate(4.0, 1.0, 4.0)) { true }

            val recipes = level?.recipeManager?.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())
            val validItemRecipes = recipes?.filter { it.value.inputItems.containsAll(items.map { entity -> entity.item }) }
            val validSacrifices = validItemRecipes?.filter { it -> entities.map { it.type }.containsAll(it.value.inputEntities) }

            if (validSacrifices != null) {
                if (validSacrifices.isNotEmpty()) {
                    ritualRecipe = validItemRecipes[0].value
                    shouldRun = true
                    setChanged()
                    level?.playSound(pPlayer, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
                    level?.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS)
                } else {
                    level?.playSound(pPlayer, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                    level?.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS)
                }
            }
        }

        return super.onUseWithoutItem(pPlayer)
    }

    override fun onUseWithItem(pPlayer: Player, pStack: ItemStack, pHand: InteractionHand): ItemInteractionResult {
        return super.onUseWithItem(pPlayer, pStack, pHand)
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
    }
}