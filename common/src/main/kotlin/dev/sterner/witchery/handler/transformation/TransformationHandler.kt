package dev.sterner.witchery.handler.transformation

import dev.sterner.witchery.entity.WerewolfEntity
import dev.sterner.witchery.handler.ability.VampireAbility
import dev.sterner.witchery.handler.vampire.VampireLeveling
import dev.sterner.witchery.handler.werewolf.WerewolfLeveling
import dev.sterner.witchery.payload.RefreshDimensionsS2CPayload
import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment.Data
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment.TransformationType
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment.getData
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment.setData
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.StructureTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.player.Player
import kotlin.math.max

object TransformationHandler {

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
        return getData(player).transformationType == TransformationType.BAT
    }

    @JvmStatic
    fun isWolf(player: Player): Boolean {
        return getData(player).transformationType == TransformationType.WOLF
    }

    @JvmStatic
    fun isWerewolf(player: Player): Boolean {
        return getData(player).transformationType == TransformationType.WEREWOLF
    }

    @JvmStatic
    fun removeForm(player: Player) {
        setData(player, Data(TransformationType.NONE, MAX_COOLDOWN))
        VampireLeveling.updateModifiers(player, VampirePlayerAttachment.getData(player).getVampireLevel(), false)
        WerewolfLeveling.updateModifiers(player, wolf = false, wolfMan = false)
        if (player.level() is ServerLevel) {
            PlatformUtils.tryDisableBatFlight(player)
        }
        WitcheryPayloads.sendToPlayers(
            player.level(),
            player.blockPosition(),
            RefreshDimensionsS2CPayload()
        )
    }

    @JvmStatic
    fun setBatForm(player: Player) {
        val data = getData(player)
        if (data.batFormCooldown <= 0) {
            VampireLeveling.updateModifiers(player, VampirePlayerAttachment.getData(player).getVampireLevel(), true)
            setData(player, Data(TransformationType.BAT, 0, 0))
        }
    }

    @JvmStatic
    fun setWolfForm(player: Player) {
        WerewolfLeveling.updateModifiers(player, wolf = true, wolfMan = false)
        setData(player, Data(TransformationType.WOLF))
    }

    @JvmStatic
    fun setWereWolfForm(player: Player) {
        WerewolfLeveling.updateModifiers(player, wolf = false, wolfMan = true)
        setData(player, Data(TransformationType.WEREWOLF))
    }

    @JvmStatic
    fun increaseBatFormTimer(player: Player) {
        val data = getData(player)
        setData(player, data.copy(batFormTicker = data.batFormTicker + 1))
    }

    @JvmStatic
    fun decreaseBatFormCooldown(player: Player) {
        val data = getData(player)
        if (data.batFormCooldown > 0) {
            setData(player, data.copy(batFormCooldown = max(data.batFormCooldown - 1, 0)))
        }
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

        if (VampirePlayerAttachment.getData(player).getVampireLevel() >= VampireAbility.BAT_FORM.unlockLevel) {
            if (player.level() is ServerLevel) {

                decreaseBatFormCooldown(player)

                if (isBat(player)) {
                    checkForVillage(player)
                    PlatformUtils.tryEnableBatFlight(player)

                    increaseBatFormTimer(player)

                    var maxBatTime =
                        (player.getAttribute(WitcheryAttributes.VAMPIRE_BAT_FORM_DURATION)?.value ?: 0).toInt()
                    maxBatTime += if (VampirePlayerAttachment.getData(player).getVampireLevel() >= 9) 60 * 20 else 0
                    val data = getData(player)
                    setData(player, data.copy(maxBatTimeClient = maxBatTime))
                    if (getData(player).batFormTicker > maxBatTime) {
                        removeForm(player)
                    }

                } else {
                    if (VampirePlayerAttachment.getData(player).getVampireLevel() == 7) {
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
        if (VampirePlayerAttachment.getData(player).getVampireLevel() == 7) {
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