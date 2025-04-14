package dev.sterner.witchery.item.potion

import dev.sterner.witchery.entity.WitcheryThrownPotion
import dev.sterner.witchery.potion.MobEffectPotionEffect
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryDataComponents.DURATION_AMPLIFIER
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryPotionEffectRegistry
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


    private fun formatDuration(ticks: Int): String {
        val totalSeconds = ticks / 20
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun toRoman(number: Int): String {
        val numerals = listOf(
            10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
        )
        var n = number
        val result = StringBuilder()
        for ((value, numeral) in numerals) {
            while (n >= value) {
                result.append(numeral)
                n -= value
            }
        }
        return result.toString()
    }

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
                    val (duration, amp) = getTotalEffectValues(ingredient)

                    tooltipComponents.add(
                        Component.literal(" - ")
                            .append(
                                Component.translatable(ingredient.item.descriptionId)
                                    .withStyle { it.withColor(ingredient.color) }
                            )
                            .append(
                                Component.literal(if(amp > 0) " ${toRoman(amp)}" else "")
                            )
                            .append(
                                Component.literal(" ${formatDuration(duration)}")
                                    .withStyle { it.withColor(Color(120, 180, 180).rgb) }
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

        val lastType = ingredients.lastOrNull()?.type ?: return InteractionResultHolder.pass(stack)

        return when (lastType) {
            WitcheryPotionIngredient.Type.CONSUMABLE -> {
                player.startUsingItem(hand)
                InteractionResultHolder.consume(stack)
            }

            WitcheryPotionIngredient.Type.SPLASH -> {
                if (!level.isClientSide) {
                    val thrown = WitcheryThrownPotion(level, player)
                    thrown.item = stack
                    thrown.shootFromRotation(player, player.xRot, player.yRot, 0f, 1.5f, 1.0f)
                    level.addFreshEntity(thrown)
                    if (!player.abilities.instabuild) {
                        stack.shrink(1)
                    }
                }
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }

            WitcheryPotionIngredient.Type.LINGERING -> {
                // TODO: Handle lingering potion throw (e.g. create AreaEffectCloud)
                InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
            }
        }
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = 32


    override fun getUseAnimation(stack: ItemStack): UseAnim {
        val ingredients = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return UseAnim.NONE
        val lastType = ingredients.lastOrNull()?.type ?: return UseAnim.NONE
        return if (lastType == WitcheryPotionIngredient.Type.CONSUMABLE) UseAnim.DRINK else UseAnim.NONE
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (entity is Player && !entity.abilities.instabuild) {
            stack.shrink(1)
        }

        if (stack.has(WITCHERY_POTION_CONTENT.get())) {
            val ingredients = stack.get(WITCHERY_POTION_CONTENT.get())
            if (ingredients != null) {
                val dura = stack.get(DURATION_AMPLIFIER.get())!!
                for ((i, ingredient) in ingredients.withIndex()) {
                    if (i == 0) continue

                    val instance = WitcheryPotionEffectRegistry.EFFECTS.get(ingredient.effect.effectId)
                    if (instance is MobEffectPotionEffect) {
                        val effectData = dura[i - 1]
                        println(dura)
                        println(i)
                        entity.addEffect(MobEffectInstance(instance.mobEffect, effectData.duration, effectData.amplifier))
                    }
                }

            }
        }

        return stack
    }

    companion object {

        fun getTotalEffectValues(ingredient: WitcheryPotionIngredient): Pair<Int, Int> {
            val baseEffect = ingredient.effect
            var totalAmplifier = baseEffect.amplifier
            var totalDuration = baseEffect.duration

            var addedPower = 0
            var addedDuration = 0
            var durationMultiplier = 1

            ingredient.effectModifier.ifPresent { modifier ->
                addedPower += modifier.powerAddition
                addedDuration += modifier.durationAddition
                durationMultiplier *= modifier.durationMultiplier
            }

            totalAmplifier += addedPower
            totalDuration = (totalDuration + addedDuration) * durationMultiplier

            return totalDuration to totalAmplifier
        }

        fun cacheEffectDuration(witchesPotion: ItemStack) {
            val ingredients = witchesPotion.get(WITCHERY_POTION_CONTENT.get()) ?: return

            val effectDurations = ingredients
                .drop(1)
                .map { ingredient ->
                    val (duration, amplifier) = getTotalEffectValues(ingredient)
                    WitcheryDataComponents.DurationAmplifier(duration, amplifier)
                }

            witchesPotion.set(DURATION_AMPLIFIER.get(), effectDurations)
        }

    }
}
