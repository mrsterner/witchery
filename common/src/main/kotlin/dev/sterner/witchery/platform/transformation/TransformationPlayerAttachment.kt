package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncTransformationS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.player.Player

object TransformationPlayerAttachment {

    var bat: Bat? = null

    @JvmStatic
    fun getBatEntity(player: Player): Bat? {
        if (bat == null) {
            bat = EntityType.BAT.create(player.level())
            bat!!.isResting = false
        }
        return this.bat
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
    fun getForm(player: Player): TransformationType {
        return getData(player).transformationType
    }

    @JvmStatic
    fun removeForm(player: Player){
        setData(player, Data(TransformationType.NONE))
        player.abilities.mayfly = false
        player.abilities.flying = false
    }

    @JvmStatic
    fun setBatForm(player: Player) {
        setData(player, Data(TransformationType.BAT))
        player.abilities.mayfly = true
        player.abilities.flying = true
    }

    @JvmStatic
    fun setWolfForm(player: Player) {
        setData(player, Data(TransformationType.WOLF))
    }

    @JvmStatic
    fun setWereWolfForm(player: Player) {
        setData(player, Data(TransformationType.WEREWOLF))
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncTransformationS2CPayload(player, data))
        }
    }

    data class Data(
        val transformationType: TransformationType = TransformationType.NONE,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    TransformationType.TRANSFORMATION_CODEC.fieldOf("transformationType").forGetter { it.transformationType },

                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("transformation_player_data")
        }
    }

    enum class TransformationType: StringRepresentable{
        NONE,
        BAT,
        WOLF,
        WEREWOLF;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val TRANSFORMATION_CODEC: Codec<TransformationType> = StringRepresentable.fromEnum(TransformationType::values)
        }
    }
}