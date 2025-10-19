package dev.sterner.witchery.item

import dev.sterner.witchery.entity.CustomBoat
import dev.sterner.witchery.entity.CustomChestBoat
import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.BoatItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

class CustomBoatItem(val chest: Boolean, val type: Boat.Type, properties: Properties) :
    BoatItem(chest, type, properties) {

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(interactionHand)
        val hitResult: HitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY)
        if (hitResult.type == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack)
        } else {
            val vec3 = player.getViewVector(1.0f)
            val list = level.getEntities(
                player,
                player.boundingBox.expandTowards(vec3.scale(5.0)).inflate(1.0),
                ENTITY_PREDICATE
            )
            if (list.isNotEmpty()) {
                val vec32 = player.eyePosition
                val var11 = list.iterator()

                while (var11.hasNext()) {
                    val entity = var11.next() as Entity
                    val aABB = entity.boundingBox.inflate(entity.pickRadius.toDouble())
                    if (aABB.contains(vec32)) {
                        return InteractionResultHolder.pass(itemStack)
                    }
                }
            }

            if (hitResult.type == HitResult.Type.BLOCK) {
                val boat = this.getBoat(level, hitResult, itemStack, player)
                boat.variant = this.type
                boat.yRot = player.yRot
                if (!level.noCollision(boat, boat.boundingBox)) {
                    return InteractionResultHolder.fail(itemStack)
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(boat)
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.location)
                        itemStack.consume(1, player)
                    }

                    player.awardStat(Stats.ITEM_USED[this])
                    return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
                }
            } else {
                return InteractionResultHolder.pass(itemStack)
            }
        }
    }

    private fun getBoat(level: Level, hitResult: HitResult, itemStack: ItemStack, player: Player): Boat {
        val vec3 = hitResult.location
        val boat = if (this.chest) CustomChestBoat(level, vec3.x, vec3.y, vec3.z) else CustomBoat(
            level,
            vec3.x,
            vec3.y,
            vec3.z
        )
        if (level is ServerLevel) EntityType.createDefaultStackConfig<Entity>(level, itemStack, player).accept(boat)
        return boat
    }

    companion object {
        val ENTITY_PREDICATE: Predicate<Entity> = EntitySelector.NO_SPECTATORS.and(Entity::isPickable)
    }
}