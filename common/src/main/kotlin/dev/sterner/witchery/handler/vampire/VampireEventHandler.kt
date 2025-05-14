package dev.sterner.witchery.handler.vampire

import com.mojang.blaze3d.platform.ScreenManager.clamp
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.event.VampireEvent
import dev.sterner.witchery.api.interfaces.VillagerTransfix
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlock
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.handler.ability.VampireAbility
import dev.sterner.witchery.handler.transformation.TransformationHandler
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import dev.sterner.witchery.payload.SpawnBloodParticlesS2CPayload
import dev.sterner.witchery.payload.VampireAbilityUseC2SPayload
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment.setData
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Chicken
import net.minecraft.world.entity.monster.Blaze
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks
import kotlin.math.ceil

/**
 * Main handler for vampire-related events and mechanics
 */
object VampireEventHandler {

    private const val BLOOD_TRANSFER_AMOUNT_BASE = 10
    private const val BLOOD_HEALING_THRESHOLD = 75
    private const val DAMAGE_DISTRIBUTION = 0.75f
    private const val BLOOD_HEALING_AMOUNT = 1f
    private const val BLOOD_DRAIN_TICK_RATE = 20
    private const val SUN_DAMAGE_AMOUNT = 2f
    private const val HUMAN_BLOOD_REGEN_RATE = 1000
    private const val HUMAN_BLOOD_REGEN_AMOUNT = 10
    private const val POISON_CHANCE_BAD_BLOOD = 0.15f
    private const val POISON_EFFECT_DURATION = 200
    private const val VILLAGER_DAMAGE_AMOUNT = 2f
    private const val RESPAWN_BLOOD_AMOUNT = 900
    private const val RESPAWN_FOOD_LEVEL = 10
    private const val CAGE_DETECTION_RANGE = 2.0
    private const val MIN_CAGE_BARS = 24
    private const val NIGHT_VISION_DURATION = 20 * 2
    private const val SPEED_BOOST_DURATION = 20 * 2

    private val overlay = Witchery.id("textures/gui/ability_hotbar_selection.png")
    private val sun = Witchery.id("textures/gui/vampire_abilities/sun_")

    /**
     * When the vampire-player gets hurt, this handles damage distribution between hearts and blood.
     * The current distribution is declared by damageDistribution
     * If the player is level 0, aka a human, this will just return the default value
     */
    fun handleHurt(player: LivingEntity, damageSource: DamageSource, original: Float): Float {
        if (player !is Player) return original

        val vampData = getData(player)

        if (vampData.getVampireLevel() < 1) return original

        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (bloodData.bloodPool > 0) {
            val bloodPerHealthPoint = BLOOD_HEALING_THRESHOLD
            val maxBloodAbsorbableDamage = bloodData.bloodPool / bloodPerHealthPoint

            val absorbableDamage = minOf(original * DAMAGE_DISTRIBUTION, maxBloodAbsorbableDamage.toFloat())
            val bloodRequired = (absorbableDamage * bloodPerHealthPoint).toInt()

            BloodPoolLivingEntityAttachment.setData(
                player,
                bloodData.copy(bloodPool = bloodData.bloodPool - bloodRequired)
            )

            return original * (1 - DAMAGE_DISTRIBUTION)
        }

        return original
    }

    /**
     * Tick human players blood up naturally and handle vampire mechanics.
     */
    fun tick(player: Player?) {
        if (player !is ServerPlayer) return

        val vampData = getData(player)
        if (vampData.getVampireLevel() < 1) {
            regenerateHumanBlood(player)
            return
        }

        if (player.isAlive) {
            vampireTick(player, vampData)
        }
    }

    /**
     * Regenerates blood for human players
     */
    private fun regenerateHumanBlood(player: ServerPlayer) {
        val humanBloodData = BloodPoolLivingEntityAttachment.getData(player)
        if (humanBloodData.bloodPool < humanBloodData.maxBlood) {
            if (player.tickCount % HUMAN_BLOOD_REGEN_RATE == 0) {
                BloodPoolHandler.increaseBlood(player, HUMAN_BLOOD_REGEN_AMOUNT)
            }
        }
    }

    /**
     * The main tick for vampires, handles sun exposure, blood healing and ability effects.
     */
    private fun vampireTick(player: ServerPlayer, vampData: VampirePlayerAttachment.Data) {
        handleSunExposure(player, vampData)

        handleBloodHealing(player)

        applyActiveEffects(player)
    }

    /**
     * Handles sun exposure and damage for vampires
     */
    private fun handleSunExposure(player: ServerPlayer, vampData: VampirePlayerAttachment.Data) {
        val isInSunlight = player.level().canSeeSky(player.blockPosition()) && player.level().isDay

        if (isInSunlight && !player.isCreative && !player.isSpectator) {
            VampireAbilityHandler.increaseInSunTick(player)

            val maxInSunTicks = player.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value ?: 0.0
            val data = getData(player)
            setData(player, data.copy(maxInSunTickClient = maxInSunTicks.toInt()))

            if (getData(player).inSunTick >= maxInSunTicks) {
                handleSunDamage(player, vampData)
            }
        } else {
            decreaseSunTick(player)
        }
    }

    /**
     * Applies sun damage based on vampire level and blood pool
     */
    private fun handleSunDamage(player: ServerPlayer, vampData: VampirePlayerAttachment.Data) {
        val sunDamageSource = (player.level().damageSources() as DamageSourcesInvoker)
            .invokeSource(WitcheryDamageSources.IN_SUN)
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (vampData.getVampireLevel() < 5) {
            if (VampireEvent.ON_SUN_DAMAGE.invoker().invoke(player) != EventResult.interruptFalse()) {
                player.hurt(sunDamageSource, Float.MAX_VALUE)
            }
        } else if (bloodData.bloodPool >= BLOOD_HEALING_THRESHOLD) {
            if (player.tickCount % BLOOD_DRAIN_TICK_RATE == 0) {
                if (VampireEvent.ON_SUN_DAMAGE.invoker().invoke(player) != EventResult.interruptFalse()) {
                    player.hurt(sunDamageSource, SUN_DAMAGE_AMOUNT)
                    playSunDamageEffects(player)
                }
            }
        } else {
            if (VampireEvent.ON_SUN_DAMAGE.invoker().invoke(player) != EventResult.interruptFalse()) {
                player.hurt(sunDamageSource, Float.MAX_VALUE)
            }
        }
    }

    /**
     * Plays sun damage visual and sound effects
     */
    private fun playSunDamageEffects(player: ServerPlayer) {
        player.level().playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.FIRE_EXTINGUISH,
            SoundSource.PLAYERS,
            0.5f,
            1.0f
        )
        WitcheryPayloads.sendToPlayers(
            player.level(),
            SpawnBloodParticlesS2CPayload(player, player.position().add(0.5, 0.5, 0.5))
        )
    }

    /**
     * Decreases sun tick counter when not in sunlight
     */
    private fun decreaseSunTick(player: ServerPlayer) {
        val decreaseAmount = (player.getAttribute(WitcheryAttributes.VAMPIRE_SUN_RESISTANCE)?.value?.div(100)) ?: 1.0
        VampireAbilityHandler.decreaseInSunTick(player, (ceil(decreaseAmount)).toInt())
    }

    /**
     * Handles blood-based healing for vampires
     */
    private fun handleBloodHealing(player: ServerPlayer) {
        val bloodData = BloodPoolLivingEntityAttachment.getData(player)

        if (bloodData.bloodPool >= BLOOD_HEALING_THRESHOLD && player.level().random.nextBoolean()) {
            if (player.health < player.maxHealth && player.health > 0) {
                BloodPoolHandler.decreaseBlood(player, BLOOD_HEALING_THRESHOLD)
                player.heal(BLOOD_HEALING_AMOUNT)
            }
        }
    }

    /**
     * Applies active vampire ability effects
     */
    private fun applyActiveEffects(player: ServerPlayer) {
        val playerData = getData(player)

        if (playerData.isNightVisionActive) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION))
        }

        if (playerData.isSpeedBoostActive) {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, SPEED_BOOST_DURATION))
        }
    }

    /**
     * Entity interaction handler for vampire abilities
     */
    @JvmStatic
    fun interactEntityWithAbility(player: Player?, entity: Entity?): EventResult? {
        if (player !is ServerPlayer || entity !is LivingEntity) {
            return EventResult.pass()
        }

        val playerData = getData(player)
        val playerBloodData = BloodPoolLivingEntityAttachment.getData(player)

        return when (playerData.abilityIndex) {
            VampireAbility.DRINK_BLOOD.ordinal -> vampireDrinkBloodAbility(player, entity, playerBloodData)
            VampireAbility.TRANSFIX.ordinal -> vampireTransfixAbility(player, entity)
            else -> EventResult.pass()
        }
    }

    /**
     * Handles the vampire transfix ability for mesmerizing villagers
     */
    private fun vampireTransfixAbility(player: ServerPlayer, entity: LivingEntity): EventResult? {
        if (entity is Villager) {
            val transfixVillager = entity as VillagerTransfix
            transfixVillager.setTransfixedLookVector(player.eyePosition)

            if (getData(player).getVampireLevel() >= 8) {
                transfixVillager.`witchery$setMesmerized`(player.uuid)
            }
            return EventResult.interruptFalse()
        }
        return EventResult.pass()
    }

    /**
     * Handles the vampire blood drinking ability
     */
    private fun vampireDrinkBloodAbility(
        player: ServerPlayer,
        entity: LivingEntity,
        playerBloodData: BloodPoolLivingEntityAttachment.Data
    ): EventResult? {
        val targetData = BloodPoolLivingEntityAttachment.getData(entity)
        val quality = BloodPoolReloadListener.BLOOD_PAIR[entity.type] ?: 0

        if (playerBloodData.bloodPool >= playerBloodData.maxBlood ||
            targetData.bloodPool < 0 ||
            targetData.maxBlood <= 0
        ) {
            return EventResult.interruptFalse()
        }

        val eventResult = VampireEvent.ON_BLOOD_DRINK.invoker().invoke(player, entity)
        if (eventResult == EventResult.interruptFalse()) {
            return EventResult.interruptFalse()
        }

        if (quality == 0 && player.level().random.nextFloat() < POISON_CHANCE_BAD_BLOOD) {
            player.addEffect(MobEffectInstance(MobEffects.POISON, POISON_EFFECT_DURATION, 0))
        }

        val targetHalfBlood = targetData.maxBlood / 2

        if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown && entity is Villager) {
            handleVillagerHalfBlood(player, entity)
        }

        if (targetData.bloodPool <= targetHalfBlood && !player.isShiftKeyDown) {
            return EventResult.pass()
        }

        playBloodDrinkingEffects(player, entity)

        transferBlood(player, entity)

        handleTargetDamage(player, entity, targetData)

        return EventResult.interruptFalse()
    }

    /**
     * Handles special vampire leveling logic for half-drained villagers
     */
    private fun handleVillagerHalfBlood(player: ServerPlayer, villager: Villager) {
        VampireLeveling.increaseVillagersHalfBlood(player, villager)

        val cageStates = villager.level().getBlockStates(
            villager.boundingBox.inflate(CAGE_DETECTION_RANGE, CAGE_DETECTION_RANGE, CAGE_DETECTION_RANGE)
        )
        val bars = cageStates.filter {
            it.`is`(Blocks.IRON_BARS) || it.`is`(BlockTags.SLABS)
        }.count()

        if (bars >= MIN_CAGE_BARS) {
            VampireLeveling.increaseTrappedVillagers(player, villager)
        }
    }

    /**
     * Plays blood drinking visual and sound effects
     */
    private fun playBloodDrinkingEffects(player: ServerPlayer, entity: LivingEntity) {
        player.level().playSound(
            null,
            entity.x,
            entity.y,
            entity.z,
            SoundEvents.HONEY_DRINK,
            SoundSource.PLAYERS
        )

        val particlePosition = entity.position().add(0.5, 0.5, 0.5)
        WitcheryPayloads.sendToPlayers(
            player.level(),
            SpawnBloodParticlesS2CPayload(player, particlePosition)
        )
    }

    /**
     * Transfers blood between entity and player
     */
    private fun transferBlood(player: ServerPlayer, entity: LivingEntity) {
        val attribute = player.getAttribute(WitcheryAttributes.VAMPIRE_DRINK_SPEED)?.value?.toInt() ?: 0
        val modifiedAmount = BLOOD_TRANSFER_AMOUNT_BASE + attribute

        BloodPoolHandler.decreaseBlood(entity, modifiedAmount)
        BloodPoolHandler.increaseBlood(player, modifiedAmount)
    }

    /**
     * Handles damage to blood-drained entities
     */
    private fun handleTargetDamage(
        player: ServerPlayer,
        entity: LivingEntity,
        targetData: BloodPoolLivingEntityAttachment.Data
    ) {
        val targetHalfBlood = targetData.maxBlood / 2

        val shouldHurt = when {
            entity is Villager && entity is VillagerTransfix &&
                    !entity.isSleeping && !entity.`witchery$isTransfixed`() -> true

            targetData.bloodPool < targetHalfBlood -> true
            else -> false
        }

        if (shouldHurt) {
            if (entity is Villager && getData(player).villagersHalfBlood.contains(entity.uuid)) {
                VampireLeveling.removeVillagerHalfBlood(player, entity)
                VampireLeveling.removeTrappedVillager(player, entity)
            }
            entity.hurt(player.damageSources().playerAttack(player), VILLAGER_DAMAGE_AMOUNT)
        }

        if (targetData.bloodPool <= 0) {
            if (entity is Player) {
                entity.hurt(player.damageSources().playerAttack(player), VILLAGER_DAMAGE_AMOUNT)
            } else {
                entity.kill()
            }
        }
    }

    /**
     * Renders the vampire HUD elements
     */
    @JvmStatic
    fun renderHud(guiGraphics: GuiGraphics) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return

        val isNotVamp = getData(player).getVampireLevel() <= 0
        if (isNotVamp) return

        val canHurtPlayer = client.gameMode!!.canHurtPlayer()
        if (!canHurtPlayer) return

        val hasOffhand = !player.offhandItem.isEmpty
        val y = guiGraphics.guiHeight() - 18 - 5
        val x = guiGraphics.guiWidth() / 2 - 36 - 18 * 4 - 5 - if (hasOffhand) 32 else 0

        drawBloodSense(guiGraphics)
        drawSun(guiGraphics, player)
        drawBatFormHud(guiGraphics, player)
        drawAbilityBar(guiGraphics, player, x, y)
    }

    /**
     * Draws the vampire ability bar
     */
    private fun drawAbilityBar(guiGraphics: GuiGraphics, player: Player, x: Int, y: Int) {
        val abilityIndex = getData(player).abilityIndex
        val abilities = VampireAbilityHandler.getAbilities(player)
        val isShiftDown = player.isShiftKeyDown
        val batCooldown = TransformationPlayerAttachment.getData(player).batFormCooldown

        for (i in abilities.indices) {
            var name = abilities[i].id
            if (abilities[i] == VampireAbility.TRANSFIX && isShiftDown) {
                name = "night_vision"
            }

            val iconX = x - (25 * i) + 4
            val iconY = y + 4

            guiGraphics.blit(
                Witchery.id("textures/gui/vampire_abilities/${name}.png"),
                iconX, iconY,
                16, 16,
                0f, 0f, 16, 16,
                16, 16
            )

            if (abilities[i] == VampireAbility.BAT_FORM && batCooldown > 0) {
                drawCooldownOverlay(guiGraphics, iconX, iconY, batCooldown)
            }
        }

        if (abilityIndex != -1) {
            guiGraphics.blit(overlay, x - (25 * abilityIndex), y, 24, 23, 0f, 0f, 24, 23, 24, 23)
        }
    }

    /**
     * Draws cooldown overlay for abilities
     */
    private fun drawCooldownOverlay(guiGraphics: GuiGraphics, iconX: Int, iconY: Int, cooldown: Int) {
        val cooldownPercent = cooldown.toFloat() / TransformationHandler.MAX_COOLDOWN
        val fillStart = iconY + Mth.floor(16f * (1.0f - cooldownPercent))
        val fillEnd = fillStart + Mth.ceil(16f * cooldownPercent)

        guiGraphics.fill(
            RenderType.guiOverlay(),
            iconX,
            fillStart,
            iconX + 16,
            fillEnd,
            0xAA000000.toInt()
        )
    }

    /**
     * Draws the bat form HUD when player is transformed
     */
    private fun drawBatFormHud(guiGraphics: GuiGraphics, player: LocalPlayer) {
        if (TransformationHandler.isBat(player)) {
            val maxTicks = TransformationPlayerAttachment.getData(player).maxBatTimeClient
            val currentTicks = maxTicks - TransformationPlayerAttachment.getData(player).batFormTicker
            val hasArmor = player.armorValue > 0
            val y = guiGraphics.guiHeight() - 36 - 10 - if (hasArmor) 10 else 0
            val x = guiGraphics.guiWidth() / 2 - 18 * 4 - 11
            RenderUtils.innerRenderBat(guiGraphics, maxTicks, currentTicks, y, x)
        }
    }

    /**
     * Draws the sun-exposure HUD
     */
    private fun drawSun(guiGraphics: GuiGraphics, player: Player) {
        val y = guiGraphics.guiHeight() - 36 - 18 - 2
        val x = guiGraphics.guiWidth() / 2 - 8
        val raw = getData(player).inSunTick
        val maxSunTicks = getData(player).maxInSunTickClient

        val sunTick = if (maxSunTicks > 0) {
            clamp((raw.toFloat() / maxSunTicks * 4).toInt(), 0, 4)
        } else {
            0
        }

        if (raw > 1) {
            RenderUtils.blitWithAlpha(
                guiGraphics.pose(),
                sun.withSuffix("${sunTick}.png"),
                x,
                y,
                0f,
                0f,
                16,
                16,
                16,
                16
            )
        }
    }

    /**
     * Draws the crosshair entity's blood pool indicator
     */
    private fun drawBloodSense(guiGraphics: GuiGraphics) {
        val x = guiGraphics.guiWidth() / 2 + 13
        val y = guiGraphics.guiHeight() / 2 + 9
        val target = Minecraft.getInstance().crosshairPickEntity

        if (target is LivingEntity && BloodPoolLivingEntityAttachment.getData(target).maxBlood > 0) {
            RenderUtils.innerRenderBlood(guiGraphics, target, y, x)
        }
    }

    /**
     * Handles player respawn by resetting sun exposure and giving initial blood
     */
    private fun respawn(oldPlayer: ServerPlayer, newPlayer: ServerPlayer) {
        if (getData(oldPlayer).getVampireLevel() > 0) {
            val oldBloodData = BloodPoolLivingEntityAttachment.getData(oldPlayer)

            newPlayer.foodData.foodLevel = RESPAWN_FOOD_LEVEL

            BloodPoolLivingEntityAttachment.setData(
                newPlayer,
                BloodPoolLivingEntityAttachment.Data(oldBloodData.maxBlood, RESPAWN_BLOOD_AMOUNT)
            )

            val data = getData(oldPlayer).copy(inSunTick = 0)
            setData(newPlayer, data)
        }
    }

    /**
     * Handles ability activation with no block interaction
     */
    fun clientRightClickAbility(player: Player?, interactionHand: InteractionHand?): Boolean {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) return false

        val playerData = getData(player)
        if (playerData.abilityIndex != -1) {
            NetworkManager.sendToServer(VampireAbilityUseC2SPayload(playerData.abilityIndex))
            return true
        }
        return false
    }

    /**
     * Handles ability activation with block interaction
     */
    private fun rightClickBlockAbility(
        player: Player?,
        interactionHand: InteractionHand?
    ): EventResult? {
        if (player == null || interactionHand == InteractionHand.OFF_HAND) {
            return EventResult.pass()
        }

        val playerData = getData(player)
        if (parseAbilityFromIndex(player, playerData.abilityIndex)) {
            return EventResult.interruptTrue()
        }

        return EventResult.pass()
    }

    /**
     * Parses and activates abilities based on index
     */
    fun parseAbilityFromIndex(player: Player, abilityIndex: Int): Boolean {
        return when {

            abilityIndex == VampireAbility.TRANSFIX.ordinal && player.isShiftKeyDown -> {
                VampireAbilityHandler.toggleNightVision(player)
                true
            }

            abilityIndex == VampireAbility.SPEED.ordinal -> {
                VampireAbilityHandler.toggleSpeedBoost(player)
                true
            }

            abilityIndex == VampireAbility.BAT_FORM.ordinal -> {
                val isBat = TransformationHandler.isBat(player)
                if (isBat) {
                    TransformationHandler.removeForm(player)
                } else {
                    TransformationHandler.setBatForm(player)
                }
                true
            }

            else -> false
        }
    }

    /**
     * Creates sacrificial circle when a chicken is killed
     */
    private fun killChicken(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity !is Chicken || damageSource?.entity !is Player) {
            return EventResult.pass()
        }

        val player = damageSource.entity as Player

        if (!player.mainHandItem.`is`(WitcheryItems.ARTHANA.get()) ||
            !player.offhandItem.`is`(WitcheryItems.WINE_GLASS.get())
        ) {
            return EventResult.pass()
        }

        if (hasNearbySacrificialCircle(livingEntity)) {
            performChickenBloodRitual(player, livingEntity)
        }

        return EventResult.pass()
    }

    /**
     * Checks if there's a sacrificial circle nearby
     */
    private fun hasNearbySacrificialCircle(entity: LivingEntity): Boolean {
        val possibleSkull = BlockPos.betweenClosedStream(entity.boundingBox.inflate(2.0))

        for (skullPos in possibleSkull) {
            val skullState = entity.level().getBlockState(skullPos)
            if (skullState.`is`(WitcheryBlocks.SACRIFICIAL_CIRCLE.get())) {
                return true
            }
        }

        return false
    }

    /**
     * Performs the chicken blood ritual
     */
    private fun performChickenBloodRitual(player: Player, chicken: Chicken) {
        player.offhandItem.shrink(1)

        val stackCopy = player.offhandItem.copy()
        if (!stackCopy.isEmpty) {
            Containers.dropItemStack(chicken.level(), player.x, player.y, player.z, stackCopy)
        }

        val bloodWine = WitcheryItems.WINE_GLASS.get().defaultInstance
        bloodWine.set(WitcheryDataComponents.CHICKEN_BLOOD.get(), true)
        bloodWine.set(WitcheryDataComponents.BLOOD.get(), chicken.uuid)
        player.setItemInHand(InteractionHand.OFF_HAND, bloodWine)
    }

    /**
     * Creates a sacrificial circle structure
     */
    fun makeSacrificialCircle(player: Player, blockPos: BlockPos): EventResult? {
        val pieces = SacrificialBlock.STRUCTURE.get().structurePieces

        pieces.forEach { piece ->
            piece.place(blockPos, player.level())
        }

        player.level().setBlockAndUpdate(
            blockPos,
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get().defaultBlockState()
        )

        player.level().getBlockEntity(blockPos)?.let { blockEntity ->
            if (blockEntity is MultiBlockComponentBlockEntity) {
                blockEntity.corePos = blockPos
            }
        }

        return EventResult.pass()
    }

    /**
     * If the vampire-player is level 3 this will count towards the requirement to level up to level 4
     */
    private fun tickNightsCount(player: Player?) {
        if (player is ServerPlayer) {
            VampireLeveling.increaseNightTicker(player)
        }
    }

    /**
     * If the player dies the night counter will reset
     */
    private fun resetNightCount(livingEntity: LivingEntity?): EventResult? {
        if (livingEntity is Player && getData(livingEntity).getVampireLevel() == 3) {
            VampireLeveling.resetNightCounter(livingEntity)
        }

        return EventResult.pass()
    }

    /**
     * When a blaze is killed is this tries to level up a vampire-player
     */
    private fun killBlaze(livingEntity: LivingEntity?, damageSource: DamageSource?): EventResult? {
        if (livingEntity is Blaze && damageSource?.entity is ServerPlayer) {
            val player = damageSource.entity as ServerPlayer
            VampireLeveling.increaseKilledBlazes(player)
        }

        return EventResult.pass()
    }

    /**
     * Registers all vampire-related event handlers
     */
    fun registerEvents() {
        TickEvent.PLAYER_PRE.register(VampireEventHandler::tickNightsCount)
        TickEvent.PLAYER_PRE.register(VampireEventHandler::tick)

        InteractionEvent.INTERACT_ENTITY.register { player, entity, _ -> interactEntityWithAbility(player, entity) }
        InteractionEvent.RIGHT_CLICK_BLOCK.register { player, hand, _, _ -> rightClickBlockAbility(player, hand) }

        EntityEvent.LIVING_DEATH.register { entity, _ -> resetNightCount(entity) }
        EntityEvent.LIVING_DEATH.register(VampireEventHandler::killChicken)
        EntityEvent.LIVING_DEATH.register(VampireEventHandler::killBlaze)

        PlayerEvent.PLAYER_CLONE.register { oldPlayer, newPlayer, _ -> respawn(oldPlayer, newPlayer) }
    }
}