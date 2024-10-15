package dev.sterner.witchery.item

import dev.sterner.witchery.item.TaglockItem.Companion.bindPlayer
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.state.properties.BedPart
import kotlin.math.abs


class BoneNeedleItem(properties: Properties) : Item(properties.durability(16)) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        var pos = context.clickedPos
        val state = level.getBlockState(pos)
        val player = context.player

        if (player != null && player.offhandItem.`is`(Items.GLASS_BOTTLE) && state.`is`(BlockTags.BEDS) && level.server != null) {

            if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(BedBlock.FACING))
            }

            for (serverPlayer in level.server!!.playerList.players) {
                if (serverPlayer.respawnPosition == pos) {
                    val taglock = WitcheryItems.TAGLOCK.get().defaultInstance
                    bindPlayer(serverPlayer, taglock)
                    addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 1.0f)
                    break
                }
            }

            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        val taglock = WitcheryItems.TAGLOCK.get().defaultInstance
        if (tryTaglockEntity(player.level(), player, player.mainHandItem, interactionTarget, taglock)) {
            stack.set(WitcheryDataComponents.TIMESTAMP.get(), player.level().gameTime)
            addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
            return InteractionResult.SUCCESS
        } else {
            val level = player.level()
            if (level is ServerLevel) {
                stack.hurtAndBreak(1, level, player as ServerPlayer) { }
                level.playSound(null, interactionTarget.onPos, SoundEvents.STONE_HIT, SoundSource.PLAYERS)
            }
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }

    companion object {

        fun tryTaglockEntity(
            level: Level,
            player: Player,
            itemStack: ItemStack,
            target: LivingEntity,
            taglock: ItemStack
        ): Boolean {
            if (itemStack.`is`(WitcheryItems.BONE_NEEDLE.get()) && player.offhandItem.`is`(Items.GLASS_BOTTLE)) {
                if (target is Player) {
                    if (trySneakyTaglocking(player, target)) {
                        bindPlayer(target, taglock)
                        level.playSound(
                            null,
                            target.onPos,
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            SoundSource.NEUTRAL,
                            0.75f,
                            1f
                        )
                        return true
                    } else {
                        level.playSound(
                            null,
                            target.onPos,
                            SoundEvents.NOTE_BLOCK_BASS.value(),
                            SoundSource.NEUTRAL,
                            0.75f,
                            1f
                        )
                        return false
                    }
                } else {
                    if (target is Mob) {
                        target.setPersistenceRequired()
                    }
                    TaglockItem.bindLivingEntity(target, taglock)
                    level.playSound(
                        null,
                        target.onPos,
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.NEUTRAL,
                        0.75f,
                        1f
                    )
                    return true
                }
            }

            return false
        }

        private fun trySneakyTaglocking(player: Player, target: Player): Boolean {
            val delta = abs(target.yHeadRot + 90.0f) % 360.0f - (player.yHeadRot + 90.0f) % 360.0f
            var chance = if (player.isInvisible) 0.5f else 0.1f
            val lightLevelPenalty: Double = 0.25 * (player.level().getMaxLocalRawBrightness(player.onPos) / 15.0)
            if (360.0 - delta % 360.0 < 90 || delta % 360.0 < 90) {
                chance += if (player.isShiftKeyDown) 0.45f else 0.25f
            }
            return player.random.nextDouble() < chance - lightLevelPenalty
        }

        fun addItemToInventoryAndConsume(player: Player, hand: InteractionHand, itemToAdd: ItemStack) {
            val currentItemStack: ItemStack = player.getItemInHand(hand)
            if (currentItemStack.isEmpty) {
                player.setItemInHand(hand, itemToAdd)
            } else {
                if (currentItemStack.count == 1) {
                    player.setItemInHand(hand, itemToAdd)
                } else {
                    currentItemStack.shrink(1)
                    if (!player.inventory.add(itemToAdd)) {
                        player.drop(itemToAdd, false, true)
                    }
                }
            }
        }
    }
}