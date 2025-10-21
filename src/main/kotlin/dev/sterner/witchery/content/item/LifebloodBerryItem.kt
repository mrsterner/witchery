package dev.sterner.witchery.content.item

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class LifebloodBerryItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (!level.isClientSide) {
            livingEntity.heal(2.0f)
            livingEntity.addEffect(MobEffectInstance(MobEffects.REGENERATION, 60, 0))
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
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}