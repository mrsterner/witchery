package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.sterner.witchery.api.event.ChainEvent
import dev.sterner.witchery.api.event.SleepingEvent
import dev.sterner.witchery.api.schedule.TickTaskScheduler
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.data.ErosionReloadListener
import dev.sterner.witchery.data.FetishEffectReloadListener
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import dev.sterner.witchery.data.NaturePowerReloadListener
import dev.sterner.witchery.data.PotionDataReloadListener
import dev.sterner.witchery.data_attachment.DeathQueueLevelAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.data_attachment.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.entity.BabaYagaEntity
import dev.sterner.witchery.entity.BansheeEntity
import dev.sterner.witchery.entity.CovenWitchEntity
import dev.sterner.witchery.entity.DeathEntity
import dev.sterner.witchery.entity.DemonEntity
import dev.sterner.witchery.entity.ElleEntity
import dev.sterner.witchery.entity.EntEntity
import dev.sterner.witchery.entity.HornedHuntsmanEntity
import dev.sterner.witchery.entity.ImpEntity
import dev.sterner.witchery.entity.InsanityEntity
import dev.sterner.witchery.entity.LilithEntity
import dev.sterner.witchery.entity.MandrakeEntity
import dev.sterner.witchery.entity.NightmareEntity
import dev.sterner.witchery.entity.OwlEntity
import dev.sterner.witchery.entity.ParasiticLouseEntity
import dev.sterner.witchery.entity.SpectreEntity
import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.handler.BarkBeltHandler
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.handler.CurseHandler
import dev.sterner.witchery.handler.DreamWeaverHandler
import dev.sterner.witchery.handler.EntSpawningHandler
import dev.sterner.witchery.handler.EquipmentHandler
import dev.sterner.witchery.handler.FamiliarHandler
import dev.sterner.witchery.handler.LecternHandler
import dev.sterner.witchery.handler.ManifestationHandler
import dev.sterner.witchery.handler.MutandisHandler
import dev.sterner.witchery.handler.NecroHandler
import dev.sterner.witchery.handler.NightmareHandler
import dev.sterner.witchery.handler.PotionHandler
import dev.sterner.witchery.handler.TeleportQueueHandler
import dev.sterner.witchery.handler.affliction.AfflictionAbilityHandler
import dev.sterner.witchery.handler.affliction.AfflictionEventHandler
import dev.sterner.witchery.handler.affliction.LichdomSpecificEventHandler
import dev.sterner.witchery.handler.affliction.TransformationHandler
import dev.sterner.witchery.handler.affliction.VampireChildrenHuntHandler
import dev.sterner.witchery.handler.affliction.VampireSpecificEventHandler
import dev.sterner.witchery.handler.affliction.WerewolfSpecificEventHandler
import dev.sterner.witchery.handler.infusion.InfernalInfusionHandler
import dev.sterner.witchery.handler.infusion.InfusionHandler
import dev.sterner.witchery.handler.infusion.LightInfusionHandler
import dev.sterner.witchery.handler.infusion.OtherwhereInfusionHandler
import dev.sterner.witchery.handler.poppet.PoppetHandler
import dev.sterner.witchery.item.accessories.BitingBeltItem
import dev.sterner.witchery.item.accessories.BitingBeltItem.Companion.usePotion
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryKeyMappings
import dev.sterner.witchery.ritual.BindSpectralCreaturesRitual
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.animal.Pig
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.slf4j.Logger

@Mod(Witchery.MODID)
class Witchery(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        modEventBus.addListener(::commonSetup)
        NeoForge.EVENT_BUS.register(this)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {

    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        NaturePowerReloadListener.addPending()
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        TickTaskScheduler.tick(event.server)
        EntSpawningHandler.serverTick(event.server)
        ManifestationHandler.tick(event.server)
        TeleportQueueHandler.processQueue(event.server)
        VampireChildrenHuntHandler.tickHuntAllLevels(event.server)
    }

    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        VampireSpecificEventHandler.resetNightCount(event.entity, event.source)
        VampireSpecificEventHandler.onKillEntity(event.entity, event.source)
        PoppetHandler.onLivingDeath(event, event.entity, event.source)
        NecroHandler.onDeath(event.entity, event.source)
        FamiliarHandler.familiarDeath(event.entity, event.source)

        WerewolfSpecificEventHandler.killEntity(event.entity, event.source)
        LichdomSpecificEventHandler.onDeath(event, event.entity, event.source)
        LichdomSpecificEventHandler.onKillEntity(event.entity, event.source)
    }

    @SubscribeEvent
    fun onLivingHurt(event: LivingIncomingDamageEvent){
        EquipmentHandler.babaYagaHit(event, event.entity, event.source, event.amount)
    }

    @SubscribeEvent
    fun onLivingHurt(event: LivingDamageEvent.Post){
        CurseHandler.onHurt(event.entity, event.source, event.originalDamage)
        PotionHandler.poisonWeaponAttack(event.entity, event.source, event.originalDamage)
        BitingBeltItem.usePotion(event.entity, event.source, event.originalDamage)
    }

    @SubscribeEvent
    fun onAddResourceListener(event: AddReloadListenerEvent){
        event.addListener(BloodPoolReloadListener.LOADER)
        event.addListener(ErosionReloadListener.LOADER)
        event.addListener(FetishEffectReloadListener.LOADER)
        event.addListener(InfiniteCenserReloadListener.LOADER)
        event.addListener(NaturePowerReloadListener.LOADER)
        event.addListener(PotionDataReloadListener.LOADER)
    }

    @SubscribeEvent
    fun onAddReloadListener(event: AddReloadListenerEvent) {
        event.addListener(NaturePowerReloadListener.LOADER)
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent) {
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
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        VampireSpecificEventHandler.respawn(event.original, event.entity, event.isWasDeath)
        AfflictionAbilityHandler.setAbilityIndex(event.entity, -1)
        InfusionPlayerAttachment.setPlayerInfusion(event.entity, InfusionPlayerAttachment.getPlayerInfusion(event.entity))
        LichdomSpecificEventHandler.respawn(event.entity, event.original, event.isWasDeath)
    }

    @SubscribeEvent
    fun onInteractEntity(event: PlayerInteractEvent.EntityInteract) {
        AfflictionEventHandler.interactEntityWithAbility(event, event.entity, event.target, event.hand)
    }

    @SubscribeEvent
    fun onRightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
        AfflictionEventHandler.rightClickBlockAbility(event, event.entity, event.hand, event.pos)
        LecternHandler.tryAccessGuidebook(event, event.entity, event.hand, event.pos)
        LichdomSpecificEventHandler.onBlockInteract(event, event.entity, event.hand, event.pos)
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        InfusionHandler.leftClickBlock(event.entity, event.hand, event.pos)
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        CurseHandler.breakBlock(event.player.level(), event.pos, event.state, event.player)
        EntSpawningHandler.breakBlock(event.player.level(), event.pos, event.state, event.player)
    }

    @SubscribeEvent
    fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) {
        CurseHandler.placeBlock(event.entity!!.level(), event.pos, event.state, event.entity)
    }

    @SubscribeEvent
    fun onAttack(event: AttackEntityEvent){
        CurseHandler.attackEntity(event.entity, event.entity.level(), event.target)
        InfusionHandler.leftClickEntity(event.entity, event.target)
    }

    @SubscribeEvent
    fun onJoin(event: EntityJoinLevelEvent){
        BloodPoolHandler.setBloodOnAdded(event.entity, event.entity.level())
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent){
        if (event.entity is ServerPlayer) {
            val serverPlayer = event.entity as ServerPlayer
            val data = DeathQueueLevelAttachment.getData(serverPlayer.serverLevel())
            if (data.killerQueue.contains(serverPlayer.uuid)) {
                serverPlayer.kill()
                DeathQueueLevelAttachment.removeFromDeathQueue(serverPlayer.serverLevel(), serverPlayer.uuid)
            }
            AfflictionPlayerAttachment.sync(serverPlayer, AfflictionPlayerAttachment.getData(serverPlayer))
            BloodPoolLivingEntityAttachment.sync(serverPlayer, BloodPoolLivingEntityAttachment.getData(serverPlayer))
            TransformationPlayerAttachment.sync(serverPlayer, TransformationPlayerAttachment.getData(serverPlayer))
            InfusionPlayerAttachment.sync(serverPlayer, InfusionPlayerAttachment.getPlayerInfusion(serverPlayer))
        }
    }

    @SubscribeEvent
    fun onSleep(event: SleepingEvent){
        DreamWeaverHandler.onWake(event.player, event.sleepCounter, event.wakeImmediately)
    }

    @SubscribeEvent
    fun onLevelTick(event: LevelTickEvent) {
        MutandisHandler.tick(event.level)
        NecroHandler.processListExhaustion(event.level)
        NecroHandler.tick(event.level)
    }

    @SubscribeEvent
    fun onServerStop(event: ServerStoppingEvent){
        TeleportQueueHandler.clearQueue(event.server)
    }

    @SubscribeEvent
    fun onLightningStruck(event: EntityStruckByLightningEvent){
        InfernalInfusionHandler.strikeLightning(event.entity)
    }

    @SubscribeEvent
    fun onRegisterKey(event: RegisterKeyMappingsEvent){
        event.register(WitcheryKeyMappings.BROOM_DISMOUNT_KEYMAPPING)
        event.register(WitcheryKeyMappings.OPEN_ABILITY_SELECTION)
    }

    @SubscribeEvent
    fun onChain(event: ChainEvent) {
        BindSpectralCreaturesRitual.handleChainDiscard(event.entity)
    }

    @SubscribeEvent
    fun onEntityAttributeCreation(event: EntityAttributeCreationEvent) {
        event.put(WitcheryEntityTypes.MANDRAKE.get(), MandrakeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.IMP.get(), ImpEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.DEMON.get(), DemonEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.OWL.get(), OwlEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.ENT.get(), EntEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.BANSHEE.get(), BansheeEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.SPECTRE.get(), SpectreEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.SPECTRAL_PIG.get(), Pig.createAttributes().build())
        event.put(WitcheryEntityTypes.NIGHTMARE.get(), NightmareEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.VAMPIRE.get(), VampireEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.WEREWOLF.get(), WerewolfEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.LILITH.get(), LilithEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.COVEN_WITCH.get(), CovenWitchEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.ELLE.get(), ElleEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.PARASITIC_LOUSE.get(), ParasiticLouseEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.INSANITY.get(), InsanityEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.BABA_YAGA.get(), BabaYagaEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.DEATH.get(), DeathEntity.createAttributes().build())
        event.put(WitcheryEntityTypes.HORNED_HUNTSMAN.get(), HornedHuntsmanEntity.createAttributes().build())
    }

    companion object {
        const val MODID: String = "witchery"
        val LOGGER: Logger = LogUtils.getLogger()

        fun id(path: String) = ResourceLocation.fromNamespaceAndPath(MODID, path)
    }
}

