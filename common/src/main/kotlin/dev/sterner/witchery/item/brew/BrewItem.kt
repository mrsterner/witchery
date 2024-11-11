package dev.sterner.witchery.item.brew

import com.google.common.base.Predicate
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.platform.FamiliarLevelAttachment
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

open class BrewItem(open val color: Int, properties: Properties, val predicate: Predicate<Direction> = Predicate { true }) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        val player = if (livingEntity is Player) livingEntity else null
        if (player is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack)
        }

        if (!level.isClientSide && player != null) {
            val frog = FamiliarLevelAttachment.getFamiliarEntityType(player.uuid, level as ServerLevel) == EntityType.FROG
            
            WitcheryApi.makePlayerWitchy(player)
            applyEffectOnSelf(player, frog)
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED[this])
            stack.consume(1, player)
        }

        if (player == null || !player.hasInfiniteMaterials()) {
            if (stack.isEmpty) {
                return ItemStack(Items.GLASS_BOTTLE)
            }

            player?.inventory?.add(ItemStack(Items.GLASS_BOTTLE))
        }

        livingEntity.gameEvent(GameEvent.DRINK)
        return stack
    }


    override fun useOn(context: UseOnContext): InteractionResult {
        return InteractionResult.PASS
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return DRINK_DURATION
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return ItemUtils.startUsingInstantly(level, player, usedHand)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    open fun applyEffectOnSelf(player: Player, hasFrog: Boolean) {

    }

    open fun applyEffectOnEntities(level: Level, livingEntity: LivingEntity, hasFrog: Boolean) {

    }

    open fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult, hasFrog: Boolean) {

    }

    open fun applyEffectOnHitLocation(level: Level, location: Vec3, hasFrog: Boolean) {

    }

    companion object {
        private const val DRINK_DURATION = 32
    }
}