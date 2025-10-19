package dev.sterner.witchery.features.brewing.potion

import dev.sterner.witchery.entity.WitcheryThrownPotion
import dev.sterner.witchery.registry.WitcheryDataComponents.WITCHERY_POTION_CONTENT
import dev.sterner.witchery.registry.WitcheryMobEffects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import dev.sterner.witchery.core.util.WitcheryUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import net.minecraft.util.StringUtil
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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
            val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get())

            if (potionContentList != null) {
                if (potionContentList.isNotEmpty()) {


                    tooltipComponents.add(
                        Component.translatable(
                            "Type: " + potionContentList.last().type.name.lowercase()
                                .replaceFirstChar { it.uppercase() })
                            .setStyle(Style.EMPTY.withColor(Color(120, 180, 180).rgb))
                    )

                    tooltipComponents.add(
                        Component.translatable("Ingredients: ")
                            .setStyle(Style.EMPTY.withColor(Color(120, 180, 180).rgb))
                    )
                    for ((index, potionContent) in potionContentList.withIndex()) {
                        if (index == 0) continue

                        val shift = true
                        val capacity = potionContent.capacityCost
                        val special = potionContent.specialEffect

                        tooltipComponents.add(
                            Component.literal(" - ")
                                .append(
                                    Component.translatable(potionContent.item.descriptionId)
                                        .withStyle { it.withColor(potionContent.color) }
                                )
                                .append(
                                    if (potionContent.effect != WitcheryMobEffects.EMPTY) {
                                        Component.literal(" (Effect: ")
                                            .append(Component.translatable(MobEffectInstance(potionContent.effect).descriptionId))
                                            .append(Component.literal(")"))
                                    } else {
                                        Component.literal("")
                                    }
                                )
                                .append(
                                    if (shift && capacity > 0) {
                                        Component.literal(" (Capacity: +$capacity)")
                                    } else {
                                        Component.literal("")
                                    }
                                )
                                .append(
                                    if (shift && special.isPresent) {
                                        Component.literal(" (Special: ")
                                            .append(Component.translatable(special.get().toString()))
                                            .append(Component.literal(")"))
                                    } else {
                                        Component.literal("")
                                    }
                                )
                        )

                    }
                    addPotionTooltip(tooltipComponents, potionContentList, context.tickRate())
                }
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    private fun formatDuration(
        effect: WitcheryPotionIngredient,
        ticksPerSecond: Float,
        modifier: WitcheryPotionIngredient.EffectModifier
    ): Component {
        val i = Mth.floor((effect.baseDuration.toFloat() + modifier.durationAddition) * modifier.durationMultiplier)
        return Component.literal(StringUtil.formatTickDuration(i, ticksPerSecond))
    }


    private fun addPotionTooltip(
        tooltipComponents: MutableList<Component>,
        potionContentList: List<WitcheryPotionIngredient>,
        tickRate: Float
    ) {
        val globalModifier = getMergedEffectModifier(potionContentList)
        var shouldInvertNext = false

        for (ingredient in potionContentList) {
            if (ingredient.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                shouldInvertNext = true
                continue
            }

            if (ingredient.effect == WitcheryMobEffects.EMPTY) continue

            val effect = if (shouldInvertNext) {
                shouldInvertNext = false
                WitcheryMobEffects.invertEffect(ingredient.effect)
            } else {
                ingredient.effect
            }

            var mutableComponent = Component.translatable(MobEffectInstance(effect).descriptionId)

            if (globalModifier.powerAddition > 0) {
                mutableComponent = Component.translatable(
                    "potion.withAmplifier",
                    mutableComponent,
                    Component.translatable("potion.potency.${globalModifier.powerAddition}")
                )
            }

            val totalDuration =
                (ingredient.baseDuration + globalModifier.durationAddition) * globalModifier.durationMultiplier
            if (totalDuration >= 20) {
                mutableComponent = Component.translatable(
                    "potion.withDuration",
                    mutableComponent,
                    formatDuration(ingredient, tickRate, globalModifier)
                )
            }

            tooltipComponents.add(mutableComponent.withStyle(effect.value().category.tooltipFormatting))
        }
    }


    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return InteractionResultHolder.pass(stack)

        val hasLingering = potionContentList.any { it.type == WitcheryPotionIngredient.Type.LINGERING }
        val hasSplash = potionContentList.any { it.type == WitcheryPotionIngredient.Type.SPLASH }

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
            WitcheryUtil.addItemToInventoryAndConsume(entity, entity.usedItemHand, Items.GLASS_BOTTLE.defaultInstance)
        }

        val potionContentList = stack.get(WITCHERY_POTION_CONTENT.get()) ?: return stack
        val globalModifier = getMergedEffectModifier(potionContentList)

        var shouldInvertNext = false

        if (!level.isClientSide) {
            for (potionContent in potionContentList) {

                if (potionContent.generalModifier.contains(WitcheryPotionIngredient.GeneralModifier.INVERT_NEXT)) {
                    shouldInvertNext = true
                    continue
                }

                val effect = if (shouldInvertNext) {
                    shouldInvertNext = false
                    WitcheryMobEffects.invertEffect(potionContent.effect)
                } else {
                    potionContent.effect
                }

                val baseDuration =
                    (potionContent.baseDuration + globalModifier.durationAddition) * globalModifier.durationMultiplier
                val amplifier = globalModifier.powerAddition

                if (effect != WitcheryMobEffects.EMPTY) {
                    val isInstantEffect = effect.value().isInstantenous
                    val finalDuration = if (isInstantEffect) 0 else baseDuration

                    entity.addEffect(MobEffectInstance(effect, finalDuration, amplifier))
                }

                println(potionContent.specialEffect.isPresent)
                if (potionContent.specialEffect.isPresent) {
                    val special =
                        WitcherySpecialPotionEffects.SPECIAL_REGISTRY.get(potionContent.specialEffect.get())
                    special?.onDrunk(level, entity, baseDuration, amplifier)
                }
            }
        }

        return stack
    }


    companion object {

        fun tryAddItemToPotion(
            potion: MutableList<WitcheryPotionIngredient>,
            toAdd: WitcheryPotionIngredient
        ): Boolean {

            if (potion.map { it.item }.contains(toAdd.item)) {
                return false
            }

            var totalCapacity = 0

            for (ingredient in potion) {
                totalCapacity += ingredient.capacityCost
            }

            if (totalCapacity + toAdd.capacityCost >= 0) {
                potion.add(toAdd)
                return true
            }

            return false
        }

        fun getMergedEffectModifier(potionContentList: List<WitcheryPotionIngredient>): WitcheryPotionIngredient.EffectModifier {
            var powerAddition = 0
            var durationAddition = 0
            var durationMultiplier = 1

            for (ingredient in potionContentList) {
                val mod = ingredient.effectModifier
                powerAddition = maxOf(powerAddition, mod.powerAddition)
                durationAddition = maxOf(durationAddition, mod.durationAddition)
                durationMultiplier = maxOf(durationMultiplier, mod.durationMultiplier)
            }

            return WitcheryPotionIngredient.EffectModifier(powerAddition, durationAddition, durationMultiplier)
        }

        fun getMergedDisperseModifier(potionContentList: List<WitcheryPotionIngredient>): WitcheryPotionIngredient.DispersalModifier {
            var rangeModifier = 1
            var lingeringDurationModifier = 1

            for (ingredient in potionContentList) {
                val mod = ingredient.dispersalModifier
                rangeModifier *= mod.rangeModifier
                lingeringDurationModifier *= mod.lingeringDurationModifier
            }

            return WitcheryPotionIngredient.DispersalModifier(rangeModifier, lingeringDurationModifier)
        }
    }
}
