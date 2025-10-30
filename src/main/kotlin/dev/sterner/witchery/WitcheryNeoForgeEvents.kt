package dev.sterner.witchery

import dev.sterner.witchery.core.api.event.ChainEvent
import dev.sterner.witchery.core.api.event.SleepingEvent
import dev.sterner.witchery.core.api.schedule.TickTaskScheduler
import dev.sterner.witchery.content.block.altar.AltarBlockEntity
import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.content.block.coffin.CoffinBlock
import dev.sterner.witchery.content.block.mushroom_log.MushroomLogBlock
import dev.sterner.witchery.content.block.phylactery.PhylacteryBlockEntity
import dev.sterner.witchery.content.block.ritual.RitualChalkBlock
import dev.sterner.witchery.content.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.content.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.content.entity.CovenWitchEntity
import dev.sterner.witchery.content.item.CaneSwordItem
import dev.sterner.witchery.content.item.WineGlassItem
import dev.sterner.witchery.content.item.curios.BitingBeltItem
import dev.sterner.witchery.content.item.brew.BrewOfSleepingItem
import dev.sterner.witchery.core.data.AltarAugmentReloadListener
import dev.sterner.witchery.core.data.BloodPoolReloadListener
import dev.sterner.witchery.core.data.ErosionReloadListener
import dev.sterner.witchery.core.data.FetishEffectReloadListener
import dev.sterner.witchery.core.data.InfiniteCenserReloadListener
import dev.sterner.witchery.core.data.NaturePowerReloadListener
import dev.sterner.witchery.core.data.PotionDataReloadListener
import dev.sterner.witchery.features.misc.BindingRitualAttachment
import dev.sterner.witchery.features.blood.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.features.misc.DeathQueueLevelAttachment
import dev.sterner.witchery.features.misc.InventoryLockPlayerAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment
import dev.sterner.witchery.features.tarot.TarotPlayerAttachment
import dev.sterner.witchery.features.misc.UnderWaterBreathPlayerAttachment
import dev.sterner.witchery.core.registry.WitcheryCommands
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryLootInjects
import dev.sterner.witchery.core.registry.WitcherySpecialPotionEffects
import dev.sterner.witchery.core.registry.WitcheryStructureInjects
import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import dev.sterner.witchery.core.registry.WitcheryVillagers
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.curse.CurseOfFragility
import dev.sterner.witchery.features.affliction.event.AfflictionEventHandler
import dev.sterner.witchery.features.affliction.event.AfflictionHandler
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.TransformationPlayerAttachment
import dev.sterner.witchery.features.affliction.event.TransformationHandler
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.features.affliction.vampire.VampireChildrenHuntHandler
import dev.sterner.witchery.features.affliction.vampire.VampireSpecificEventHandler
import dev.sterner.witchery.features.affliction.werewolf.WerewolfSpecificEventHandler
import dev.sterner.witchery.features.curse.CurseHandler
import dev.sterner.witchery.features.familiar.FamiliarHandler
import dev.sterner.witchery.features.infusion.InfernalInfusionHandler
import dev.sterner.witchery.features.infusion.InfusionHandler
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.LightInfusionHandler
import dev.sterner.witchery.features.infusion.OtherwhereInfusionHandler
import dev.sterner.witchery.features.bark_belt.BarkBeltHandler
import dev.sterner.witchery.features.blood.BloodPoolHandler
import dev.sterner.witchery.features.coven.CovenDialogue
import dev.sterner.witchery.features.coven.CovenPlayerAttachment
import dev.sterner.witchery.features.death.DeathEquipmentEventHandler
import dev.sterner.witchery.features.death.DeathPlayerAttachment
import dev.sterner.witchery.features.misc.DreamWeaverHandler
import dev.sterner.witchery.features.ent.EntSpawningHandler
import dev.sterner.witchery.features.hags_ring.VeinMiningTracker
import dev.sterner.witchery.features.lifeblood.LifebloodHandler
import dev.sterner.witchery.features.lifeblood.LifebloodPlayerAttachment
import dev.sterner.witchery.features.misc.EquipmentHandler
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import dev.sterner.witchery.features.misc.LecternHandler
import dev.sterner.witchery.features.misc.MiscPlayerAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationHandler
import dev.sterner.witchery.features.mutandis.MutandisHandler
import dev.sterner.witchery.features.necromancy.NecroHandler
import dev.sterner.witchery.features.misc.PotionHandler
import dev.sterner.witchery.features.misc.TeleportQueueHandler
import dev.sterner.witchery.features.nightmare.NightmareHandler
import dev.sterner.witchery.features.poppet.PoppetHandler
import dev.sterner.witchery.features.poppet.VoodooPoppetLivingEntityAttachment
import dev.sterner.witchery.features.possession.PossessedDataAttachment
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import dev.sterner.witchery.features.ritual.BindSpectralCreaturesRitual
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.item.trading.ItemCost
import net.minecraft.world.item.trading.MerchantOffer
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent
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
import net.neoforged.neoforge.event.village.VillagerTradesEvent

object WitcheryNeoForgeEvents {

    @SubscribeEvent
    fun addFortuneTellerTrades(event: VillagerTradesEvent) {
        if (event.type == WitcheryVillagers.FORTUNE_TELLER_PROFESSION.get()) {
            val villagerTraders = event.trades

            // Novice (Level 1)
            villagerTraders[1]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.GYPSUM.get(), 2, 8, 16, 2)
            )
            villagerTraders[1]?.add(
                VillagerTrades.EmeraldForItems(WitcheryItems.QUARTZ_SPHERE.get(), 1, 16, 2)
            )
            villagerTraders[1]?.add(
                VillagerTrades.ItemsForEmeralds(Items.CANDLE, 1, 4, 16, 1)
            )

            // Apprentice (Level 2)
            villagerTraders[2]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.BONE_NEEDLE.get(), 4, 1, 10, 5)
            )
            villagerTraders[2]?.add(
                VillagerTrades.EmeraldForItems(Items.AMETHYST_SHARD, 4, 12, 10)
            )

            // Journeyman (Level 3)
            villagerTraders[3]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.DREAM_WEAVER.get(), 8, 1, 8, 10)
            )
            villagerTraders[3]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.GOLDEN_THREAD.get(), 6, 2, 8, 10)
            )
            villagerTraders[3]?.add(
                VillagerTrades.EmeraldForItems(Items.LAPIS_LAZULI, 8, 12, 20)
            )

            // Expert (Level 4)
            villagerTraders[4]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.HAPPENSTANCE_OIL.get(), 10, 1, 5, 15)
            )
            villagerTraders[4]?.add(
                VillagerTrades.ItemsForEmeralds(WitcheryItems.ATTUNED_STONE.get(), 12, 1, 5, 15)
            )
            villagerTraders[4]?.add(
                VillagerTrades.ItemsForEmeralds(Items.ECHO_SHARD, 16, 1, 3, 20)
            )

            // Master (Level 5)
            villagerTraders[5]?.add { entity, random ->
                MerchantOffer(
                    ItemCost(Items.EMERALD, 24),
                    WitcheryItems.TAROT_DECK.get().defaultInstance,
                    1,
                    3,
                    0.2f
                )
            }
        }
    }

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
        BindingRitualAttachment.tick(entity)
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

            if (wereData.getWerewolfLevel() > 0) {
                if (TransformationHandler.isWolf(attacker) || TransformationHandler.isWerewolf(attacker)) {
                    damage = WerewolfSpecificEventHandler.modifyWerewolfDamage(
                        attacker, entity, damage
                    )
                }
            }
        }

        val isVamp =
            entity is Player && AfflictionPlayerAttachment.getData(entity).getVampireLevel() > 0
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

        if (damage > 0f && entity is Player) {
            damage = LifebloodHandler.handleDamage(entity, damageSource, damage)
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
        event.addListener(AltarAugmentReloadListener.LOADER)
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
        val player = event.entity
        if (!player.level().isClientSide) {
            LifebloodHandler.tick(player)
        }
        if (player is ServerPlayer) {
            VeinMiningTracker.tick(player)
        }
        DeathEquipmentEventHandler.onPlayerTick(event)
    }

    @SubscribeEvent
    fun onEquipmentChange(event: LivingEquipmentChangeEvent){
        DeathEquipmentEventHandler.onEquipmentChange(event)
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        if (event.entity is ServerPlayer) {
            val miscData = MiscPlayerAttachment.getData(event.entity)
            MiscPlayerAttachment.setData(event.entity, miscData.copy(hasDeathTeleport = false))
        }
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val oldData = AfflictionPlayerAttachment.getData(event.original)

        val newData = oldData.withAbilityIndex(-1)
        AfflictionPlayerAttachment.setData(event.entity, newData, sync = false)

        VampireSpecificEventHandler.respawn(event.original, event.entity, event.isWasDeath)

        InfusionPlayerAttachment.setData(
            event.entity,
            InfusionPlayerAttachment.getData(event.original)
        )

        InventoryLockPlayerAttachment.setData(event.entity, InventoryLockPlayerAttachment.getData(event.original))

        val miscData = MiscPlayerAttachment.getData(event.entity)
        MiscPlayerAttachment.setData(event.entity, miscData.copy(hasDeathTeleport = false))

        DeathPlayerAttachment.setData(event.entity, DeathPlayerAttachment.getData(event.original))
        LichdomSpecificEventHandler.respawn(event.entity, event.original, event.isWasDeath)
        PhylacteryBlockEntity.onPlayerLoad(event.entity)
        BrewOfSleepingItem.respawnPlayer(event.entity)
        CovenPlayerAttachment.setData(event.entity, CovenPlayerAttachment.getData(event.original))
        LifebloodPlayerAttachment.setData(event.entity, LifebloodPlayerAttachment.getData(event.original))

        if (event.entity is ServerPlayer) {
            val serverPlayer = event.entity as ServerPlayer
            serverPlayer.server.execute {
                val currentData = AfflictionPlayerAttachment.getData(serverPlayer)
                AfflictionPlayerAttachment.syncFull(serverPlayer, currentData)

                if (currentData.getVampireLevel() > 0) {
                    val bloodData = BloodPoolLivingEntityAttachment.getData(serverPlayer)
                    BloodPoolLivingEntityAttachment.setData(serverPlayer, bloodData)
                }
            }
        }
    }

    @SubscribeEvent
    private fun finalizeMobSpawn(event: FinalizeSpawnEvent){
        val entity = event.entity
        if (entity is CovenWitchEntity) {
            if (entity.customName == null) {
                entity.customName = CovenDialogue.generateName(event.level.random)
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

            if (afflictionData.getVampireLevel() > 0) {
                val bloodData = BloodPoolLivingEntityAttachment.getData(player)
                BloodPoolLivingEntityAttachment.setData(player, bloodData)
            }
            HudPlayerAttachment.sync(player as ServerPlayer, HudPlayerAttachment.getData(player))
            DeathPlayerAttachment.setData(player, DeathPlayerAttachment.getData(player))
            InventoryLockPlayerAttachment.setData(event.entity, InventoryLockPlayerAttachment.getData(event.entity))
            LifebloodPlayerAttachment.setData(event.entity, LifebloodPlayerAttachment.getData(event.entity))
            TarotPlayerAttachment.sync(player, TarotPlayerAttachment.getData(player))
        }
    }


    @SubscribeEvent
    fun onInteractEntity(event: PlayerInteractEvent.EntityInteract) {
        AfflictionEventHandler.interactEntityWithAbility(event, event.entity, event.target)
        WineGlassItem.applyWineOnVillager(event, event.entity, event.target)
    }

    @SubscribeEvent
    fun onRightClickItem(event: PlayerInteractEvent.RightClickItem) {
        AfflictionEventHandler.rightClickItem(event, event.entity, event.hand)
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
        AfflictionEventHandler.leftClickBlock(event, event.entity)
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        CurseHandler.breakBlock(event.player.level(), event.state, event.player)
        EntSpawningHandler.breakBlock(event.player.level(), event.pos, event.state, event.player)
        AltarBlockEntity.onBlockBreak(event)
        InventoryLockPlayerAttachment.blockBreakEvent(event)
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            VeinMiningTracker.cancelVeinMining(player)
        }
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            VeinMiningTracker.cancelVeinMining(player)
        }
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
            InfusionPlayerAttachment.sync(serverPlayer, InfusionPlayerAttachment.getData(serverPlayer))
            LifebloodPlayerAttachment.sync(serverPlayer, LifebloodPlayerAttachment.getData(serverPlayer))
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