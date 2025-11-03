package dev.sterner.witchery.core.util

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.ArgumentTypeInfosInvoker
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.PlayerAdvancements
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.common.NeoForgeMod
import top.theillusivec4.curios.api.CuriosApi


object WitcheryUtil {

    fun hasAdvancement(serverPlayer: ServerPlayer, advancementResourceLocation: ResourceLocation): Boolean {
        if (serverPlayer.getServer() == null) {
            return false
        }

        val manager = serverPlayer.server.advancements
        val tracker: PlayerAdvancements = serverPlayer.advancements

        val advancement = manager.get(advancementResourceLocation)
        if (advancement != null) {
            return tracker.getOrStartProgress(advancement).isDone
        }

        return false
    }

    fun grantAdvancementCriterion(
        serverPlayer: ServerPlayer,
        advancementResourceLocation: ResourceLocation,
        criterion: String
    ) {
        if (serverPlayer.getServer() == null) {
            return
        }
        val manager = serverPlayer.server.advancements
        val tracker: PlayerAdvancements = serverPlayer.advancements

        val advancement = manager.get(advancementResourceLocation)
        if (advancement != null) {
            if (!tracker.getOrStartProgress(advancement).isDone) {
                tracker.award(advancement, criterion)
            }
        }
    }

    fun rotateShape(from: Direction, to: Direction, shape: VoxelShape): VoxelShape {
        val buffer = arrayOf<VoxelShape>(shape, Shapes.empty())

        val times: Int = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4
        for (i in 0 until times) {
            buffer[0].forAllBoxes { minX, minY, minZ, maxX, maxY, maxZ ->
                buffer[1] = Shapes.join(
                    buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX),
                    BooleanOp.OR
                )
            }
            buffer[0] = buffer[1]
            buffer[1] = Shapes.empty()
        }
        return buffer[0]
    }

    fun formatDuration(ticks: Int): String {
        val totalSeconds = ticks / 20
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun toRoman(number: Int): String {
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

    fun addItemToInventoryAndConsume(player: Player, hand: InteractionHand, itemToAdd: ItemStack) {
        val currentItemStack: ItemStack = player.getItemInHand(hand)
        if (currentItemStack.isEmpty) {
            player.setItemInHand(hand, itemToAdd)
        } else {
            if (currentItemStack.count == 1) {
                player.setItemInHand(hand, itemToAdd)
            } else {
                currentItemStack.shrink(1)
                if (!player.inventory.add(itemToAdd)) {
                    player.drop(itemToAdd, false, true)
                }
            }
        }
    }

    @JvmStatic
    fun allEquippedAccessories(livingEntity: Player): List<ItemStack> {
        val curioInventory = CuriosApi.getCuriosInventory(livingEntity).orElse(null) ?: return emptyList()

        val equippedCurios = curioInventory.equippedCurios
        val equippedItems = mutableListOf<ItemStack>()

        for (slotIndex in 0 until equippedCurios.slots) {
            val itemStack = equippedCurios.getStackInSlot(slotIndex)
            if (!itemStack.isEmpty) {
                equippedItems.add(itemStack)
            }
        }

        return equippedItems
    }

    @JvmStatic
    fun tryEnableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            if (flightAttribute != null && flightAttribute.value <= 0) {
                flightAttribute.addPermanentModifier(
                    AttributeModifier(
                        Witchery.id("bat_flight"),
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                    )
                )
            }

            if (!player.onGround()) {
                player.abilities.flying = true
                player.onUpdateAbilities()
            }
        }
    }


    @JvmStatic
    fun tryDisableBatFlight(player: Player) {
        if (!player.isCreative && !player.isSpectator) {
            val flightAttribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            flightAttribute?.removeModifier(Witchery.id("bat_flight"))

            player.abilities.flying = false
            player.onUpdateAbilities()
        }
    }


    @JvmStatic
    fun registerWoodType(woodType: WoodType): WoodType {
        return WoodType.register(woodType)
    }

    @JvmStatic
    fun getByClass(): MutableMap<Class<*>, ArgumentTypeInfo<*, *>> {
        return ArgumentTypeInfosInvoker.getByClass()
    }

    @JvmStatic
    fun createProxiedDamage(source: DamageSource, newAttacker: Entity): DamageSource? {
        if (source.entity != null) {
            val damageType = source.typeHolder()
            val key = damageType.getKey()

            if (key != null) {
                return newAttacker.damageSources().source(key, source.directEntity, source.entity)
            }
        }

        return null
    }
}