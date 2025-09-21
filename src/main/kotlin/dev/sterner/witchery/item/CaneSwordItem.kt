package dev.sterner.witchery.item

import dev.sterner.witchery.api.client.BloodPoolComponent
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.data_attachment.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.WitcheryConstants
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import java.util.*
import kotlin.math.min

class CaneSwordItem(tier: Tier, properties: Properties) : SwordItem(tier, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player
        val hand = context.hand
        if (player != null) {
            transformCane(level, player, hand, player.mainHandItem)
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }

    private fun transformCane(level: Level, player: Player, hand: InteractionHand, itemStack: ItemStack) {
        if (!level.isClientSide && !player.isShiftKeyDown && hand == InteractionHand.MAIN_HAND) {
            val item = itemStack.copy()
            item.remove(DataComponents.ATTRIBUTE_MODIFIERS)
            if (itemStack.has(WitcheryDataComponents.UNSHEETED.get())) {
                val unsheeted = itemStack.get(WitcheryDataComponents.UNSHEETED.get())!!
                item.set(WitcheryDataComponents.UNSHEETED.get(), !unsheeted)
                if (!unsheeted) {
                    item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                }
            } else {
                item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                item.set(WitcheryDataComponents.UNSHEETED.get(), true)
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, item)
        }
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun getDrinkingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun getEatingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)
        if (livingEntity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(livingEntity, stack)
            livingEntity.awardStat(Stats.ITEM_USED[this])

            val storedBlood = stack.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0
            if (storedBlood > 0) {

                val playerBloodPool = BloodPoolLivingEntityAttachment.getData(livingEntity)
                val maxBlood = playerBloodPool.maxBlood
                val currentBlood = playerBloodPool.bloodPool

                val transferableBlood = minOf(storedBlood, maxBlood - currentBlood)
                if (transferableBlood > 0) {
                    BloodPoolHandler.increaseBlood(livingEntity, transferableBlood)
                    stack.set(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get(), storedBlood - transferableBlood)
                    return stack
                }
            }
        }

        return stack
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if ((player.mainHandItem.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0) > 0 &&
            player.mainHandItem.get(WitcheryDataComponents.UNSHEETED.get()) != true &&
            player.isShiftKeyDown
        ) {
            return ItemUtils.startUsingInstantly(level, player, usedHand)
        }
        if (!player.isShiftKeyDown) {
            transformCane(level, player, usedHand, player.mainHandItem)
        }

        return super.use(level, player, usedHand)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(BloodPoolComponent(stack))
    }

    companion object {

        const val MAX_STORED_BLOOD = WitcheryConstants.BLOOD_DROP * 2



        fun harvestBlood(livingEntity: LivingEntity?, damageSource: DamageSource?) {
            if (livingEntity != null && BloodPoolReloadListener.BLOOD_PAIR.contains(livingEntity.type)) {
                if (damageSource?.entity is Player) {
                    val player = damageSource.entity as Player
                    if (player.mainHandItem.`is`(WitcheryItems.CANE_SWORD.get())) {
                        val cane = player.mainHandItem.copy()
                        val drops = BloodPoolReloadListener.BLOOD_PAIR[livingEntity.type]!!.bloodDrops
                        val absorbedAmount = (drops * WitcheryConstants.BLOOD_DROP) / 20
                        val oldBloodValue = cane.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0
                        val finalValue = min(oldBloodValue + absorbedAmount, MAX_STORED_BLOOD)
                        cane.set(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get(), finalValue)
                        player.setItemInHand(InteractionHand.MAIN_HAND, cane)
                    }
                }
            }
        }
    }
}