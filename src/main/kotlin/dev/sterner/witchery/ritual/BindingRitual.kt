package dev.sterner.witchery.ritual

import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.data_attachment.BindingCurseAttachment
import dev.sterner.witchery.handler.PoppetHandler
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class BindingRitual : Ritual("binding") {

    companion object {
        private const val BINDING_DURATION = 20 * 60 * 5
        private const val BINDING_RADIUS = 50.0
        private const val CHECK_INTERVAL = 20
    }

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        if (level.isClientSide) return

        val recipe = goldenChalkBlockEntity.ritualRecipe ?: return

        val taglock = recipe.inputItems.firstOrNull {
            it.item == WitcheryItems.TAGLOCK.get()
        } ?: return

        val waystone = recipe.inputItems.firstOrNull {
            it.item == WitcheryItems.WAYSTONE.get()
        } ?: return

        val player = PoppetHandler.getBoundPlayer(level, taglock)
        if (player !is ServerPlayer) return

        val waystonePos: GlobalPos = waystone.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get()) ?: GlobalPos(level.dimension(), blockPos)

        val bindingData = BindingCurseAttachment.Data(
            centerPos = waystonePos.pos,
            radius = BINDING_RADIUS,
            duration = BINDING_DURATION,
            isActive = true
        )

        BindingCurseAttachment.setData(player, bindingData)
        BindingCurseAttachment.sync(player)

        if (level is ServerLevel) {
            level.playSound(
                null,
                waystonePos.pos,
                SoundEvents.CHAIN_PLACE,
                SoundSource.BLOCKS,
                1.0f, 0.5f
            )

            level.sendParticles(
                ParticleTypes.ENCHANT,
                waystonePos.pos.x + 0.5,
                waystonePos.pos.y + 1.0,
                waystonePos.pos.z + 0.5,
                50,
                2.0, 1.0, 2.0,
                0.1
            )
        }
    }

    override fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {
        if (level.isClientSide) return
        if (level.gameTime % CHECK_INTERVAL != 0L) return

        val recipe = goldenChalkBlockEntity.ritualRecipe ?: return
        val taglock = recipe.inputItems.firstOrNull {
            it.item == WitcheryItems.TAGLOCK.get()
        } ?: return

        val player = PoppetHandler.getBoundPlayer(level, taglock)
        if (player !is ServerPlayer) return

        val bindingData = BindingCurseAttachment.getData(player)
        if (!bindingData.isActive) return

        val distance = player.blockPosition().distSqr(bindingData.centerPos)
        if (distance > BINDING_RADIUS * BINDING_RADIUS) {
            val direction = Vec3(
                bindingData.centerPos.x + 0.5 - player.x,
                bindingData.centerPos.y + 0.5 - player.y,
                bindingData.centerPos.z + 0.5 - player.z
            ).normalize()

            val pullStrength = if (WitcheryApi.isWitchy(player)) 0.3 else 0.15

            player.deltaMovement = player.deltaMovement.add(
                direction.x * pullStrength,
                direction.y * pullStrength * 0.5,
                direction.z * pullStrength
            )
            player.hurtMarked = true

            (level as ServerLevel).sendParticles(
                ParticleTypes.ENCHANT,
                player.x,
                player.y + 1.0,
                player.z,
                5,
                0.3, 0.3, 0.3,
                0.05
            )
        }

        bindingData.duration--
        if (bindingData.duration <= 0) {
            bindingData.isActive = false
        }

        BindingCurseAttachment.setData(player, bindingData)
    }
}