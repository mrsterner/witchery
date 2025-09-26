package dev.sterner.witchery.data_attachment.possession

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.possession.MobEntityAccessor
import dev.sterner.witchery.payload.SyncAIEntityToggleS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import it.unimi.dsi.fastutil.objects.Object2BooleanMap
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.network.PacketDistributor


object EntityAiToggle {

    val POSSESSION_MECHANISM_ID: ResourceLocation = Witchery.id("possession")

    fun getEntityToggle(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.ENTITY_TOGGLE_DATA_ATTACHMENT)
    }

    fun setEntityToggle(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.ENTITY_TOGGLE_DATA_ATTACHMENT, data)
        if (livingEntity.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntity(livingEntity, SyncAIEntityToggleS2CPayload(livingEntity.id, data))
        }
    }

    data class Data(
        var isAiDisabled: Boolean = false,
        var inhibitors: Object2BooleanMap<ResourceLocation> = Object2BooleanOpenHashMap()
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isAiDisabled").forGetter { it.isAiDisabled },
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.BOOL)
                        .fieldOf("inhibitors")
                        .forGetter { it.inhibitors }
                ).apply(instance) { isAiDisabled, inhibitorsMap ->
                    val fastUtilMap = Object2BooleanOpenHashMap<ResourceLocation>()
                    fastUtilMap.putAll(inhibitorsMap)
                    Data(isAiDisabled, fastUtilMap)
                }
            }
        }
    }

    @JvmStatic
    fun toggleAi(entity: LivingEntity,inhibitorId: ResourceLocation?, inhibit: Boolean, persistent: Boolean) {
        val toggle = getEntityToggle(entity)

        val wasDisabled = toggle.isAiDisabled
        if (inhibit) {
            toggle.inhibitors.put(inhibitorId, persistent)
        } else {
            toggle.inhibitors.removeBoolean(inhibitorId)
        }
        val nowDisabled = !toggle.inhibitors.isEmpty()

        if (wasDisabled != nowDisabled) {
            this.refresh(entity, nowDisabled)
        }
    }

    private fun refresh(entity: LivingEntity, nowDisabled: Boolean) {
        val toggle = getEntityToggle(entity)
        toggle.isAiDisabled = nowDisabled
        (entity.getBrain() as DisableableAiController).`requiem$setDisabled`(nowDisabled)
        if (entity is MobEntityAccessor) {
            (entity.getGoalSelector() as DisableableAiController).`requiem$setDisabled`(nowDisabled)
            (entity.getTargetSelector() as DisableableAiController).`requiem$setDisabled`(nowDisabled)
            (entity.`requiem$getNavigation`() as DisableableAiController).`requiem$setDisabled`(nowDisabled)
        }
    }
}