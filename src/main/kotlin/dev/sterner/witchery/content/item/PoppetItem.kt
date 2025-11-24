package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.content.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.content.item.TaglockItem.Companion.getLivingEntityName
import dev.sterner.witchery.content.item.TaglockItem.Companion.getPlayerProfile
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.awt.Color

open class PoppetItem(properties: Properties) : Item(properties.stacksTo(1)) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        if (player != null && this.calculateHitResult(player).type == HitResult.Type.BLOCK) {
            player.startUsingItem(context.hand)
        }

        return InteractionResult.CONSUME
    }

    override fun isDamageable(stack: ItemStack): Boolean {
        return true
    }

    private fun calculateHitResult(player: Player): HitResult {
        return ProjectileUtil.getHitResultOnViewVector(
            player,
            { entity: Entity -> !entity.isSpectator && entity.isPickable },
            player.blockInteractionRange()
        )
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 2000
    }

    override fun releaseUsing(stack: ItemStack, level: Level, livingEntity: LivingEntity, timeCharged: Int) {
        super.releaseUsing(stack, level, livingEntity, timeCharged)

        if (livingEntity is Player && timeCharged > 20 * 2) {
            val dir = livingEntity.getViewVector(0f)
            val eyePos = livingEntity.getEyePosition(0f)
            val rayEnd = eyePos.add(dir.scale(5.0))

            val result: BlockHitResult = level.clip(
                ClipContext(eyePos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity)
            )
            if (result.direction.axis.isHorizontal) {
                WitcheryApi.makePlayerWitchy(livingEntity)
                PoppetBlockEntity.placePoppet(level, result.blockPos, livingEntity, result.direction)
            }
        }
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BOW
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val player = getPlayerProfile(stack)
        val living = getLivingEntityName(stack)

        val isVampiric = stack.`is`(WitcheryItems.VAMPIRIC_POPPET.get())
        val targetProfile = stack.get(WitcheryDataComponents.VAMPIRIC_TARGET_PROFILE.get())
        val targetName = stack.get(WitcheryDataComponents.VAMPIRIC_TARGET_NAME.get())

        if (isVampiric && (targetProfile != null || targetName != null)) {
            tooltipComponents.add(
                Component.translatable("tooltip.witchery.vampiric_poppet.owner")
                    .withStyle(ChatFormatting.GRAY)
            )

            if (player != null) {
                tooltipComponents.add(
                    Component.literal("  " + player.gameProfile.name.replaceFirstChar(Char::uppercase))
                        .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
                )
            } else if (living != null) {
                tooltipComponents.add(
                    Component.literal("  ")
                        .append(Component.translatable(living))
                        .setStyle(Style.EMPTY.withColor(Color(255, 100, 100).rgb))
                )
            }

            tooltipComponents.add(
                Component.translatable("tooltip.witchery.vampiric_poppet.target")
                    .withStyle(ChatFormatting.GRAY)
            )

            if (targetProfile != null) {
                tooltipComponents.add(
                    Component.literal("  " + targetProfile.gameProfile.name.replaceFirstChar(Char::uppercase))
                        .setStyle(Style.EMPTY.withColor(Color(200, 50, 255).rgb))
                )
            } else if (targetName != null) {
                tooltipComponents.add(
                    Component.literal("  ")
                        .append(Component.translatable(targetName))
                        .setStyle(Style.EMPTY.withColor(Color(200, 100, 255).rgb))
                )
            }
        } else {
            if (player != null) {
                tooltipComponents.add(
                    Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                        .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
                )
            } else if (living != null) {
                tooltipComponents.add(
                    Component.translatable(living)
                        .setStyle(Style.EMPTY.withColor(Color(255, 100, 100).rgb))
                )
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}