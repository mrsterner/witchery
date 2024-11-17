package dev.sterner.witchery.item

import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class TornPageItem(properties: Properties) : Item(properties) {

    val advancementLocations = listOf(
        Witchery.id("vampire/1"),
        Witchery.id("vampire/2"),
        Witchery.id("vampire/3"),
        Witchery.id("vampire/4"),
        Witchery.id("vampire/5"),
        Witchery.id("vampire/6"),
        Witchery.id("vampire/7"),
        Witchery.id("vampire/8"),
        Witchery.id("vampire/9"),
        Witchery.id("vampire/10")
    )

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (player is ServerPlayer) {
            val nextAdvancement = advancementLocations.firstOrNull { !hasAdvancement(player, it) }

            nextAdvancement?.let {
                val criterion = "impossible_${advancementLocations.indexOf(it) + 1}"
                grantAdvancementCriterion(player, it, criterion)

                val stack = player.getItemInHand(usedHand)
                stack.shrink(1)
                return InteractionResultHolder.success(stack)
            }
        }

        return super.use(level, player, usedHand)
    }

    companion object {

        fun hasAdvancement(serverPlayer: ServerPlayer, advancementResourceLocation: ResourceLocation): Boolean {
            if (serverPlayer.getServer() == null) {
                return false
            }

            val manager = serverPlayer.server.advancements
            val tracker: PlayerAdvancements = serverPlayer.advancements

            val advancement = manager.get(advancementResourceLocation)
            if (advancement != null) {
                return tracker.getOrStartProgress(advancement).isDone
            }

            return false
        }

        fun grantAdvancementCriterion(
            serverPlayer: ServerPlayer,
            advancementResourceLocation: ResourceLocation,
            criterion: String
        ) {
            if (serverPlayer.getServer() == null) {
                return
            }
            val manager = serverPlayer.server.advancements
            val tracker: PlayerAdvancements = serverPlayer.advancements

            val advancement = manager.get(advancementResourceLocation)
            if (advancement != null) {
                if (!tracker.getOrStartProgress(advancement).isDone) {
                    tracker.award(advancement, criterion)
                }
            }
        }
    }
}