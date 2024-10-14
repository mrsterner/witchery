package dev.sterner.witchery.item

import dev.sterner.witchery.item.WaystoneItem.Companion.getLivingEntityName
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level
import java.awt.Color
import kotlin.math.abs


class TaglockItem(properties: Properties) : Item(properties) {

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {

        if (tryTaglockEntity(player.level(), player, player.mainHandItem, interactionTarget)) {
            return InteractionResult.SUCCESS
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val player = Minecraft.getInstance().level?.let { getPlayer(it, stack) }
        if (player != null) {
            tooltipComponents.add(
                Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255,2,100).rgb)))
        }
        val living = Minecraft.getInstance().level?.let { getLivingEntityName(stack) }
        if (living != null) {
            tooltipComponents.add(Component.translatable(living).setStyle(Style.EMPTY.withColor(Color(255,100,100).rgb)))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {

        fun bindPlayer(player: Player, stack: ItemStack) {
            stack.set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
        }

        fun getPlayerProfile(stack: ItemStack): ResolvableProfile? {
            return stack.get(DataComponents.PROFILE)
        }

        fun getPlayer(level: Level, stack: ItemStack): Player? {
            val profile = stack.get(DataComponents.PROFILE)
            if (profile != null && profile.id.isPresent) {
                return level.getPlayerByUUID(profile.id.get())
            }
            return null
        }

        fun tryTaglockEntity(level: Level, player: Player, itemStack: ItemStack, target: LivingEntity): Boolean {
            if (itemStack.`is`(WitcheryItems.BONE_NEEDLE.get()) && player.offhandItem.`is`(Items.GLASS_BOTTLE)) {
                if (target is Player) {
                    if (trySneakyTaglocking(player, target)) {
                        bindPlayer(target, itemStack)
                        level.playSound(null, target.onPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 0.75f, 1f)
                        return true
                    } else {
                        level.playSound(null, target.onPos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.NEUTRAL, 0.75f, 1f)
                        return false
                    }
                } else {
                    WaystoneItem.bindLivingEntity(target, itemStack)
                    level.playSound(null, target.onPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 0.75f, 1f)
                    return true
                }
            }

            return false
        }

        private fun trySneakyTaglocking(player: Player, target: Player): Boolean {
            val delta = abs(target.yHeadRot + 90.0f) % 360.0f - (player.yHeadRot + 90.0f) % 360.0f
            var chance = if (player.isInvisible) 0.5f else 0.1f
            val lightLevelPenalty: Double = 0.25 * (player.level().getMaxLocalRawBrightness(player.onPos) / 15.0)
            if (360.0 - delta % 360.0 < 90 || delta % 360.0 < 90) {
                chance += if(player.isShiftKeyDown) 0.45f else 0.25f
            }
            return player.random.nextDouble() < chance - lightLevelPenalty;
        }
    }
}