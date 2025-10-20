package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.item.TaglockItem
import dev.sterner.witchery.core.data_attachment.BindingRitualAttachment
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BindingRitual : Ritual("binding") {

    companion object {
        const val BINDING_DURATION = 20 * 60 * 2
        const val WEAK_BINDING_DURATION = 20 * 60 * 1
        private const val BINDING_RADIUS = 20.0
    }

    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        if (level.isClientSide) return

        val recipe = goldenChalkBlockEntity.ritualRecipe ?: return

        val taglock: ItemStack = recipe.inputItems.firstOrNull {
            it.item == WitcheryItems.TAGLOCK.get()
        } ?: return

        val target: LivingEntity? = TaglockItem.getLivingEntity(level, taglock)
            ?: TaglockItem.getPlayer(level, taglock)

        if (target == null) {
            return
        }

        val waystone = recipe.inputItems.firstOrNull {
            it.item == WitcheryItems.WAYSTONE.get()
        } ?: return

        val waystonePos: GlobalPos = if (waystone.has(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())) {
            waystone.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())!!
        } else {
            GlobalPos.of(level.dimension(), blockPos)
        }

        var dur: Int = BINDING_DURATION
        if (target is Player) {
            if (!WitcheryApi.isWitchy(target)) {
                dur = WEAK_BINDING_DURATION
            }
        }
        val bindingData = BindingRitualAttachment.Data(
            centerPos = waystonePos.pos(),
            radius = BINDING_RADIUS,
            duration = dur,
            isActive = true
        )

        BindingRitualAttachment.setData(target, bindingData)

        if (level is ServerLevel) {
            level.playSound(
                null,
                waystonePos.pos(),
                SoundEvents.CHAIN_PLACE,
                SoundSource.BLOCKS,
                1.0f, 0.5f
            )

            level.sendParticles(
                ParticleTypes.ENCHANT,
                waystonePos.pos().x + 0.5,
                waystonePos.pos().y + 1.0,
                waystonePos.pos().z + 0.5,
                50,
                2.0, 1.0, 2.0,
                0.1
            )
        }
    }
}