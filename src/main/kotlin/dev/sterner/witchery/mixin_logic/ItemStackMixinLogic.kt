package dev.sterner.witchery.mixin_logic

import dev.sterner.witchery.core.api.PoppetLocation
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.features.misc.AccessoryHandler
import dev.sterner.witchery.features.poppet.CorruptPoppetPlayerAttachment
import dev.sterner.witchery.features.poppet.PoppetHandler
import dev.sterner.witchery.features.poppet.PoppetLevelAttachment
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object ItemStackMixinLogic {

    fun armorProtection(player: Player?): Boolean {
        if (player == null) {
            return false
        }

        val armorPoppetType: PoppetType = WitcheryPoppetRegistry.ARMOR_PROTECTION.get()
        val poppetData = PoppetHandler.findPoppet(player, armorPoppetType)
        val (poppetStack, location) = poppetData

        if (poppetStack == null || location == null) {
            return false
        }

        val corruptData = CorruptPoppetPlayerAttachment.getData(player)
        val isCorrupted = corruptData.corruptedPoppets.contains(armorPoppetType.getRegistryId())

        var activated = false
        for (armor in player.inventory.armor) {
            if (!armor.isEmpty) {
                val maxDurability = armor.maxDamage
                val currentDurability = maxDurability - armor.damageValue

                if (currentDurability <= 1) {
                    val poppetDamage = armorPoppetType.getDurabilityDamage(PoppetUsage.PROTECTION)
                    poppetStack.damageValue += poppetDamage

                    if (!isCorrupted) {
                        armor.damageValue = armor.damageValue.coerceAtMost(maxDurability - 1)
                    } else {
                        armorPoppetType.onCorruptedActivate(player, null)
                    }

                    if (poppetStack.damageValue >= poppetStack.maxDamage) {
                        when (location) {
                            PoppetLocation.ACCESSORY -> AccessoryHandler.removeAccessory(player, armorPoppetType.item)
                            PoppetLocation.INVENTORY -> {
                                poppetStack.shrink(1)
                            }
                            PoppetLocation.WORLD -> {
                                val level = player.level() as? ServerLevel ?: continue
                                val poppetLevelData = PoppetLevelAttachment.getPoppetData(level)
                                val blockPoppet = poppetLevelData.poppetDataMap.find { it.poppetItemStack == poppetStack }
                                if (blockPoppet != null) {
                                    blockPoppet.poppetItemStack.shrink(1)
                                    PoppetLevelAttachment.updatePoppetItem(level, blockPoppet.blockPos, blockPoppet.poppetItemStack)
                                }
                            }
                        }
                    }

                    activated = true
                }
            }
        }

        return if (isCorrupted) false else activated
    }
}