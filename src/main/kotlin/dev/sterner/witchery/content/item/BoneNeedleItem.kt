package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.util.WitcheryUtil
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


open class BoneNeedleItem(properties: Properties) : Item(properties.durability(16)) {

    override fun hasCraftingRemainingItem(): Boolean {
        return true
    }

    override fun isRepairable(arg: ItemStack): Boolean {
        return false
    }

    override fun getCraftingRemainingItem(itemStack: ItemStack): ItemStack {
        val damage = itemStack.damageValue + 1
        val copy = itemStack.copy()
        copy.damageValue = damage
        return copy
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        var pos = context.clickedPos
        val state = level.getBlockState(pos)
        val player = context.player

        if (state.`is`(BlockTags.WOOL) && player != null) {
            player.hurt(player.damageSources().playerAttack(player), 2f)
            level.setBlockAndUpdate(pos, WitcheryBlocks.BLOOD_STAINED_WOOL.get().defaultBlockState())
            return InteractionResult.SUCCESS
        }

        if (player != null && player.offhandItem.`is`(Items.GLASS_BOTTLE) && state.`is`(BlockTags.BEDS) && level.server != null) {

            if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(BedBlock.FACING))
            }

            for (serverPlayer in level.server!!.playerList.players) {
                if (serverPlayer.respawnPosition == pos) {
                    val taglock = WitcheryItems.TAGLOCK.get().defaultInstance
                    TaglockItem.bindPlayerOrLiving(serverPlayer, taglock)
                    WitcheryUtil.addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
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
            WitcheryUtil.addItemToInventoryAndConsume(player, InteractionHand.OFF_HAND, taglock)
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
                        TaglockItem.bindPlayerOrLiving(target, taglock)
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
                    TaglockItem.bindPlayerOrLiving(target, taglock)
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
            val delta = (target.yHeadRot - player.yHeadRot + 360.0f) % 360.0f
            var chance = if (player.isInvisible) 0.5f else 0.1f

            val lightLevelPenalty: Double = 0.25 * (player.level().getMaxLocalRawBrightness(player.onPos) / 15.0)

            if (delta < 90.0f || delta > 270.0f) {
                chance += if (player.isShiftKeyDown) 0.45f else 0.25f
            }

            return player.random.nextDouble() < chance - lightLevelPenalty
        }
    }
}