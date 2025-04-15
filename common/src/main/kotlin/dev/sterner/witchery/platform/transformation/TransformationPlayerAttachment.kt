package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.handler.ability.VampireAbility
import dev.sterner.witchery.handler.vampire.VampireLeveling
import dev.sterner.witchery.handler.werewolf.WerewolfLeveling
import dev.sterner.witchery.payload.SyncTransformationS2CPayload
import dev.sterner.witchery.platform.PlatformUtils
import dev.sterner.witchery.platform.WitcheryAttributes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.StructureTags
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.player.Player
import kotlin.math.max

object TransformationPlayerAttachment {

    const val MAX_COOLDOWN = 20 * 10
    var bat: Bat? = null
    var wolf: Wolf? = null


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

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
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

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncTransformationS2CPayload(player, data)
            )
        }
    }

    private var villageCheckTicker = 0

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

    data class Data(
        val transformationType: TransformationType = TransformationType.NONE,
        val batFormCooldown: Int = 0,
        val batFormTicker: Int = 0,
        val maxBatTimeClient: Int = 0,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    TransformationType.TRANSFORMATION_CODEC.fieldOf("transformationType")
                        .forGetter { it.transformationType },
                    Codec.INT.fieldOf("batFormCooldown").forGetter { it.batFormCooldown },
                    Codec.INT.fieldOf("batFormTicker").forGetter { it.batFormTicker },
                    Codec.INT.fieldOf("maxBatTimeClient").forGetter { it.maxBatTimeClient },

                    ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("transformation_player_data")
        }
    }

    enum class TransformationType : StringRepresentable {
        NONE,
        BAT,
        WOLF,
        WEREWOLF;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val TRANSFORMATION_CODEC: Codec<TransformationType> =
                StringRepresentable.fromEnum(TransformationType::values)
        }
    }
}