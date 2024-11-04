package dev.sterner.witchery.item

import dev.sterner.witchery.entity.SleepingPlayerEntity
import dev.sterner.witchery.entity.SleepingPlayerEntity.Companion.replaceWithPlayer
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition

class IcyNeedleItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)

            if (livingEntity is ServerPlayer && (level.dimension() == WitcheryWorldgenKeys.DREAM || level.dimension() == WitcheryWorldgenKeys.NIGHTMARE)) {
                for (serverLevel in level.server!!.allLevels) {
                    val sleepingUuid = SleepingPlayerLevelAttachment.getPlayerFromSleeping(livingEntity.uuid, serverLevel)
                    if (sleepingUuid != null) {
                        val sleepingPlayer: SleepingPlayerEntity? = serverLevel.getEntity(sleepingUuid) as SleepingPlayerEntity?
                        if (sleepingPlayer != null) {
                            val destination = serverLevel.server.getLevel(sleepingPlayer.level().dimension())
                            if (destination != null) {
                                livingEntity.teleportTo(destination, sleepingPlayer.x, sleepingPlayer.y, sleepingPlayer.z, sleepingPlayer.yRot, sleepingPlayer.xRot)
                                replaceWithPlayer(livingEntity, sleepingPlayer)
                                SleepingPlayerLevelAttachment.remove(livingEntity.uuid, serverLevel)
                            }

                            return stack
                        }
                    }

                }
                val transition = livingEntity.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING)
                livingEntity.changeDimension(transition)
            }

        return stack

    }

    override fun getUseDuration(stack: ItemStack?, entity: LivingEntity?): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack?): UseAnim {
        return UseAnim.BOW
    }

    override fun use(level: Level?, player: Player?, usedHand: InteractionHand?): InteractionResultHolder<ItemStack> {
        return ItemUtils.startUsingInstantly(level, player, usedHand)
    }
}