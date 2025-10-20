package dev.sterner.witchery.core.api

import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment
import net.minecraft.world.entity.player.Player

object InventorySlots {
    // Hotbar: 0-8
    const val HOTBAR_START = 0
    const val HOTBAR_END = 8

    // Main inventory: 9-35
    const val MAIN_START = 9
    const val MAIN_END = 35

    // Armor: 36-39
    const val BOOTS = 36
    const val LEGGINGS = 37
    const val CHESTPLATE = 38
    const val HELMET = 39

    // Offhand: 40
    const val OFFHAND = 40

    // Lock methods
    fun lockHotbar(player: Player) {
        val slots = (HOTBAR_START..HOTBAR_END).toSet()
        InventoryLockPlayerAttachment.lockSlots(player, slots)
    }

    fun lockArmor(player: Player) {
        val slots = setOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET)
        InventoryLockPlayerAttachment.lockSlots(player, slots)
    }

    fun lockMainInventory(player: Player) {
        val slots = (MAIN_START..MAIN_END).toSet()
        InventoryLockPlayerAttachment.lockSlots(player, slots)
    }

    fun lockAllExceptHotbar(player: Player) {
        val slots = ((MAIN_START..MAIN_END) + listOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET, OFFHAND)).toSet()
        InventoryLockPlayerAttachment.lockSlots(player, slots)
    }

    fun lockAll(player: Player) {
        val slots = (0..40).toSet()
        InventoryLockPlayerAttachment.lockSlots(player, slots)
    }

    // Unlock methods
    fun unlockHotbar(player: Player) {
        val slots = (HOTBAR_START..HOTBAR_END).toSet()
        InventoryLockPlayerAttachment.unlockSlots(player, slots)
    }

    fun unlockArmor(player: Player) {
        val slots = setOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET)
        InventoryLockPlayerAttachment.unlockSlots(player, slots)
    }

    fun unlockMainInventory(player: Player) {
        val slots = (MAIN_START..MAIN_END).toSet()
        InventoryLockPlayerAttachment.unlockSlots(player, slots)
    }

    fun unlockAllExceptHotbar(player: Player) {
        val slots = ((MAIN_START..MAIN_END) + listOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET, OFFHAND)).toSet()
        InventoryLockPlayerAttachment.unlockSlots(player, slots)
    }

    fun unlockAll(player: Player) {
        InventoryLockPlayerAttachment.unlockAllSlots(player)
    }

    // Toggle methods
    fun toggleHotbar(player: Player) {
        val data = InventoryLockPlayerAttachment.getData(player)
        val hotbarSlots = (HOTBAR_START..HOTBAR_END).toSet()
        if (hotbarSlots.any { data.lockedSlots.contains(it) }) {
            unlockHotbar(player)
        } else {
            lockHotbar(player)
        }
    }

    fun toggleArmor(player: Player) {
        val data = InventoryLockPlayerAttachment.getData(player)
        val armorSlots = setOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET)
        if (armorSlots.any { data.lockedSlots.contains(it) }) {
            unlockArmor(player)
        } else {
            lockArmor(player)
        }
    }

    fun toggleMainInventory(player: Player) {
        val data = InventoryLockPlayerAttachment.getData(player)
        val mainSlots = (MAIN_START..MAIN_END).toSet()
        if (mainSlots.any { data.lockedSlots.contains(it) }) {
            unlockMainInventory(player)
        } else {
            lockMainInventory(player)
        }
    }

    // Query methods
    fun isHotbarLocked(player: Player): Boolean {
        val data = InventoryLockPlayerAttachment.getData(player)
        return (HOTBAR_START..HOTBAR_END).all { data.lockedSlots.contains(it) }
    }

    fun isArmorLocked(player: Player): Boolean {
        val data = InventoryLockPlayerAttachment.getData(player)
        return setOf(BOOTS, LEGGINGS, CHESTPLATE, HELMET).all { data.lockedSlots.contains(it) }
    }

    fun isMainInventoryLocked(player: Player): Boolean {
        val data = InventoryLockPlayerAttachment.getData(player)
        return (MAIN_START..MAIN_END).all { data.lockedSlots.contains(it) }
    }

    fun getLockedSlotsCount(player: Player): Int {
        return InventoryLockPlayerAttachment.getData(player).lockedSlots.size
    }
}