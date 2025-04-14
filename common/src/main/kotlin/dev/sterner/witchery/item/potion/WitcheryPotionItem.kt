package dev.sterner.witchery.item.potion

import dev.sterner.witchery.entity.WitcheryThrownPotion
import dev.sterner.witchery.potion.MobEffectPotionEffect
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryDataComponents.DURATION_AMPLIFIER
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryPotionEffectRegistry
import dev.sterner.witchery.util.WitcheryUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import java.awt.Color

class WitcheryPotionItem(properties: Properties) : Item(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
            val ingredients = stack.get(WITCHERY_POTION_CONTENT.get())

            if (ingredients != null) {
                tooltipComponents.add(
                    Component.translatable("Type: " + ingredients.last().type.name.lowercase().replaceFirstChar { it.uppercase() })
                        .setStyle(Style.EMPTY.withColor(Color(120, 180, 180).rgb))
                )

                tooltipComponents.add(
                    Component.translatable("Ingredients: ")
                        .setStyle(Style.EMPTY.withColor(Color(120, 180, 180).rgb))
                )
                for ((index, ingredient) in ingredients.withIndex()) {
                    if (index == 0) continue
                    val (duration, amp) = getTotalEffectValues(ingredient, ingredients)

                    tooltipComponents.add(
                        Component.literal(" - ")
                            .append(
                                Component.translatable(ingredient.item.descriptionId)
                                    .withStyle { it.withColor(ingredient.color) }
                            )
                            .append(
                                Component.literal(if(amp > 0) " ${WitcheryUtil.toRoman(amp + 1)}" else "")
                            )
                            .append(
                                if (duration > 0) {
                                    Component.literal(" ${WitcheryUtil.formatDuration(duration)}")
                                        .withStyle { it.withColor(Color(120, 180, 180).rgb) }
                                } else {
                                    Component.literal("")
                                }
                            )
                    )
                }
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        val ingredients = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return InteractionResultHolder.pass(stack)

        val hasLingering = ingredients.any { it.type == WitcheryPotionIngredient.Type.LINGERING }
        val hasSplash = ingredients.any { it.type == WitcheryPotionIngredient.Type.SPLASH }

        return when {
            hasLingering -> {
                if (!level.isClientSide) {
                    val thrown = WitcheryThrownPotion(level, player)
                    thrown.item = stack
                    thrown.lingering = true
                    thrown.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
                    level.addFreshEntity(thrown)
                    if (!player.abilities.instabuild) {
                        stack.shrink(1)
                    }
                }
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }

            hasSplash -> {
                if (!level.isClientSide) {
                    val thrown = WitcheryThrownPotion(level, player)
                    thrown.item = stack
                    thrown.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
                    level.addFreshEntity(thrown)
                    if (!player.abilities.instabuild) {
                        stack.shrink(1)
                    }
                }
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }

            else -> {
                player.startUsingItem(hand)
                InteractionResultHolder.consume(stack)
            }
        }
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = 32

    override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.DRINK

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (entity is Player && !entity.abilities.instabuild) {
            stack.shrink(1)
        }

        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
            val ingredients = stack.get(WITCHERY_POTION_CONTENT.get())
            if (ingredients != null) {
                for ((i, ingredient) in ingredients.withIndex()) {
                    if (i == 0) continue

                    val instance = WitcheryPotionEffectRegistry.EFFECTS.get(ingredient.effect.effectId)
                    if (instance is MobEffectPotionEffect) {
                        val effectData = stack.get(DURATION_AMPLIFIER.get())
                        var duration = 0
                        var amplifier = 0
                        if (effectData != null) {
                            duration = effectData[i - 1].duration
                            amplifier = effectData[i - 1].amplifier
                        }

                        entity.addEffect(MobEffectInstance(instance.mobEffect, duration, amplifier))
                    }

                    ingredient.effect.affectEntity(entity, ingredient)
                }
            }
        }

        return stack
    }

    companion object {

        fun tryAddItemToPotion(potion: MutableList<WitcheryPotionIngredient>, toAdd: WitcheryPotionIngredient): Boolean {

            if (potion.map { it.item }.contains(toAdd.item)) {
                return false
            }

            var totalCapacity = 4

            for (ingredient in potion) {
                totalCapacity += ingredient.capacityCost
            }

            if (totalCapacity + toAdd.capacityCost >= 0) {
                potion.add(toAdd)
                return true
            }

            return false
        }

        fun getTotalEffectValues(ingredient: WitcheryPotionIngredient, allIngredients: List<WitcheryPotionIngredient>): Pair<Int, Int> {
            val baseEffect = ingredient.effect
            var totalAmplifier = baseEffect.amplifier
            var totalDuration = baseEffect.duration

            val highestPowerAddition = allIngredients.mapNotNull { it.effectModifier.orElse(null)?.powerAddition }.maxOrNull() ?: 0

            var addedDuration = 0
            var durationMultiplier = 1

            ingredient.effectModifier.ifPresent { modifier ->
                addedDuration = modifier.durationAddition
                durationMultiplier = modifier.durationMultiplier
            }

            totalAmplifier += highestPowerAddition
            totalDuration = (totalDuration + addedDuration) * durationMultiplier

            return totalDuration to totalAmplifier
        }

        fun cacheEffectDuration(witchesPotion: ItemStack) {
            val ingredients = witchesPotion.get(WITCHERY_POTION_CONTENT.get()) ?: return

            val effectDurations = ingredients
                .drop(1)
                .map { ingredient ->
                    val (duration, amplifier) = getTotalEffectValues(ingredient, ingredients)
                    WitcheryDataComponents.DurationAmplifier(duration, amplifier)
                }

            witchesPotion.set(DURATION_AMPLIFIER.get(), effectDurations)
        }



    }
}
