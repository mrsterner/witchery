package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class WheelOfFortuneEffect : TarotEffect(11) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.wheel_of_fortune.reversed" else "tarot.witchery.wheel_of_fortune"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.wheel_of_fortune.reversed.description" else "tarot.witchery.wheel_of_fortune.description"
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