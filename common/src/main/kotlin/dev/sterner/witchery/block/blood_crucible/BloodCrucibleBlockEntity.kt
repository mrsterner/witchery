package dev.sterner.witchery.block.blood_crucible

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.item.BoneNeedleItem
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.BlockState
import java.util.UUID
import kotlin.text.compareTo

class BloodCrucibleBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.BLOOD_CRUCIBLE.get(), blockPos, blockState) {

    private val BLOOD_TRANSFER_AMOUNT = 300 // 1 full blood drop

    private var bloodAmount: Int = 0
    private val maxBloodStorage: Int = BLOOD_TRANSFER_AMOUNT * 16
    
    fun addBlood(amount: Int) {
        bloodAmount = (bloodAmount + amount).coerceAtMost(maxBloodStorage)
        setChanged()
    }

    fun removeBlood(amount: Int): Int {
        val removed = amount.coerceAtMost(bloodAmount)
        bloodAmount -= removed
        setChanged()
        return removed
    }

    fun getBloodAmount(): Int {
        return bloodAmount
    }

    fun bloodPercent(): Double {
        return bloodAmount.toDouble() / maxBloodStorage.toDouble()
    }

    fun handleWineGlass(pPlayer: Player, item: ItemStack) {
        val bl = item.has(WitcheryDataComponents.BLOOD.get())

        if (bl) {
            if (getBloodAmount() <= maxBloodStorage - BLOOD_TRANSFER_AMOUNT) {
                addBlood(BLOOD_TRANSFER_AMOUNT)
                WitcheryUtil.addItemToInventoryAndConsume(pPlayer, InteractionHand.MAIN_HAND,
                    WitcheryItems.WINE_GLASS.get().defaultInstance
                )
            }
        } else {
            if (getBloodAmount() >= BLOOD_TRANSFER_AMOUNT) {
                val wineBlood = WitcheryItems.WINE_GLASS.get().defaultInstance
                wineBlood.set(WitcheryDataComponents.BLOOD.get(), UUID.randomUUID())
                removeBlood(BLOOD_TRANSFER_AMOUNT)
                WitcheryUtil.addItemToInventoryAndConsume(pPlayer, InteractionHand.MAIN_HAND, wineBlood)
            }
        }
    }

    override fun onUseWithoutItem(pPlayer: Player): InteractionResult {
        val level = pPlayer.level()

        val vampData = VampirePlayerAttachment.getData(pPlayer)
        if (vampData.getVampireLevel() <= 0) {
            return InteractionResult.PASS
        }

        val playerBloodData = BloodPoolLivingEntityAttachment.getData(pPlayer)
        if (playerBloodData.bloodPool >= playerBloodData.maxBlood) {
            return InteractionResult.PASS
        }

        if (getBloodAmount() <= 0) {
            return InteractionResult.PASS
        }

        val neededBlood = playerBloodData.maxBlood - playerBloodData.bloodPool
        val availableBlood = getBloodAmount()
        val transferAmount = minOf(neededBlood, availableBlood, BLOOD_TRANSFER_AMOUNT)

        if (transferAmount <= 0) {
            return InteractionResult.PASS
        }

        if (!level.isClientSide) {
            removeBlood(transferAmount)
            BloodPoolHandler.increaseBlood(pPlayer, transferAmount)

            level.playSound(
                null,
                blockPos,
                SoundEvents.GENERIC_DRINK,
                SoundSource.PLAYERS,
                0.5f,
                0.9f + level.random.nextFloat() * 0.2f
            )
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("BloodAmount", bloodAmount)
    }

    override fun loadAdditional(
        pTag: CompoundTag,
        pRegistries: HolderLookup.Provider
    ) {
        super.loadAdditional(pTag, pRegistries)
        bloodAmount = pTag.getInt("BloodAmount")
    }
}