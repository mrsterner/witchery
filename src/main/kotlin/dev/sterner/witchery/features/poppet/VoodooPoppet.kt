package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.features.poppet.PoppetHandler
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.hunter.HunterArmorDefenseHandler
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
import kotlin.math.max

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

                    val pieces = HunterArmorDefenseHandler.getHunterArmorPieceCount(this)
                    if (pieces > 0) {
                        val multiplier = HunterArmorDefenseHandler.getProtectionMultiplier(this)
                        val reduction = HunterArmorDefenseHandler.POPPET_DAMAGE_REDUCTION * multiplier
                        scaledMovement = scaledMovement.scale(1.0 - reduction)
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
                        VoodooPoppetLivingEntityAttachment.Data(true, 20)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.Data(true, 20)
                    )
                }
            } else {
                boundPlayer?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.Data(false, 0)
                    )
                }
                boundEntity?.let {
                    VoodooPoppetLivingEntityAttachment.setPoppetData(
                        it,
                        VoodooPoppetLivingEntityAttachment.Data(false, 0)
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
            blockHitResult: BlockHitResult
        ): InteractionResult {
            val boundPlayer = PoppetHandler.getBoundPlayer(level, item)
            val boundEntity = PoppetHandler.getBoundEntity(level, item)

            val blockState = level.getBlockState(blockHitResult.blockPos)

            if (boundPlayer == null && boundEntity == null) return InteractionResult.PASS

            val (fireTicksPlayer, fireTicksEntity, damage) = when {
                blockState.`is`(Blocks.LAVA) -> Triple(20 * 2, 20 * 4, 16)
                blockState.`is`(Blocks.FIRE) -> Triple(20 * 1, 20 * 2, 8)
                else -> return InteractionResult.PASS
            }

            boundPlayer?.let { player ->
                var ticks = if (WitcheryApi.isWitchy(player)) fireTicksPlayer * 2 else fireTicksPlayer

                if (player is Player) {
                    val pieces = HunterArmorDefenseHandler.getHunterArmorPieceCount(player)
                    if (pieces > 0) {
                        val multiplier = HunterArmorDefenseHandler.getProtectionMultiplier(player)
                        val reduction = HunterArmorDefenseHandler.POPPET_DAMAGE_REDUCTION * multiplier
                        ticks = max((ticks * (1.0 - reduction)).toInt(), 1)
                    }
                }

                player.remainingFireTicks = ticks
            }

            boundEntity?.remainingFireTicks = fireTicksEntity
            item.damageValue += damage
            if (item.damageValue >= item.maxDamage) {
                item.shrink(1)
            }

            return InteractionResult.SUCCESS
        }
    }
}