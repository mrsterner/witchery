package dev.sterner.witchery.poppet

import dev.sterner.witchery.api.interfaces.PoppetType
import dev.sterner.witchery.api.PoppetUsage
import dev.sterner.witchery.item.PoppetItem
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
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
            SoundEvents.ARMOR_EQUIP_IRON,
            SoundSource.PLAYERS,
            0.7f,
            0.8f
        )

        owner.displayClientMessage(
            Component.translatable(
                "curse.witchery.corrupt_poppet.armor_effect",
                Component.translatable(itemToDamage.descriptionId)
            ).withStyle(ChatFormatting.DARK_PURPLE),
            true
        )

        return true
    }
}