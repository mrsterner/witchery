package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.item.MirrorItem
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.UUID

class MirrorPairRitual : Ritual("linked_mirror") {

    override fun onEndRitual(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        if (level is ServerLevel) {
            val pairId = UUID.randomUUID()

            val mirror1 = ItemStack(WitcheryItems.MIRROR.get())
            val mirror2 = ItemStack(WitcheryItems.MIRROR.get())

            MirrorItem.setPairId(mirror1, pairId)
            MirrorItem.setPairId(mirror2, pairId)

            Containers.dropItemStack(level, blockPos.x.toDouble(), blockPos.y.toDouble() + 1.0, blockPos.z.toDouble(), mirror1)
            Containers.dropItemStack(level, blockPos.x.toDouble(), blockPos.y.toDouble() + 1.0, blockPos.z.toDouble(), mirror2)

        }
    }
}