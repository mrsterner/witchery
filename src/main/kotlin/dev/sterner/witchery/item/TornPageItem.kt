package dev.sterner.witchery.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class TornPageItem(properties: Properties) : Item(properties) {


    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (player is ServerPlayer) {
            val nextAdvancement = advancementLocations.firstOrNull { !WitcheryUtil.hasAdvancement(player, it) }

            nextAdvancement?.let {
                val index = advancementLocations.indexOf(it) + 1
                val criterion = "impossible_${index}"
                WitcheryUtil.grantAdvancementCriterion(player, it, criterion)

                val stack = player.getItemInHand(usedHand)
                if (!player.isCreative) {
                    stack.shrink(1)
                }
                player.displayClientMessage(Component.translatable("witchery.add_page.$index"), true)
                level.playSound(null, player.x, player.y, player.z, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS)
                return InteractionResultHolder.success(stack)
            }
        }

        return super.use(level, player, usedHand)
    }

    companion object {

        val advancementLocations = listOf(
            Witchery.id("vampire/1"),
            Witchery.id("vampire/2"),
            Witchery.id("vampire/3"),
            Witchery.id("vampire/4"),
            Witchery.id("vampire/5"),
            Witchery.id("vampire/6"),
            Witchery.id("vampire/7"),
            Witchery.id("vampire/8"),
            Witchery.id("vampire/9")
        )
    }
}