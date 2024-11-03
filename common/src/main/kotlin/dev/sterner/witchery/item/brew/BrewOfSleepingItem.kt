package dev.sterner.witchery.item.brew

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SleepingPlayerData
import dev.sterner.witchery.entity.SleepingPlayerEntity
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap

class BrewOfSleepingItem(color: Int, properties: Properties) : BrewItem(color, properties) {

    override fun applyEffectOnSelf(player: Player) {

        if (player.level().dimension() != Level.OVERWORLD) {
            return
        }

        val sleepingPlayer = SleepingPlayerEntity.createFromPlayer(player, SleepingPlayerData.fromPlayer(player))
        val itemsToKeep = mutableListOf<ItemStack>()

        for (i in 0 until player.inventory.containerSize) {
            val itemStack = player.inventory.getItem(i)
            if (itemStack.item == WitcheryItems.ICY_NEEDLE.get()) {
                itemsToKeep.add(player.inventory.removeItem(i, itemStack.count))
            }
        }

        player.inventory.clearContent()

        for (keep in itemsToKeep) {
            player.inventory.add(keep.copy())
        }

        player.level().addFreshEntity(sleepingPlayer)

        val key = ResourceKey.create(Registries.DIMENSION, Witchery.id("dream_world"))
        val nightmareKey = ResourceKey.create(Registries.DIMENSION, Witchery.id("nightmare_world"))

        val nightmareChance = player.level().random.nextDouble() > 0.05 //TODO make dreamweaving affect this
        val destinationKey = if (nightmareChance) nightmareKey else key

        val destination = player.level().server?.getLevel(destinationKey)
        if (destination != null) {
            val targetX = player.x
            val targetY = player.y
            val targetZ = player.z

            val blockPos = BlockPos.containing(targetX, targetY, targetZ)
            val solidY: Int = if (!destination.getBlockState(blockPos).isSolid) blockPos.y else destination.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.x, blockPos.z)

            if (player is ServerPlayer) {
                player.teleportTo(destination, targetX, solidY + 1.0, targetZ, player.yRot, player.xRot)
            }

        }

        super.applyEffectOnSelf(player)
    }

    companion object {
        fun respawnPlayer(oldPlayer: ServerPlayer?, newServerPlayer: ServerPlayer?, wonGame: Boolean) {
            if (newServerPlayer?.level() is ServerLevel) {
                val serverLevel = newServerPlayer.level() as ServerLevel
                val hasSleeping = SleepingPlayerLevelAttachment.getPlayerFromSleeping(newServerPlayer.uuid, serverLevel)
                if (hasSleeping != null) {
                    val sleepEntity: SleepingPlayerEntity? = serverLevel.getEntity(hasSleeping) as SleepingPlayerEntity?
                    if (sleepEntity != null) {
                        SleepingPlayerEntity.replaceWithPlayer(newServerPlayer, sleepEntity)
                    }
                }
            }
        }
    }
}