package dev.sterner.witchery.item

import dev.architectury.event.EventResult
import dev.sterner.witchery.api.RenderUtils
import dev.sterner.witchery.api.WitcheryTooltipComponent
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.handler.vampire.VampireLeveling
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
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
        if (!context.level.isClientSide && context.player?.isShiftKeyDown != true && context.hand == InteractionHand.MAIN_HAND) {
            val item = context.itemInHand.copy()
            item.remove(DataComponents.ATTRIBUTE_MODIFIERS)
            if (context.itemInHand.has(WitcheryDataComponents.UNSHEETED.get())) {
                val unsheeted = context.itemInHand.get(WitcheryDataComponents.UNSHEETED.get())!!
                item.set(WitcheryDataComponents.UNSHEETED.get(), !unsheeted)
                if (!unsheeted) {
                    item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                }
            } else {
                item.set(DataComponents.ATTRIBUTE_MODIFIERS, createAttributes(Tiers.IRON, 4, -2.4F))
                item.set(WitcheryDataComponents.UNSHEETED.get(), true)
            }
            context.player!!.setItemInHand(InteractionHand.MAIN_HAND, item)
        }

        return super.useOn(context)
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
                    BloodPoolLivingEntityAttachment.increaseBlood(livingEntity, transferableBlood)
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

        return super.use(level, player, usedHand)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(BloodPoolComponent(stack))
    }

    companion object {

        const val MAX_STORED_BLOOD = 300 * 2

        fun harvestBlood(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
            if (livingEntity != null && BloodPoolHandler.BLOOD_PAIR.contains(livingEntity.type)) {
                if (damageSource?.entity is Player) {
                    val player = damageSource.entity as Player
                    if (player.mainHandItem.`is`(WitcheryItems.CANE_SWORD.get())) {
                        val cane = player.mainHandItem.copy()
                        val drops = BloodPoolHandler.BLOOD_PAIR[livingEntity.type]!!.bloodDrops
                        val absorbedAmount = (drops * 300) / 20
                        val oldBloodValue = cane.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0
                        val finalValue = min(oldBloodValue + absorbedAmount, MAX_STORED_BLOOD)
                        cane.set(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get(), finalValue)
                        player.setItemInHand(InteractionHand.MAIN_HAND ,cane)
                    }
                }
            }

            return EventResult.pass()
        }
    }

    class BloodPoolComponent(val stack: ItemStack): WitcheryTooltipComponent<BloodPoolComponent, ClientBloodPoolComponent>() {
        override fun getClientTooltipComponent(): ClientBloodPoolComponent {
            return ClientBloodPoolComponent(stack)
        }
    }

    class ClientBloodPoolComponent(val stack: ItemStack): ClientTooltipComponent {

        override fun getHeight(): Int {
            return 12
        }

        override fun getWidth(font: Font): Int {
            return 18
        }

        override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
            val bl = stack.has(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get())
            if (bl) {
                val amount = stack.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0
                RenderUtils.innerRenderBlood(guiGraphics, MAX_STORED_BLOOD, amount, y, x + 14)
            }

            super.renderImage(font, x, y, guiGraphics)
        }
    }
}