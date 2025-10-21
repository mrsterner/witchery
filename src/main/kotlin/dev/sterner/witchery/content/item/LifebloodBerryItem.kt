package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.registry.WitcheryBlocks
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

import dev.sterner.witchery.features.lifeblood.LifebloodHandler
import dev.sterner.witchery.features.lifeblood.LifebloodPlayerAttachment
import net.minecraft.world.entity.player.Player

class LifebloodBerryItem(properties: Properties) : ItemNameBlockItem(WitcheryBlocks.LIFE_BLOOD.get(), properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (!level.isClientSide) {
            livingEntity.heal(2.0f)
            livingEntity.addEffect(MobEffectInstance(MobEffects.REGENERATION, 20 * 2, 0))

            if (livingEntity is Player && LifebloodPlayerAttachment.getData(livingEntity).lifebloodPoints <= LifebloodPlayerAttachment.Data.POINTS_PER_HEART) {
                LifebloodHandler.addLifeblood(livingEntity, 1)
                if (!livingEntity.abilities.instabuild) {
                    stack.shrink(1)
                }
            }
        }
        return super.finishUsingItem(stack, level, livingEntity)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("item.witchery.lifeblood_berry.tooltip")
                .withStyle { it.withColor(0x6BB6FF) }
        )
        tooltipComponents.add(
            Component.translatable("item.witchery.lifeblood_berry.tooltip2")
                .withStyle { it.withColor(0x4D9FFF) }
        )
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}