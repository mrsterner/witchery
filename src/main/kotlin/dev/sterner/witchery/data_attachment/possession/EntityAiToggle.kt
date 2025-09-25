package dev.sterner.witchery.data_attachment.possession

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment.PlayerPossessionData
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment.PossessedEntityData
import dev.sterner.witchery.mixin.possession.MobEntityAccessor
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryDataAttachments.PLAYER_POSSESSION
import it.unimi.dsi.fastutil.objects.Object2BooleanMap
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import java.util.Map
import java.util.Objects
import java.util.Optional
import java.util.function.Supplier
import java.util.stream.Collectors


object EntityAiToggle {

    fun getEntityToggle(livingEntity: LivingEntity): Data {
        return livingEntity.getData(WitcheryDataAttachments.ENTITY_TOGGLE_DATA_ATTACHMENT)
    }

    fun setEntityToggle(livingEntity: LivingEntity, data: Data) {
        livingEntity.setData(WitcheryDataAttachments.ENTITY_TOGGLE_DATA_ATTACHMENT, data)
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


    /**
     * Toggles an AI inhibitor on this entity.
     *
     * @param inhibitorId the unique identifier for the mechanic inhibiting this entity's AI
     * @param inhibit if `true`, the entity's AI will be disabled, otherwise the inhibitor will stop affecting the entity
     * @param persistent if `true`, the inhibition will be saved with the entity
     */
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
        KEY.sync(this.owner)
    }

    public fun writeSyncPacket(buf: FriendlyByteBuf, recipient: ServerPlayer?) {
        buf.writeBoolean(this.isAiDisabled)
    }

    public fun applySyncPacket(buf: FriendlyByteBuf) {
        this.isAiDisabled = buf.readBoolean()
    }

    public fun readFromNbt(tag: CompoundTag, wrapperLookup: HolderLookup.Provider?) {
        tag.getList("inhibitors", Tag.TAG_STRING.toInt())
            .stream()
            .map(Tag::asString)
            .map(ResourceLocation::tryParse)
            .filter(Objects::nonNull)
            .forEach({ id -> this.aiInhibitors.put(id, true) })
        this.refresh(!this.aiInhibitors.isEmpty())
    }

    public fun writeToNbt(tag: CompoundTag, wrapperLookup: HolderLookup.Provider?) {
        tag.put(
            "inhibitors", this.aiInhibitors.object2BooleanEntrySet().stream()
                .filter { Object2BooleanMap.Entry.getBooleanValue() }
                .map<ResourceLocation?> { Map.Entry.key }
                .map<Any?>(ResourceLocation::toString)
                .map<Any?>(StringTag::valueOf)
                .collect(Collectors.toCollection(Supplier { ListTag() }))
        )
    }
}