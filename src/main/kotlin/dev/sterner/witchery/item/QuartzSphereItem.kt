package dev.sterner.witchery.item

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.entity.ThrownBrewEntity
import dev.sterner.witchery.handler.affliction.vampire.VampireLeveling
import dev.sterner.witchery.item.brew.ThrowableBrewItem
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.*
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.level.Level
import java.awt.Color

class QuartzSphereItem(properties: Properties) : Item(properties), ProjectileItem {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)

        if (itemStack.get(WitcheryDataComponents.HAS_SUN.get()) == true) {
            return ItemUtils.startUsingInstantly(level, player, usedHand)
        }

        val loadedPotion = getLoadedPotion(itemStack)
        if (loadedPotion != null && loadedPotion.item is ThrowableBrewItem) {
            if (!level.isClientSide) {
                WitcheryApi.makePlayerWitchy(player)
                val thrownPotion = ThrownBrewEntity(level, player)
                thrownPotion.item = loadedPotion.copy()
                thrownPotion.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
                level.addFreshEntity(thrownPotion)
            }

            player.awardStat(Stats.ITEM_USED[this])
            player.cooldowns.addCooldown(this, 20)

            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
        }

        return InteractionResultHolder.fail(itemStack)
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (livingEntity is ServerPlayer && stack.has(WitcheryDataComponents.HAS_SUN.get()) &&
            stack.get(WitcheryDataComponents.HAS_SUN.get()) == true
        ) {
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

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.has(WitcheryDataComponents.HAS_SUN.get()) && stack.get(WitcheryDataComponents.HAS_SUN.get()) == true) {
            tooltipComponents.add(
                Component.translatable("witchery.has_sun")
                    .setStyle(Style.EMPTY.withColor(Color(250, 220, 40).rgb))
            )
        }

        val loadedPotion = getLoadedPotion(stack)
        if (loadedPotion != null) {
            tooltipComponents.add(
                Component.translatable("item.witchery.quartz_sphere.loaded")
                    .withStyle(ChatFormatting.GOLD)
            )
            tooltipComponents.add(
                Component.literal("  ")
                    .append(loadedPotion.hoverName)
                    .withStyle(ChatFormatting.GRAY)
            )
        } else if (stack.get(WitcheryDataComponents.HAS_SUN.get()) != true) {
            tooltipComponents.add(
                Component.translatable("item.witchery.quartz_sphere.empty")
                    .withStyle(ChatFormatting.GRAY)
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun asProjectile(level: Level, pos: Position, stack: ItemStack, direction: Direction): Projectile {
        val loadedPotion = getLoadedPotion(stack) ?: ItemStack(Items.SPLASH_POTION)
        val thrownPotion = ThrownBrewEntity(level, pos.x(), pos.y(), pos.z())
        thrownPotion.item = loadedPotion
        return thrownPotion
    }

    override fun createDispenseConfig(): ProjectileItem.DispenseConfig {
        return ProjectileItem.DispenseConfig.builder()
            .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5f)
            .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25f)
            .build()
    }

    companion object {
        fun getLoadedPotion(stack: ItemStack): ItemStack? {
            val contents = stack.get(WitcheryDataComponents.LOADED_POTION.get()) ?: return null
            return contents.nonEmptyItems().firstOrNull()
        }

        fun setLoadedPotion(stack: ItemStack, potion: ItemStack?) {
            if (potion == null || potion.isEmpty) {
                stack.remove(WitcheryDataComponents.LOADED_POTION.get())
            } else {
                stack.set(WitcheryDataComponents.LOADED_POTION.get(), ItemContainerContents.fromItems(listOf(potion)))
            }
        }

        fun hasLoadedPotion(stack: ItemStack): Boolean {
            return getLoadedPotion(stack) != null
        }
    }
}