package dev.sterner.witchery.item

import dev.sterner.witchery.entity.SleepingPlayerEntity
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level
import java.awt.Color
import java.util.*


class TaglockItem(properties: Properties) : Item(properties) {

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (stack.has(WitcheryDataComponents.TIMESTAMP.get()) && getPlayer(level, stack) != null) {
            val timestamp = stack.get(WitcheryDataComponents.TIMESTAMP.get())
            if (timestamp != null) {
                val currentGameTime = level.gameTime

                // Check if 7 days (168000 ticks) have passed since the timestamp
                if (currentGameTime - timestamp >= 168000) {
                    stack.set(WitcheryDataComponents.EXPIRED_TAGLOCK.get(), true)
                }
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val player = getPlayerProfile(stack)
        val living = getLivingEntityName(stack)
        if (player != null) {
            tooltipComponents.add(
                Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255, 2, 100).rgb))
            )
        } else if (living != null) {
            tooltipComponents.add(
                Component.translatable(living).setStyle(Style.EMPTY.withColor(Color(255, 100, 100).rgb))
            )
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {

        fun bindPlayerOrLiving(livingEntity: LivingEntity, stack: ItemStack) {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return
            }

            if (livingEntity is Player) {
                stack.set(DataComponents.PROFILE, ResolvableProfile(livingEntity.gameProfile))
                stack.set(
                    WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
                    livingEntity.gameProfile.name.replaceFirstChar(Char::uppercase)
                )
            } else {
                stack.set(
                    WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
                    livingEntity.type.descriptionId.toString()
                )
            }

            stack.set(WitcheryDataComponents.ENTITY_ID_COMPONENT.get(), livingEntity.stringUUID)
        }

        fun getLivingEntityName(stack: ItemStack): String? {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return null
            }
            val id = stack.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())
            return id
        }

        fun getPlayerProfile(stack: ItemStack): ResolvableProfile? {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return null
            }

            return if (stack.has(DataComponents.PROFILE)) stack.get(DataComponents.PROFILE) else null
        }

        fun getPlayer(level: Level, stack: ItemStack): Player? {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return null
            }

            val profile = stack.get(DataComponents.PROFILE)
            if (profile != null && profile.id.isPresent) {
                return level.getPlayerByUUID(profile.id.get())
            }
            return null
        }

        fun getLivingEntity(level: Level, stack: ItemStack): LivingEntity? {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return null
            }

            if (stack.has(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())) {
                val id = stack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
                if (id != null) {
                    for (serverLevel in level.server!!.allLevels) {
                        if (UUID.fromString(id) != null && serverLevel.getEntity(UUID.fromString(id)) != null) {
                            return serverLevel.getEntity(UUID.fromString(id)) as LivingEntity
                        }
                    }
                }
            }

            return null
        }

        fun bindSleepingPlayer(sleepingPlayerEntity: SleepingPlayerEntity, stack: ItemStack) {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return
            }

            stack.set(DataComponents.PROFILE, sleepingPlayerEntity.data.resolvableProfile)
            stack.set(
                WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(),
                sleepingPlayerEntity.data.resolvableProfile!!.gameProfile.name?.replaceFirstChar(Char::uppercase)
            )

            stack.set(
                WitcheryDataComponents.ENTITY_ID_COMPONENT.get(),
                sleepingPlayerEntity.getSleepingUUID().toString()
            )
        }
    }
}