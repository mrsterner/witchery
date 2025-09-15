package dev.sterner.witchery.item

import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.registry.WitcheryDamageSources
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class WoodenStakeItem(properties: Properties) : Item(properties) {

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if (target is VampireEntity || (target is Player && AfflictionPlayerAttachment.getData(target)
                .getVampireLevel() > 0)
        ) {
            val source = (target.damageSources() as DamageSourcesInvoker).invokeSource(WitcheryDamageSources.IN_SUN)
            target.hurt(source, 4f)
        }
        return super.hurtEnemy(stack, target, attacker)
    }
}