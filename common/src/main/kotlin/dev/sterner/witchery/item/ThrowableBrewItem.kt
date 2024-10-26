package dev.sterner.witchery.item

import dev.sterner.witchery.entity.ThrownBrewEntity
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileItem
import net.minecraft.world.level.Level

class ThrowableBrewItem(override val color: Int, properties: Properties) : BrewItem(color, properties), ProjectileItem {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (!level.isClientSide) {
            val thrownPotion = ThrownBrewEntity(level, player)
            thrownPotion.item = itemStack
            thrownPotion.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
            level.addFreshEntity(thrownPotion)

        }

        player.awardStat(Stats.ITEM_USED[this])
        itemStack.consume(1, player)
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
    }

    override fun asProjectile(level: Level, pos: Position, stack: ItemStack, direction: Direction): Projectile {
        val thrownPotion = ThrownBrewEntity(level, pos.x(), pos.y(), pos.z())
        thrownPotion.item = stack
        return thrownPotion
    }

    override fun createDispenseConfig(): ProjectileItem.DispenseConfig {
        return ProjectileItem.DispenseConfig.builder()
            .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5f)
            .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25f)
            .build()
    }
}