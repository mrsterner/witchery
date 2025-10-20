package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.features.coven.CovenHandler
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import java.util.LinkedList
import java.util.function.Supplier

class CovenContractItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)

        if (livingEntity is Player && !level.isClientSide) {
            val componentType = WitcheryDataComponents.PLAYER_UUID_ORDERED_LIST.get()
            val list = stack.get(componentType)?.toMutableList() ?: mutableListOf()

            if (list.size < 8 && !list.any { it.first == livingEntity.uuid }) {
                list.add(Pair(livingEntity.uuid, livingEntity.name.string))
                stack.set(componentType, LinkedList(list))
            }

            val leader = level.server?.playerList?.getPlayer(list.first().first)

            if (leader is ServerPlayer) {
                for (memberEntry in list.drop(1)) {
                    val memberPlayer = level.server?.playerList?.getPlayer(memberEntry.first)
                    if (memberPlayer is ServerPlayer) {
                        CovenHandler.addPlayerToCoven(leader, memberPlayer)
                    }
                }
            }
        }

        return stack
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BOW
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return ItemUtils.startUsingInstantly(level, player, usedHand)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltip, flag)

        val componentType = WitcheryDataComponents.PLAYER_UUID_ORDERED_LIST.get()
        val list = stack.get(componentType)

        if (!list.isNullOrEmpty()) {
            val leader = list.first()
            tooltip.add(Component.literal("Contract Holder:").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD))
            tooltip.add(Component.literal(" - ${leader.second}").withStyle(ChatFormatting.BLUE))

            val members = list.drop(1)
            if (members.isNotEmpty()) {
                tooltip.add(Component.literal("Contract Members:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                members.forEach { (_, name) ->
                    tooltip.add(Component.literal(" - $name").withStyle(ChatFormatting.AQUA))
                }
            }
        }
    }

}