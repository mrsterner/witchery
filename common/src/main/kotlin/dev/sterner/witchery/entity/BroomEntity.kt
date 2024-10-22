package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class BroomEntity(level: Level) : Entity(WitcheryEntityTypes.BROOM.get(), level) {

    override fun canRide(vehicle: Entity): Boolean {
        return true
    }

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource): Boolean {
        return false
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if (!level().isClientSide) {
            player.startRiding(this)
        }

        return super.interact(player, hand)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {

    }

    override fun readAdditionalSaveData(compound: CompoundTag) {

    }

    override fun addAdditionalSaveData(compound: CompoundTag) {

    }
}