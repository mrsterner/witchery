package dev.sterner.witchery.tarot

import dev.sterner.witchery.data_attachment.TarotPlayerAttachment
import dev.sterner.witchery.registry.WitcheryTarotEffects
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.living.LivingEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent

object TarotEffectEventHandler {

    private var lastDayTime = 0L

    @SubscribeEvent
    fun onItemToss(event: ItemTossEvent) {
        val player = event.player
        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false

            if (cardNumber == 13 && isReversed) {
                event.isCanceled = true

                val itemEntity = event.entity
                if (!player.inventory.add(itemEntity.item.copy())) {
                    itemEntity.discard()
                } else {
                    itemEntity.discard()
                }

                return
            }
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (player.level().isClientSide) return

        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        TheMagicianEffect.TheMagicianBrewReturn.tick(player)

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            effect?.onTick(player, isReversed)
        }

        val currentTime = player.level().dayTime % 24000
        if (currentTime < lastDayTime) {
            for (i in data.drawnCards.indices) {
                val cardNumber = data.drawnCards[i]
                val isReversed = data.reversedCards.getOrNull(i) ?: false
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onMorning(player, isReversed)
            }
        } else if (lastDayTime < 12000 && currentTime >= 12000) {
            for (i in data.drawnCards.indices) {
                val cardNumber = data.drawnCards[i]
                val isReversed = data.reversedCards.getOrNull(i) ?: false
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onNightfall(player, isReversed)
            }
        }
        lastDayTime = currentTime
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        val player = event.player as? ServerPlayer ?: return
        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            effect?.onBlockBreak(player, event.state, event.pos, isReversed)
        }
    }

    @SubscribeEvent
    fun onEntityHurt(event: AttackEntityEvent) {
        val attacker = event.entity as? ServerPlayer ?: return
        val data = TarotPlayerAttachment.getData(attacker)
        if (data.drawnCards.isEmpty()) return

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            effect?.onEntityHit(attacker, event.entity, isReversed)
        }
    }

    @SubscribeEvent
    fun onPlayerHurt(event: LivingIncomingDamageEvent) {
        val player = event.entity as? ServerPlayer ?: return
        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        var modifiedDamage = event.amount

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            modifiedDamage = effect?.onPlayerHurt(player, event.source, modifiedDamage, isReversed) ?: modifiedDamage
        }

        event.amount = modifiedDamage
    }

    @SubscribeEvent
    fun onEntityKill(event: LivingDeathEvent) {
        val attacker = event.source.entity as? ServerPlayer ?: return
        val victim = event.entity ?: return

        val data = TarotPlayerAttachment.getData(attacker)
        if (data.drawnCards.isEmpty()) return

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            effect?.onEntityKill(attacker, victim, isReversed)
        }
    }

    @SubscribeEvent
    fun onItemUse(event: LivingEntityUseItemEvent.Finish) {
        val player = event.entity as? ServerPlayer ?: return
        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
            effect?.onItemUse(player, event.item, isReversed)
        }
    }

    @SubscribeEvent
    fun onPlayerSleep(event: SleepFinishedTimeEvent) {
        for (player in event.level.players()) {
            if (player !is ServerPlayer) continue
            val data = TarotPlayerAttachment.getData(player)
            if (data.drawnCards.isEmpty()) continue

            for (i in data.drawnCards.indices) {
                val cardNumber = data.drawnCards[i]
                val isReversed = data.reversedCards.getOrNull(i) ?: false
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onSleep(player, isReversed)
            }
        }
    }

    @SubscribeEvent
    fun onEntityEnterWater(event: PlayerTickEvent.Post) {
        val entity = event.entity
        if (entity !is ServerPlayer) return
        if (entity.level().isClientSide) return

        if (entity.isInWater) {
            val data = TarotPlayerAttachment.getData(entity)
            if (data.drawnCards.isEmpty()) return

            for (i in data.drawnCards.indices) {
                val cardNumber = data.drawnCards[i]
                val isReversed = data.reversedCards.getOrNull(i) ?: false
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onEnterWater(entity, isReversed)
            }
        }
    }

    fun register(bus: IEventBus) {
        bus.register(this)
    }
}