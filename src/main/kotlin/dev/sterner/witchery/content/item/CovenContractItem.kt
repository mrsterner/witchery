package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.entity.CovenWitchEntity
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.features.coven.CovenDialogue
import dev.sterner.witchery.features.coven.CovenHandler
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import java.util.LinkedList

class CovenContractItem(properties: Properties) : Item(properties) {

    override fun onDestroyed(itemEntity: ItemEntity, damageSource: DamageSource) {
        if (itemEntity.level() is ServerLevel) {
            val stack = itemEntity.item
            CovenHandler.disbandCovenFromContract(itemEntity.level() as ServerLevel, stack)
        }
        super.onDestroyed(itemEntity, damageSource)
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)

        if (!level.isClientSide && livingEntity is ServerPlayer) {
            val componentType = WitcheryDataComponents.PLAYER_UUID_ORDERED_LIST.get()
            val list = stack.get(componentType)?.toMutableList() ?: mutableListOf()

            if (list.size < 8 && !list.any { it.first == livingEntity.uuid }) {
                list.add(Pair(livingEntity.uuid, livingEntity.name.string))
                stack.set(componentType, LinkedList(list))

                livingEntity.displayClientMessage(
                    Component.translatable("witchery.coven.contract_signed"),
                    false
                )
            }

            if (list.size > 1) {
                val leader = level.server?.playerList?.getPlayer(list.first().first)

                if (leader is ServerPlayer && leader == livingEntity) {
                    var successCount = 0

                    for (memberEntry in list.drop(1)) {
                        val memberPlayer = level.server?.playerList?.getPlayer(memberEntry.first)
                        if (memberPlayer is ServerPlayer) {
                            if (CovenHandler.addPlayerToCoven(leader, memberPlayer)) {
                                successCount++
                            }
                        }
                    }

                    if (successCount > 0) {
                        leader.displayClientMessage(
                            Component.translatable("witchery.coven.bound_members", successCount),
                            false
                        )

                        level.playSound(
                            null,
                            leader.blockPosition(),
                            SoundEvents.ENCHANTMENT_TABLE_USE,
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f
                        )
                    }
                }
            }
        }

        return stack
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget is CovenWitchEntity && player is ServerPlayer) {
            val witchName = interactionTarget.customName ?: CovenDialogue.generateName(interactionTarget.random)

            if (interactionTarget.getIsCoven()) {
                player.sendSystemMessage(
                    CovenDialogue.getAlreadyBoundResponse(witchName, interactionTarget.random)
                        .withStyle(ChatFormatting.GOLD)
                )
                return InteractionResult.FAIL
            }

            if (CovenHandler.addWitchToCoven(player, interactionTarget)) {
                val componentType = WitcheryDataComponents.PLAYER_UUID_ORDERED_LIST.get()
                val list = stack.get(componentType)?.toMutableList() ?: mutableListOf()

                list.add(Pair(interactionTarget.uuid, interactionTarget.customName!!.string))
                stack.set(componentType, LinkedList(list))

                player.setItemInHand(usedHand, stack.copy())
                return InteractionResult.SUCCESS
            }

            return InteractionResult.FAIL
        }

        return InteractionResult.PASS
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
            tooltip.add(
                Component.literal("Contract Holder:")
                    .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD)
            )
            tooltip.add(
                Component.literal(" - ${leader.second}")
                    .withStyle(ChatFormatting.BLUE)
            )
        }

        val members = list?.drop(1) ?: emptyList()
        if (members.isNotEmpty()) {
            tooltip.add(
                Component.literal("Contract Members:")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
            )

            members.forEach { (_, name) ->
                tooltip.add(
                    Component.literal(" - $name")
                        .withStyle(ChatFormatting.AQUA)
                )
            }
        }
    }
}