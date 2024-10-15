package dev.sterner.witchery.item

import dev.sterner.witchery.block.ritual.RitualPatternUtil
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.context.UseOnContext
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
        val player = Minecraft.getInstance().level?.let { getPlayer(it, stack) }
        if (player != null) {
            tooltipComponents.add(
                Component.literal(player.gameProfile.name.replaceFirstChar(Char::uppercase))
                    .setStyle(Style.EMPTY.withColor(Color(255,2,100).rgb)))
        }
        val living = Minecraft.getInstance().level?.let { getLivingEntityName(stack) }
        if (living != null) {
            tooltipComponents.add(Component.translatable(living).setStyle(Style.EMPTY.withColor(Color(255,100,100).rgb)))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    companion object {

        fun bindLivingEntity(livingEntity: LivingEntity, stack: ItemStack) {
            stack.set(WitcheryDataComponents.ENTITY_ID_COMPONENT.get(), livingEntity.stringUUID)
            stack.set(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get(), livingEntity.type.descriptionId.toString())
        }

        fun getLivingEntity(level: Level, stack: ItemStack): LivingEntity? {
            val id = stack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            for (serverLevel in level.server!!.allLevels) {
                val liv = serverLevel.getEntity(UUID.fromString(id))
                if (liv is LivingEntity) {
                    return liv
                }
            }
            return null
        }

        fun getLivingEntityName(stack: ItemStack): String? {
            val id = stack.get(WitcheryDataComponents.ENTITY_NAME_COMPONENT.get())

            return id
        }

        fun bindPlayer(player: Player, stack: ItemStack) {
            if (stack.has(WitcheryDataComponents.EXPIRED_TAGLOCK.get()) && stack.get(WitcheryDataComponents.EXPIRED_TAGLOCK.get())!!) {
                return
            }
            stack.set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
        }

        fun getPlayerProfile(stack: ItemStack): ResolvableProfile? {
            return stack.get(DataComponents.PROFILE)
        }

        fun getPlayer(level: Level, stack: ItemStack): Player? {
            val profile = stack.get(DataComponents.PROFILE)
            if (profile != null && profile.id.isPresent) {
                return level.getPlayerByUUID(profile.id.get())
            }
            return null
        }
    }
}