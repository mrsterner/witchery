package dev.sterner.witchery.features.poppet

import dev.sterner.witchery.core.api.interfaces.PoppetType
import dev.sterner.witchery.core.api.PoppetUsage
import dev.sterner.witchery.content.item.PoppetItem
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class ArmorProtectionPoppet : PoppetType {
    override val item: Item = WitcheryItems.ARMOR_PROTECTION_POPPET.get()

    override fun isValidFor(entity: LivingEntity, source: DamageSource?): Boolean {
        return true
    }

    override fun getDurabilityDamage(usage: PoppetUsage): Int {
        return 1
    }

    override fun onActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.ARMOR_STAND_HIT,
            SoundSource.PLAYERS,
            0.7f,
            1.2f
        )
        return true
    }

    override fun onCorruptedActivate(owner: LivingEntity, source: DamageSource?): Boolean {
        if (owner !is Player) return onActivate(owner, source)

        val equipment = mutableListOf<ItemStack>()

        for (slot in EquipmentSlot.entries) {
            val item = owner.getItemBySlot(slot)
            if (!item.isEmpty && item.maxDamage > 0 && item.item !is PoppetItem) {
                equipment.add(item)
            }
        }

        if (equipment.isEmpty()) return onActivate(owner, source)

        val itemToDamage = equipment.random()
        itemToDamage.hurtAndBreak(
            owner.level().random.nextInt(3) + 1,
            owner,
            null
        )

        owner.level().playSound(
            null,
            owner.x, owner.y, owner.z,
            SoundEvents.ITEM_BREAK,
            SoundSource.PLAYERS,
            0.5f,
            0.8f
        )

        return true
    }
}