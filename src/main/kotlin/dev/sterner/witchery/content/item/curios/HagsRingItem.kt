package dev.sterner.witchery.content.item.curios

import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.TooltipFlag
import top.theillusivec4.curios.api.CuriosApi
import java.awt.Color

class HagsRingItem(properties: Properties) : Item(properties), ICurioItem {

    override fun makesPiglinsNeutral(
        stack: ItemStack,
        wearer: LivingEntity
    ): Boolean {
        return true
    }

    companion object {
        private const val MAX_VEIN_SIZE = 64

        fun getFortuneLevel(player: Player): Int {
            val curiosInventory = CuriosApi.getCuriosInventory(player)
                .orElse(null) ?: return 0

            val ringStack = curiosInventory.findFirstCurio(WitcheryItems.HAGS_RING.get())
                .map { it.stack() }
                .orElse(ItemStack.EMPTY)

            if (ringStack.isEmpty) return 0

            return ringStack.getOrDefault(WitcheryDataComponents.FORTUNE_LEVEL.get(), 0)
        }

        fun gatherConnectedOres(
            level: ServerLevel,
            startPos: BlockPos,
            targetBlock: Block
        ): List<BlockPos> {
            val visited = mutableSetOf<BlockPos>()
            val orePositions = mutableListOf<BlockPos>()

            fun dfs(pos: BlockPos) {
                if (visited.contains(pos) || orePositions.size >= MAX_VEIN_SIZE) {
                    return
                }

                visited.add(pos)
                val state = level.getBlockState(pos)

                if (state.`is`(WitcheryTags.VEIN_MINEABLE) && state.block == targetBlock) {
                    orePositions.add(pos)

                    for (dx in -1..1) {
                        for (dy in -1..1) {
                            for (dz in -1..1) {
                                if (dx == 0 && dy == 0 && dz == 0) continue
                                dfs(pos.offset(dx, dy, dz))
                            }
                        }
                    }
                }
            }

            dfs(startPos)

            return orePositions.sortedByDescending { it.distSqr(startPos) }
        }
    }

    override fun canEquipFromUse(slotContext: SlotContext, stack: ItemStack): Boolean {
        return true
    }
}