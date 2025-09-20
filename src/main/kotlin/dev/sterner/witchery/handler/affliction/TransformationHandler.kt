package dev.sterner.witchery.handler.affliction

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.PlatformUtils
import dev.sterner.witchery.data_attachment.WitcheryAttributes
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.transformation.TransformationPlayerAttachment
import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.StructureTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object TransformationHandler {

    var SMALL_SIZE = AttributeModifier(Witchery.id("small_transform"), -0.6, AttributeModifier.Operation.ADD_VALUE)

    const val MAX_COOLDOWN = 20 * 10
    var bat: Bat? = null
    var wolf: Wolf? = null
    var werewolf: WerewolfEntity? = null

    private var villageCheckTicker = 0



    @JvmStatic
    fun getBatEntity(player: Player): Bat? {
        if (bat == null) {
            bat = EntityType.BAT.create(player.level())
            bat!!.isResting = false
        }
        return this.bat
    }

    @JvmStatic
    fun getWolfEntity(player: Player): Wolf? {
        if (wolf == null) {
            wolf = EntityType.WOLF.create(player.level())
        }
        return this.wolf
    }

    @JvmStatic
    fun getWerewolf(player: Player): WerewolfEntity? {
        if (werewolf == null) {
            werewolf = WitcheryEntityTypes.WEREWOLF.get().create(player.level())
        }
        return this.werewolf
    }

    @JvmStatic
    fun isBat(player: Player): Boolean {
        return TransformationPlayerAttachment.getData(player).transformationType == TransformationPlayerAttachment.TransformationType.BAT
    }

    @JvmStatic
    fun isWolf(player: Player): Boolean {
        return TransformationPlayerAttachment.getData(player).transformationType == TransformationPlayerAttachment.TransformationType.WOLF
    }

    @JvmStatic
    fun isWerewolf(player: Player): Boolean {
        return TransformationPlayerAttachment.getData(player).transformationType == TransformationPlayerAttachment.TransformationType.WEREWOLF
    }

    @JvmStatic
    fun removeForm(player: Player) {
        TransformationPlayerAttachment.setData(
            player,
            TransformationPlayerAttachment.Data(TransformationPlayerAttachment.TransformationType.NONE, MAX_COOLDOWN)
        )

        removeAllTransformationModifiers(player)

        VampireLeveling.updateModifiers(player, AfflictionPlayerAttachment.getData(player).getVampireLevel(), false)
        WerewolfLeveling.updateModifiers(player, wolf = false, wolfMan = false)

        if (player.level() is ServerLevel) {
            PlatformUtils.tryDisableBatFlight(player)
        }
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, RefreshDimensionsS2CPayload())
    }

    @JvmStatic
    fun setBatForm(player: Player) {
        removeAllTransformationModifiers(player)

        VampireLeveling.updateModifiers(player, AfflictionPlayerAttachment.getData(player).getVampireLevel(), true)

        player.attributes.getInstance(Attributes.SCALE)?.let { attribute ->
            if (!attribute.hasModifier(SMALL_SIZE.id)) {
                attribute.addPermanentModifier(SMALL_SIZE)
            }
        }

        TransformationPlayerAttachment.setData(
            player,
            TransformationPlayerAttachment.Data(TransformationPlayerAttachment.TransformationType.BAT, 0, 0)
        )
    }

    @JvmStatic
    fun setWolfForm(player: Player) {
        removeAllTransformationModifiers(player)

        WerewolfLeveling.updateModifiers(player, wolf = true, wolfMan = false)

        player.attributes.getInstance(Attributes.SCALE)?.let { attribute ->
            if (!attribute.hasModifier(SMALL_SIZE.id)) {
                attribute.addPermanentModifier(SMALL_SIZE)
            }
        }

        TransformationPlayerAttachment.setData(
            player,
            TransformationPlayerAttachment.Data(TransformationPlayerAttachment.TransformationType.WOLF)
        )
    }

    @JvmStatic
    fun setWereWolfForm(player: Player) {
        removeAllTransformationModifiers(player)

        WerewolfLeveling.updateModifiers(player, wolf = false, wolfMan = true)

        player.attributes.getInstance(Attributes.SCALE)?.removeModifier(SMALL_SIZE)

        TransformationPlayerAttachment.setData(
            player,
            TransformationPlayerAttachment.Data(TransformationPlayerAttachment.TransformationType.WEREWOLF)
        )
    }

    private fun removeAllTransformationModifiers(player: Player) {
        player.attributes.getInstance(Attributes.SCALE)?.removeModifier(SMALL_SIZE)

        WerewolfLeveling.removeAllModifiers(player)

        VampireLeveling.updateModifiers(player, AfflictionPlayerAttachment.getData(player).getVampireLevel(), false)
    }

    @JvmStatic
    fun increaseBatFormTimer(player: Player) {
        val data = TransformationPlayerAttachment.getData(player)
        TransformationPlayerAttachment.setData(player, data.copy(batFormTicker = data.batFormTicker + 1))
    }

    fun tickWolf(player: Player) {
        if (player.level() is ServerLevel) {

        } else {
            if (isWolf(player)) {
                wolf?.tick()
            }
        }
    }

    fun tickBat(player: Player) {

        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= VampireAbility.BAT_FORM.requiredLevel) {
            if (player.level() is ServerLevel) {

                if (isBat(player)) {
                    checkForVillage(player)
                    if (player.onGround()) {
                        if (player.abilities.flying) {
                            player.abilities.flying = false
                            player.onUpdateAbilities()
                        }
                    } else {
                        PlatformUtils.tryEnableBatFlight(player)
                    }

                    increaseBatFormTimer(player)

                    var maxBatTime = (player.getAttribute(WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION)?.value ?: 0).toInt()

                    maxBatTime += if (AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 9) 60 * 20 else 0
                    val data = TransformationPlayerAttachment.getData(player)
                    TransformationPlayerAttachment.setData(player, data.copy(maxBatTimeClient = maxBatTime))
                    if (TransformationPlayerAttachment.getData(player).batFormTicker > maxBatTime) {
                        removeForm(player)
                    }

                } else {
                    if (AfflictionPlayerAttachment.getData(player).getVampireLevel() == 7) {
                        VampireLeveling.resetVillages(player)
                    }

                    PlatformUtils.tryDisableBatFlight(player)
                }
            } else {
                if (isBat(player)) {
                    bat?.tick()
                }
            }
        }
    }

    private fun checkForVillage(player: Player) {
        if (AfflictionPlayerAttachment.getData(player).getVampireLevel() == 7) {
            villageCheckTicker++
            if (villageCheckTicker > 20) {
                villageCheckTicker = 0
                val serverLevel = player.level() as ServerLevel

                if (serverLevel.structureManager().getStructureWithPieceAt(
                        player.blockPosition(),
                        StructureTags.VILLAGE
                    ).isValid
                ) {
                    val structureStart = serverLevel.structureManager().getStructureWithPieceAt(
                        player.blockPosition(),
                        StructureTags.VILLAGE
                    )

                    VampireLeveling.addVillage(player as ServerPlayer, structureStart.chunkPos)
                }
            }
        }
    }
}