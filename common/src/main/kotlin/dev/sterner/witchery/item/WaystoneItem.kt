package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.Level
import javax.xml.crypto.Data

class WaystoneItem(properties: Properties) : Item(properties) {

    fun bindGlobalBlockPos(level: Level, pos: BlockPos, stack: ItemStack) {
        stack.set(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get(), GlobalPos.of(level.dimension(), pos))
    }

    fun getGlobalPos(level: Level, stack: ItemStack): GlobalPos? {
        return stack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
    }

    fun bindLivingEntity(level: Level, livingEntity: LivingEntity, stack: ItemStack) {
        stack.set(WitcheryDataComponents.ENTITY_ID_COMPONENT.get(), livingEntity.id)
    }

    fun getLivingEntity(level: Level, stack: ItemStack): LivingEntity? {
        val id = stack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
        if (id != null && level.getEntity(id) is LivingEntity) {
            val living = level.getEntity(id) as LivingEntity
            return living
        }
       return null
    }

    fun bindPlayer(level: Level, player: Player, stack: ItemStack) {
        stack.set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
    }

    fun getPlayer(level: Level, stack: ItemStack): Player? {
        val profile = stack.get(DataComponents.PROFILE)
        if (profile != null && profile.id.isPresent) {
            return level.getPlayerByUUID(profile.id.get())
        }
        return null
    }
}