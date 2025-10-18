package dev.sterner.witchery

import dev.sterner.witchery.api.event.ChainEvent
import dev.sterner.witchery.api.event.SleepingEvent
import dev.sterner.witchery.api.schedule.TickTaskScheduler
import dev.sterner.witchery.block.altar.AltarBlockEntity
import dev.sterner.witchery.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.block.coffin.CoffinBlock
import dev.sterner.witchery.block.mushroom_log.MushroomLogBlock
import dev.sterner.witchery.block.phylactery.PhylacteryBlockEntity
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.curse.CurseOfFragility
import dev.sterner.witchery.data.*
import dev.sterner.witchery.data_attachment.BindingCurseAttachment
import dev.sterner.witchery.data_attachment.DeathQueueLevelAttachment
import dev.sterner.witchery.data_attachment.InventoryLockPlayerAttachment
import dev.sterner.witchery.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.data_attachment.UnderWaterBreathPlayerAttachment
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.data_attachment.possession.PossessedDataAttachment
import dev.sterner.witchery.data_attachment.possession.PossessionComponentAttachment
import dev.sterner.witchery.data_attachment.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.data_attachment.TarotPlayerAttachment
import dev.sterner.witchery.data_attachment.affliction.TransformationPlayerAttachment
import dev.sterner.witchery.handler.*
import dev.sterner.witchery.handler.affliction.*
import dev.sterner.witchery.handler.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.handler.affliction.vampire.VampireChildrenHuntHandler
import dev.sterner.witchery.handler.affliction.vampire.VampireSpecificEventHandler
import dev.sterner.witchery.handler.affliction.werewolf.WerewolfSpecificEventHandler
import dev.sterner.witchery.handler.infusion.InfernalInfusionHandler
import dev.sterner.witchery.handler.infusion.InfusionHandler
import dev.sterner.witchery.handler.infusion.LightInfusionHandler
import dev.sterner.witchery.handler.infusion.OtherwhereInfusionHandler
import dev.sterner.witchery.handler.PoppetHandler
import dev.sterner.witchery.item.CaneSwordItem
import dev.sterner.witchery.item.WineGlassItem
import dev.sterner.witchery.item.accessories.BitingBeltItem
import dev.sterner.witchery.item.brew.BrewOfSleepingItem
import dev.sterner.witchery.registry.WitcheryCommands
import dev.sterner.witchery.registry.WitcheryLootInjects
import dev.sterner.witchery.registry.WitcherySpecialPotionEffects
import dev.sterner.witchery.registry.WitcheryStructureInjects
import dev.sterner.witchery.registry.WitcheryTarotEffects
import dev.sterner.witchery.ritual.BindSpectralCreaturesRitual
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.*
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

object WitcheryNeoForgeEvents {

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        NaturePowerReloadListener.addPending()
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Pre) {
        TickTaskScheduler.tick(event.server)
        EntSpawningHandler.serverTick(event.server)
        ManifestationHandler.tick(event.server)
        TeleportQueueHandler.processQueue(event.server)
        VampireChildrenHuntHandler.tickHuntAllLevels(event.server)
        WitcherySpecialPotionEffects.serverTick(event.server)
    }

    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        VampireSpecificEventHandler.resetNightCount(event.entity)
        VampireSpecificEventHandler.onKillEntity(event.entity, event.source)
        PoppetHandler.onLivingDeath(event, event.entity, event.source)
        NecroHandler.onDeath(event.entity, event.source)
        FamiliarHandler.familiarDeath(event.entity, event.source)
        WerewolfSpecificEventHandler.killEntity(event.entity, event.source)
        LichdomSpecificEventHandler.onDeath(event, event.entity, event.source)
        LichdomSpecificEventHandler.onKillEntity(event.entity, event.source)
        CaneSwordItem.harvestBlood(event.entity, event.source)
    }

    @SubscribeEvent
    fun onLivingTick(event: EntityTickEvent.Post) {
        val entity = event.entity
        if (entity !is LivingEntity) return

        val prevData = VoodooPoppetLivingEntityAttachment.getPoppetData(entity)

        if (prevData.underWaterTicks > 0) {
            val newTicks = prevData.underWaterTicks - 1

            VoodooPoppetLivingEntityAttachment.setPoppetData(
                entity,
                VoodooPoppetLivingEntityAttachment.Data(
                    isUnderWater = true,
                    underWaterTicks = newTicks
                )
            )
        } else if (prevData.isUnderWater) {
            VoodooPoppetLivingEntityAttachment.setPoppetData(
                entity,
                VoodooPoppetLivingEntityAttachment.Data(
                    isUnderWater = false,
                    underWaterTicks = 0
                )
            )
        }

        BloodPoolHandler.tickBloodRegen(entity)
        NecroHandler.tickLiving(entity)
        if (entity is Player) {
            TarotPlayerAttachment.serverTick(entity)

        }
        BindingCurseAttachment.tick(entity)
    }


    @SubscribeEvent
    fun onLivingTick2(event: EntityTickEvent.Post) {
        val entity = event.entity
        if (entity !is LivingEntity) return
        if (entity is Player) {

            PossessionComponentAttachment.get(entity).serverTick()
        }
    }


    @SubscribeEvent
    fun onItemUsedFinish(event: LivingEntityUseItemEvent.Finish) {
        PossessionComponentAttachment.PossessionComponent.cure(event)
    }

    @SubscribeEvent
    fun onLivingHurt(event: LivingIncomingDamageEvent) {
        EquipmentHandler.babaYagaHit(event, event.entity, event.source, event.amount)

        val entity = event.entity
        val damageSource = event.source
        var damage = event.amount

        if (entity is Player) {
            if (ManifestationPlayerAttachment.getData(entity).manifestationTimer > 0) {
                event.amount = 0f
                return
            }
        }

        if (damageSource.entity is Player) {
            val attacker = damageSource.entity as Player
            val wereData = AfflictionPlayerAttachment.getData(attacker)

            if (wereData.getLevel(AfflictionTypes.LYCANTHROPY) > 0) {
                if (TransformationHandler.isWolf(attacker) || TransformationHandler.isWerewolf(attacker)) {
                    damage = WerewolfSpecificEventHandler.modifyWerewolfDamage(
                        attacker, entity, damage
                    )
                }
            }
        }

        val isVamp =
            entity is Player && AfflictionPlayerAttachment.getData(entity).getLevel(AfflictionTypes.VAMPIRISM) > 0
        val isWereMan = entity is Player && AfflictionPlayerAttachment.getData(entity).isWolfManForm()
        val isWere = entity is Player && AfflictionPlayerAttachment.getData(entity).isWolfForm()

        if (!isVamp && !isWere && !isWereMan) {
            val barkMitigated = BarkBeltHandler.hurt(entity, damageSource, damage)
            damage = barkMitigated.coerceAtMost(damage)

            if (damage > 0f) {
                damage = PoppetHandler.onLivingHurt(entity, damageSource, damage)
            }
        } else if (isVamp) {
            if (damage > 0f) {
                damage = AfflictionHandler.handleHurt(entity, damageSource, damage)
            }
        } else if (isWereMan) {
            if (damage > 0f) {
                damage = WerewolfSpecificEventHandler.handleHurtWolfman(damageSource, damage)
            }
        } else if (isWere) {
            if (damage > 0f) {
                damage = WerewolfSpecificEventHandler.handleHurtWolf(damageSource, damage)
            }
        }

        if (damage > 0f) {
            damage = PotionHandler.handleHurt(entity, damageSource, damage)
        }

        if (damage > 0f && entity is Player) {
            damage = CurseOfFragility.modifyDamage(entity, damage)
        }

        event.amount = damage
    }

    @SubscribeEvent
    fun onLivingHurt(event: LivingDamageEvent.Post) {
        CurseHandler.onHurt(event.entity, event.source, event.originalDamage)
        PotionHandler.poisonWeaponAttack(event.entity, event.source, event.originalDamage)
        BitingBeltItem.usePotion(event.entity, event.source, event.originalDamage)
    }

    @SubscribeEvent
    fun onAddReloadListener(event: AddReloadListenerEvent) {
        event.addListener(BloodPoolReloadListener.LOADER)
        event.addListener(ErosionReloadListener.LOADER)
        event.addListener(FetishEffectReloadListener.LOADER)
        event.addListener(InfiniteCenserReloadListener.LOADER)
        event.addListener(NaturePowerReloadListener.LOADER)
        event.addListener(PotionDataReloadListener.LOADER)
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        VampireSpecificEventHandler.tick(event.entity)
        AfflictionEventHandler.tick(event.entity)
        CurseHandler.tickCurse(event.entity)
        BarkBeltHandler.tick(event.entity)
        BloodPoolHandler.tick(event.entity)
        NightmareHandler.tick(event.entity)
        InfernalInfusionHandler.tick(event.entity)
        LightInfusionHandler.tick(event.entity)
        WerewolfSpecificEventHandler.tick(event.entity)
        OtherwhereInfusionHandler.tick(event.entity)
        TransformationHandler.tickBat(event.entity)
        TransformationHandler.tickWolf(event.entity)
        LichdomSpecificEventHandler.tick(event.entity)
        UnderWaterBreathPlayerAttachment.tick(event.entity)
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val oldData = AfflictionPlayerAttachment.getData(event.original)

        val newData = oldData.withAbilityIndex(-1)
        AfflictionPlayerAttachment.setData(event.entity, newData, sync = false)

        VampireSpecificEventHandler.respawn(event.original, event.entity, event.isWasDeath)

        InfusionPlayerAttachment.setPlayerInfusion(
            event.entity,
            InfusionPlayerAttachment.getPlayerInfusion(event.original)
        )

        InventoryLockPlayerAttachment.setData(event.entity, InventoryLockPlayerAttachment.getData(event.original))

        LichdomSpecificEventHandler.respawn(event.entity, event.original, event.isWasDeath)
        PhylacteryBlockEntity.onPlayerLoad(event.entity)
        BrewOfSleepingItem.respawnPlayer(event.entity)

        if (event.entity is ServerPlayer) {
            val serverPlayer = event.entity as ServerPlayer
            serverPlayer.server.execute {
                val currentData = AfflictionPlayerAttachment.getData(serverPlayer)
                AfflictionPlayerAttachment.syncFull(serverPlayer, currentData)

                if (currentData.getLevel(AfflictionTypes.VAMPIRISM) > 0) {
                    val bloodData = BloodPoolLivingEntityAttachment.getData(serverPlayer)
                    BloodPoolLivingEntityAttachment.setData(serverPlayer, bloodData)
                }
            }
        }
    }

    @SubscribeEvent
    private fun onLivingConversion(event: LivingConversionEvent.Post) {
        PossessedDataAttachment.onMobConverted(event.entity, event.outcome)
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.entity is ServerPlayer) {
            val player = event.entity
            val afflictionData = AfflictionPlayerAttachment.getData(player)
            AfflictionPlayerAttachment.setData(player, afflictionData, sync = true)

            if (afflictionData.getLevel(AfflictionTypes.VAMPIRISM) > 0) {
                val bloodData = BloodPoolLivingEntityAttachment.getData(player)
                BloodPoolLivingEntityAttachment.setData(player, bloodData)
            }

            InventoryLockPlayerAttachment.setData(event.entity, InventoryLockPlayerAttachment.getData(event.entity))
        }
        val player = event.entity
        if (player is ServerPlayer) {
            val data = TarotPlayerAttachment.getData(player)
            TarotPlayerAttachment.sync(player, data)
        }
    }


    @SubscribeEvent
    fun onInteractEntity(event: PlayerInteractEvent.EntityInteract) {
        AfflictionEventHandler.interactEntityWithAbility(event, event.entity, event.target)
        WineGlassItem.applyWineOnVillager(event, event.entity, event.target)
    }

    @SubscribeEvent
    fun onRightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
        AfflictionEventHandler.rightClickBlockAbility(event, event.entity, event.hand)
        LecternHandler.tryAccessGuidebook(event, event.entity, event.hand, event.pos)
        LichdomSpecificEventHandler.onBlockInteract(event, event.entity, event.hand, event.pos)
        BrazierBlockEntity.makeSoulCage(event, event.entity, event.pos)
        SacrificialBlockEntity.rightClick(event, event.entity, event.pos)
        MushroomLogBlock.makeMushroomLog(event, event.entity, event.pos)
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        InfusionHandler.leftClickBlock(event.entity, event.hand, event.pos)
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        CurseHandler.breakBlock(event.player.level(), event.state, event.player)
        EntSpawningHandler.breakBlock(event.player.level(), event.pos, event.state, event.player)
        AltarBlockEntity.onBlockBreak(event)
        InventoryLockPlayerAttachment.blockBreakEvent(event)
    }

    @SubscribeEvent
    fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) {
        CurseHandler.placeBlock(event.entity!!.level(), event.state, event.entity)
        AltarBlockEntity.onBlockPlace(event)
        RitualChalkBlock.placeInfernal(event, event.entity!!.level(), event.pos, event.state, event.entity)
        InventoryLockPlayerAttachment.blockPlaceEvent(event)
    }

    @SubscribeEvent
    fun onServerStarted(event: ServerStartedEvent) {
        WitcheryStructureInjects.addStructure(event.server)
    }

    @SubscribeEvent
    fun onLootTableLoad(event: LootTableLoadEvent) {
        WitcheryLootInjects.onLootTableLoad(event)
    }

    @SubscribeEvent
    fun registerEvents(event: RegisterCommandsEvent) {
        WitcheryCommands.register(event.dispatcher, event.buildContext, event.commandSelection)
    }

    @SubscribeEvent
    fun onAttack(event: AttackEntityEvent) {
        CurseHandler.attackEntity(event.entity, event.entity.level(), event.target)
        InfusionHandler.leftClickEntity(event.entity, event.target)
    }

    @SubscribeEvent
    fun onJoin(event: EntityJoinLevelEvent) {
        BloodPoolHandler.setBloodOnAdded(event.entity, event.entity.level())
    }

    @SubscribeEvent
    fun onEntityJoinLevel(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.entity is ServerPlayer) {
            val serverPlayer = event.entity as ServerPlayer
            val data = DeathQueueLevelAttachment.getData(serverPlayer.serverLevel())
            if (data.killerQueue.contains(serverPlayer.uuid)) {
                serverPlayer.kill()
                DeathQueueLevelAttachment.removeFromDeathQueue(serverPlayer.serverLevel(), serverPlayer.uuid)
            }
            AfflictionPlayerAttachment.syncFull(serverPlayer, AfflictionPlayerAttachment.getData(serverPlayer))
            BloodPoolLivingEntityAttachment.sync(serverPlayer, BloodPoolLivingEntityAttachment.getData(serverPlayer))
            TransformationPlayerAttachment.sync(serverPlayer, TransformationPlayerAttachment.getData(serverPlayer))
            InfusionPlayerAttachment.sync(serverPlayer, InfusionPlayerAttachment.getPlayerInfusion(serverPlayer))
            PhylacteryBlockEntity.onPlayerLoad(serverPlayer)
        }
    }

    @SubscribeEvent
    fun onSleep(event: SleepingEvent.Stop) {
        DreamWeaverHandler.onWake(event.player, event.sleepCounter, event.wakeImmediately)
    }

    @SubscribeEvent
    fun onLevelTick(event: LevelTickEvent.Pre) {
        MutandisHandler.tick(event.level)
        NecroHandler.processListExhaustion(event.level)
        NecroHandler.tick(event.level)
    }

    @SubscribeEvent
    fun onServerStop(event: ServerStoppingEvent) {
        TeleportQueueHandler.clearQueue(event.server)
    }

    @SubscribeEvent
    fun onLightningStruck(event: EntityStruckByLightningEvent) {
        InfernalInfusionHandler.strikeLightning(event.entity)
    }

    @SubscribeEvent
    fun onChain(event: ChainEvent.Discard) {
        BindSpectralCreaturesRitual.handleChainDiscard(event.entity)
        SoulCageBlockEntity.handleChainDiscard(event.entity)
    }

    @SubscribeEvent
    fun canPlayerSleepEvent(event: CanPlayerSleepEvent) {
        if (event.state.block is CoffinBlock) {
            if (event.level.isDay) {
                event.entity.setRespawnPosition(event.level.dimension(), event.pos, event.entity.yRot, false, true)
                event.problem = null
            } else {
                event.problem = Player.BedSleepingProblem.NOT_POSSIBLE_NOW
            }
        }
    }

    @SubscribeEvent
    fun canContinueSleepingEvent(event: CanContinueSleepingEvent) {
        val blockState = event.entity.sleepingPos
            .map { event.entity.level().getBlockState(it) }
            .orElse(null)

        if (blockState?.block is CoffinBlock) {
            if (event.entity.level().isDay) {
                event.setContinueSleeping(true)
            } else {
                event.setContinueSleeping(false)
            }
        }
    }

    @SubscribeEvent
    fun sleepFinishedTimeEvent(event: SleepFinishedTimeEvent) {
        val level = event.level
        if (level is ServerLevel) {
            val sleepingInCoffin = level.players().any { player ->
                player.sleepingPos
                    .map { level.getBlockState(it).block is CoffinBlock }
                    .orElse(false)
            }

            if (sleepingInCoffin && level.isDay) {
                val fullDays = level.dayTime / 24000L
                val newTime = (fullDays * 24000L) + 13000L
                event.setTimeAddition(newTime)

                triggerTarotEffectsForAllPlayers(level, isNightfall = true)
            } else {
                triggerTarotEffectsForAllPlayers(level, isNightfall = false)
            }
        }
    }

    private fun triggerTarotEffectsForAllPlayers(level: ServerLevel, isNightfall: Boolean) {
        for (player in level.players()) {
            if (player !is ServerPlayer) continue

            val data = TarotPlayerAttachment.getData(player)
            if (data.drawnCards.isEmpty()) continue

            for (i in data.drawnCards.indices) {
                val cardNumber = data.drawnCards[i]
                val isReversed = data.reversedCards.getOrNull(i) ?: false
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)

                if (isNightfall) {
                    effect?.onNightfall(player, isReversed)
                } else {
                    effect?.onMorning(player, isReversed)
                }
            }
        }
    }
}