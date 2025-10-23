package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class WheelOfFortuneEffect : TarotEffect(11) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Wheel of Fortune (Reversed)" else "Wheel of Fortune"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "The wheel turns against you - constant bad luck"
        else "Fortune's favor - increased luck, rare drops from slain enemies"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.LUCK)) {
                player.addEffect(MobEffectInstance(MobEffects.LUCK, 400, 1, true, false))
            }
        } else {
            if (!player.hasEffect(MobEffects.UNLUCK)) {
                player.addEffect(MobEffectInstance(MobEffects.UNLUCK, 400, 1, true, false))
            }
        }
    }

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed) {

            if (player.level().random.nextFloat() < 0.25f && player.level() is ServerLevel) {
                val level = player.level() as ServerLevel
                val lootTable = entity.lootTable

                val lootParams = LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, entity)
                    .withParameter(LootContextParams.ORIGIN, entity.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, player.damageSources().playerAttack(player))
                    .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .create(LootContextParamSets.ENTITY)

                val loot = level.server.reloadableRegistries().getLootTable(lootTable)
                val items = loot.getRandomItems(lootParams)

                items.forEach { itemStack ->
                    Block.popResource(level, entity.blockPosition(), itemStack)
                }
            }
        }
    }
}