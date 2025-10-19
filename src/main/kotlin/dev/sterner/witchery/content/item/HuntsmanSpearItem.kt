package dev.sterner.witchery.content.item

import dev.sterner.witchery.content.entity.HornedHuntsmanEntity
import dev.sterner.witchery.content.entity.HuntsmanSpearEntity
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class HuntsmanSpearItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        player.startUsingItem(hand)
        return InteractionResultHolder.consume(itemStack)
    }

    override fun getUseAnimation(itemStack: ItemStack): UseAnim {
        return UseAnim.SPEAR
    }

    override fun getUseDuration(itemStack: ItemStack, entity: LivingEntity): Int {
        return 72000
    }

    override fun releaseUsing(itemStack: ItemStack, level: Level, user: LivingEntity, timeLeft: Int) {
        if (user !is Player) return

        val useTime = getUseDuration(itemStack, user) - timeLeft
        if (useTime < 10) return

        if (!level.isClientSide) {
            val spearEntity = HuntsmanSpearEntity(level, user, itemStack.copy())

            val power = 0.9f.coerceAtMost(useTime / 20.0f)

            spearEntity.shootFromRotation(user, user.xRot, user.yRot, 0.0f, power * 2.5f, 1.0f)

            level.addFreshEntity(spearEntity)

            level.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.TRIDENT_THROW,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )

            if (!user.abilities.instabuild) {
                itemStack.shrink(1)
            }
        }

        user.awardStat(Stats.ITEM_USED.get(this))
    }

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        val bonusDamage = if (attacker is HornedHuntsmanEntity) 7.0f else 0.0f

        if (bonusDamage > 0 && target.isAlive) {
            target.hurt(target.level().damageSources().mobAttack(attacker), bonusDamage)
        }

        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND)

        return true
    }
}