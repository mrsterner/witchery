package dev.sterner.witchery.poppet

import dev.sterner.witchery.api.PoppetType
import dev.sterner.witchery.api.PoppetUsage
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.data_attachment.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.handler.poppet.PoppetHandler
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class VoodooPoppet : PoppetType {
    override val item = WitcheryItems.VOODOO_POPPET.get()

    override fun isValidFor(owner: LivingEntity, source: DamageSource?): Boolean = true

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean = false

    override fun handleItemEntityTick(entity: ItemEntity) {
        val movementVector: Vec3 = entity.deltaMovement
        val itemStack = entity.item

        val boundPlayer = PoppetHandler.getBoundPlayer(entity.level(), itemStack)
        val boundEntity = PoppetHandler.getBoundEntity(entity.level(), itemStack)


        if (boundPlayer != null || boundEntity != null) {
            if (movementVector.length() > 0.2) {
                var scaledMovement = movementVector.scale(0.45)
                boundPlayer?.apply {
                    if (WitcheryApi.isWitchy(boundPlayer)) {
                        scaledMovement = scaledMovement.scale(0.75)
                    }
                    addDeltaMovement(scaledMovement)
                    hurtMarked = true
                }
                boundEntity?.apply {
                    addDeltaMovement(scaledMovement)
                    hurtMarked = true
                }
            }

            if (entity.isUnderWater) {
                boundPlayer?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(true, 20)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(true, 20)
                    )
                }
            } else {
                boundPlayer?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false, 0)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.VoodooPoppetData(false, 0)
                    )
                }
            }

            entity.item.damageValue += getDurabilityDamage(PoppetUsage.VOODOO)
            if (entity.item.damageValue >= entity.item.maxDamage) {
                entity.remove(Entity.RemovalReason.DISCARDED)
            }
        }
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int = when (usage) {
        PoppetUsage.VOODOO -> 1
        else -> 0
    }

    companion object {
        fun handleInteraction(
            level: Level,
            pos: BlockPos,
            item: ItemStack,
            player: Player?,
            blockHitResult: BlockHitResult
        ): InteractionResult {
            if (level.getBlockState(blockHitResult.blockPos).`is`(Blocks.LAVA)) {
                val boundPlayer = PoppetHandler.getBoundPlayer(level, item)
                val boundEntity = PoppetHandler.getBoundEntity(level, item)

                if (boundPlayer != null || boundEntity != null) {
                    boundPlayer?.apply {
                        if (WitcheryApi.isWitchy(boundPlayer)) {
                            boundPlayer.remainingFireTicks = 20 * 4
                        } else {
                            boundPlayer.remainingFireTicks = 20 * 2
                        }
                    }

                    boundEntity?.remainingFireTicks = 20 * 4
                    item.damageValue += 16
                    if (item.damageValue >= item.maxDamage) {
                        item.shrink(1)
                    }
                    return InteractionResult.SUCCESS
                }
            } else if (level.getBlockState(pos).`is`(Blocks.FIRE)) {
                val boundPlayer = PoppetHandler.getBoundPlayer(level, item)
                val boundEntity = PoppetHandler.getBoundEntity(level, item)

                if (boundPlayer != null || boundEntity != null) {
                    boundPlayer?.apply {
                        if (WitcheryApi.isWitchy(boundPlayer)) {
                            boundPlayer.remainingFireTicks = 20 * 2
                        } else {
                            boundPlayer.remainingFireTicks = 20 * 1
                        }
                    }
                    boundEntity?.remainingFireTicks = 20 * 2
                    item.damageValue += 8
                    if (item.damageValue >= item.maxDamage) {
                        item.shrink(1)
                    }
                    return InteractionResult.SUCCESS
                }
            }
            return InteractionResult.PASS
        }
    }
}