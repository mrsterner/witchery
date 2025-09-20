package dev.sterner.witchery.item.accessories

import dev.sterner.witchery.handler.AccessoryHandler
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import top.theillusivec4.curios.api.type.capability.ICurioItem
import java.util.*

open class BitingBeltItem(properties: Properties) : Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)), ICurioItem {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val dualData = stack.get(WitcheryDataComponents.DUAL_POTION_CONTENT.get())
        if (dualData != null) {
            val pos = dualData.positive
            val neg = dualData.negative
            if (pos.isPresent) {
                tooltipComponents.add(Component.literal("Positive:"))
                pos.get().addPotionTooltip({ e: Component? ->
                    tooltipComponents.add(
                        e!!
                    )
                }, 1.0f, context.tickRate())
            }
            if (neg.isPresent) {
                tooltipComponents.add(Component.literal("Negative:"))
                neg.get().addPotionTooltip({ e: Component? ->
                    tooltipComponents.add(
                        e!!
                    )
                }, 1.0f, context.tickRate())
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {
        fun registerEvents() {
            EntityEvent.LIVING_HURT.register(BitingBeltItem::usePotion)
        }

        private fun usePotion(livingEntity: LivingEntity?, damageSource: DamageSource?, fl: Float): EventResult? {
            if (livingEntity != null) {
                val belt = AccessoryHandler.checkNoConsume(livingEntity, WitcheryItems.BITING_BELT.get())
                if (belt != null) {
                    val dualData = belt.get(WitcheryDataComponents.DUAL_POTION_CONTENT.get())
                    val pos = dualData?.positive ?: Optional.empty()
                    val neg = dualData?.negative ?: Optional.empty()
                    if (pos.isPresent) {
                        pos.get().forEachEffect {
                            if (!livingEntity.hasEffect(it.effect)) {
                                livingEntity.addEffect(it)
                            }
                        }
                    }
                    if (neg.isPresent) {
                        if (damageSource?.entity is LivingEntity) {
                            neg.get().forEachEffect {
                                if (!livingEntity.hasEffect(it.effect)) {
                                    livingEntity.addEffect(it)
                                }
                            }
                        }
                    }
                }
            }

            return EventResult.pass()
        }
    }
}