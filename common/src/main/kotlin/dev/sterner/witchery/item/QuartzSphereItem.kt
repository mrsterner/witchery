package dev.sterner.witchery.item

import dev.sterner.witchery.handler.vampire.VampireLeveling
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import java.awt.Color

class QuartzSphereItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (livingEntity is ServerPlayer && stack.has(WitcheryDataComponents.HAS_SUN.get()) && stack.get(WitcheryDataComponents.HAS_SUN.get()) == true) {
            livingEntity.mainHandItem.shrink(1)
            livingEntity.remainingFireTicks = 20 * 4
            VampireLeveling.increaseUsedSunGrenades(livingEntity)
        }

        return super.finishUsingItem(stack, level, livingEntity)
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BOW
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (player.mainHandItem.get(WitcheryDataComponents.HAS_SUN.get()) != null) {
            return ItemUtils.startUsingInstantly(level, player, usedHand)
        }

        return InteractionResultHolder.fail(player.mainHandItem)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.has(WitcheryDataComponents.HAS_SUN.get()) && stack.get(WitcheryDataComponents.HAS_SUN.get()) == true) {
            tooltipComponents.add(Component.translatable("witchery.has_sun").setStyle(Style.EMPTY.withColor(Color(250, 220, 40).rgb)))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}