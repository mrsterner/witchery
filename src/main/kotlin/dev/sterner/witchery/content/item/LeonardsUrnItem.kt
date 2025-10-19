package dev.sterner.witchery.content.item

import dev.sterner.witchery.client.tooltip.UrnTooltipComponent
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.features.brewing.brew.BrewItem
import dev.sterner.witchery.features.brewing.brew.ThrowableBrewItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.TooltipFlag
import java.util.Optional

class LeonardsUrnItem : Item(Properties().stacksTo(1)) {

    override fun overrideStackedOnOther(
        stack: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player
    ): Boolean {
        if (action != ClickAction.SECONDARY) {
            return false
        }

        val slotItem = slot.item
        if (slotItem.isEmpty) {

            val potions = getStoredPotions(stack)
            if (potions.isNotEmpty()) {
                val removed = potions.removeAt(potions.size - 1)
                setStoredPotions(stack, potions)
                slot.safeInsert(removed)
                playRemoveSound(player)
                return true
            }
        } else if (slotItem.item is BrewItem || slotItem.item is ThrowableBrewItem || slotItem.item is PotionItem) {

            var level = 1
            if (stack.has(WitcheryDataComponents.URN_LEVEL)) {
                level += stack.get(WitcheryDataComponents.URN_LEVEL)!!
            }
            val potions = getStoredPotions(stack)
            if (potions.size < level) {
                val toAdd = slotItem.split(1)
                potions.add(toAdd)
                setStoredPotions(stack, potions)
                playInsertSound(player)
                return true
            }
        }

        return false
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess
    ): Boolean {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false
        }

        if (other.isEmpty) {

            val potions = getStoredPotions(stack)
            if (potions.isNotEmpty()) {
                val removed = potions.removeAt(potions.size - 1)
                setStoredPotions(stack, potions)
                access.set(removed)
                playRemoveSound(player)
                return true
            }
        } else if (other.item is BrewItem || other.item is ThrowableBrewItem || other.item is PotionItem) {
            var level = 1
            if (stack.has(WitcheryDataComponents.URN_LEVEL)) {
                level += stack.get(WitcheryDataComponents.URN_LEVEL)!!
            }
            val potions = getStoredPotions(stack)
            if (potions.size < level) {
                val toAdd = other.split(1)
                potions.add(toAdd)
                setStoredPotions(stack, potions)
                playInsertSound(player)
                return true
            }
        }

        return false
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val potions = getStoredPotions(stack)
        var level = 1
        if (stack.has(WitcheryDataComponents.URN_LEVEL)) {
            level += stack.get(WitcheryDataComponents.URN_LEVEL)!!
        }
        tooltipComponents.add(
            Component.translatable("item.witchery.leonards_urn.potions", potions.size, level)
                .withStyle(ChatFormatting.GRAY)
        )

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        val potions = getStoredPotions(stack)
        return if (potions.isEmpty()) {
            Optional.empty()
        } else {
            Optional.of(UrnTooltipComponent(potions))
        }
    }

    private fun playInsertSound(player: Player) {
        player.playSound(
            SoundEvents.BOTTLE_FILL,
            1.0f,
            1.0f + player.level().random.nextFloat() * 0.4f
        )
    }

    private fun playRemoveSound(player: Player) {
        player.playSound(
            SoundEvents.BOTTLE_EMPTY,
            1.0f,
            0.8f + player.level().random.nextFloat() * 0.4f
        )
    }

    companion object {
        fun getStoredPotions(stack: ItemStack): MutableList<ItemStack> {
            val list = stack.get(WitcheryDataComponents.URN_POTIONS.get()) ?: return mutableListOf()
            return list.toMutableList()
        }

        fun setStoredPotions(stack: ItemStack, potions: List<ItemStack>) {
            if (potions.isEmpty()) {
                stack.remove(WitcheryDataComponents.URN_POTIONS.get())
            } else {
                stack.set(WitcheryDataComponents.URN_POTIONS.get(), potions)
            }
        }

        fun hasUrn(player: Player): Boolean {
            return player.inventory.items.any { it.item is LeonardsUrnItem }
        }

        fun findUrn(player: Player): ItemStack? {
            return player.inventory.items.firstOrNull { it.item is LeonardsUrnItem }
        }
    }
}