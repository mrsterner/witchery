package dev.sterner.witchery.item

import dev.sterner.witchery.entity.LilithEntity
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.ChatFormatting
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import java.awt.Color

class WineGlassItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)
        if (livingEntity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(livingEntity, stack)
            livingEntity.awardStat(Stats.ITEM_USED[this])

            if (stack.has(WitcheryDataComponents.VAMPIRE_BLOOD.get()) && stack.get(WitcheryDataComponents.VAMPIRE_BLOOD.get()) == true) {
                val data = VampirePlayerAttachment.getData(livingEntity)
                if (data.vampireLevel == 0) {
                    VampirePlayerAttachment.increaseVampireLevel(player = livingEntity)
                    BloodPoolLivingEntityAttachment.increaseBlood(player = livingEntity, 300)
                }
            }
        }

        return ItemStack(WitcheryItems.WINE_GLASS.get())
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun getDrinkingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun getEatingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (player.mainHandItem.get(WitcheryDataComponents.BLOOD.get()) != null) {
            return ItemUtils.startUsingInstantly(level, player, usedHand)
        }

        val data = player.mainHandItem.get(WitcheryDataComponents.BLOOD.get())
        if (data == null && player.isShiftKeyDown && player.offhandItem.`is`(WitcheryItems.BONE_NEEDLE.get())) {
            player.mainHandItem.set(WitcheryDataComponents.BLOOD.get(), player.uuid)
            if (VampirePlayerAttachment.getData(player).vampireLevel == 10) {
                player.mainHandItem.set(WitcheryDataComponents.VAMPIRE_BLOOD.get(), true)
            }
            player.hurt(level.damageSources().playerAttack(player), 4f)
        }

        return InteractionResultHolder.fail(player.mainHandItem)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val bl = stack.has(WitcheryDataComponents.BLOOD.get())
        val bl2 = stack.has(WitcheryDataComponents.VAMPIRE_BLOOD.get())
        if (bl2 && stack.get(WitcheryDataComponents.VAMPIRE_BLOOD.get()) == true) {
            tooltipComponents.add(Component.translatable("witchery.vampire_blood").setStyle(Style.EMPTY.withColor(Color(255, 50, 100).rgb)).withStyle(ChatFormatting.ITALIC))
        } else if (bl && stack.get(WitcheryDataComponents.BLOOD.get()) != null) {
            tooltipComponents.add(Component.translatable("witchery.blood").setStyle(Style.EMPTY.withColor(Color(255, 50, 80).rgb)))
        } else {
            tooltipComponents.add(Component.translatable("witchery.use_with_needle"))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget is LilithEntity && interactionTarget.entityData.get(LilithEntity.IS_DEFEATED)) {
            stack.set(WitcheryDataComponents.VAMPIRE_BLOOD.get(), true)
            stack.set(WitcheryDataComponents.BLOOD.get(), interactionTarget.uuid)
            interactionTarget.discard()
            return InteractionResult.SUCCESS
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }
}