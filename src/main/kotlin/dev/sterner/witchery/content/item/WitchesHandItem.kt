package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.item.curios.HagsRingItem
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryTags
import dev.sterner.witchery.features.hags_ring.VeinMiningTracker
import dev.sterner.witchery.features.infusion.InfusionHandler
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import top.theillusivec4.curios.api.CuriosApi

class WitchesHandItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        WitcheryApi.makePlayerWitchy(player)
        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        val level = context.level

        if (level.isClientSide) return InteractionResult.SUCCESS

        if (player !is ServerPlayer) return InteractionResult.PASS

        val pos = context.clickedPos
        val state = level.getBlockState(pos)

        val hasRing = CuriosApi.getCuriosInventory(player)
            .map { inv -> inv.findFirstCurio(WitcheryItems.HAGS_RING.get()).isPresent }
            .orElse(false)

        if (hasRing && state.`is`(WitcheryTags.VEIN_MINEABLE) && !player.isShiftKeyDown) {
            if (VeinMiningTracker.isVeinMining(player)) {
                return InteractionResult.SUCCESS
            }

            val oresToBreak = HagsRingItem.gatherConnectedOres(level as ServerLevel, pos, state.block)

            if (oresToBreak.isNotEmpty()) {
                VeinMiningTracker.startVeinMining(player, oresToBreak)
                return InteractionResult.SUCCESS
            }
        }

        if (InfusionHandler.canUse(player)) {
            player.startUsingItem(context.hand)
            return InteractionResult.CONSUME
        }

        return InteractionResult.PASS
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 72000
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (livingEntity is Player) {
            if (livingEntity is ServerPlayer && !VeinMiningTracker.isVeinMining(livingEntity)) {
                InfusionHandler.onHoldRightClick(livingEntity)
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration)
    }

    override fun releaseUsing(stack: ItemStack, level: Level, livingEntity: LivingEntity, timeCharged: Int) {
        super.releaseUsing(stack, level, livingEntity, timeCharged)

        if (livingEntity is Player) {
            InfusionHandler.onHoldReleaseRightClick(livingEntity)
        }
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BLOCK
    }
}