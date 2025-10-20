package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.core.util.WitcheryUtil
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

data class ReadTabletC2SPayload(
    val tabletId: UUID,
    val tabletPos: BlockPos
) : CustomPacketPayload {


    fun handleOnServer(ctx: IPayloadContext) {
        val player = ctx.player() as? ServerPlayer ?: return
        val level = player.serverLevel()

        if (!WitcheryUtil.hasAdvancement(player, Witchery.id("necro/1"))) {
            return
        }

        val infusion = InfusionPlayerAttachment.getData(player)
        if (infusion.type != InfusionType.NECRO) {
            return
        }

        val data = AfflictionPlayerAttachment.getData(player)
        if (data.lichData.readTablets.size >= MAX_TABLETS) {
            return
        }

        val blockEntity = level.getBlockEntity(tabletPos)
        if (blockEntity !is AncientTabletBlockEntity) return
        if (blockEntity.getTabletId() != tabletId) return
        if (player.distanceToSqr(Vec3.atCenterOf(tabletPos)) > 100.0) return

        if (data.lichData.readTablets.contains(tabletId)) {
            return
        }

        val newData = data.addReadTablet(tabletId)
        AfflictionPlayerAttachment.setData(player, newData)

        val tabletCount = newData.lichData.readTablets.size
        if (tabletCount <= MAX_TABLETS) {
            val advancementLocation = ADVANCEMENT_LOCATIONS[tabletCount - 1]
            val criterion = "impossible_$tabletCount"
            WitcheryUtil.grantAdvancementCriterion(player, advancementLocation, criterion)
        }

        player.addEffect(MobEffectInstance(MobEffects.CONFUSION, 200, 0))
        player.addEffect(MobEffectInstance(MobEffects.DARKNESS, 200, 0))

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.PORTAL_TRIGGER,
            SoundSource.PLAYERS,
            1.0f,
            0.5f
        )

        player.displayClientMessage(
            Component.literal("Ancient knowledge floods your mind... ($tabletCount/$MAX_TABLETS)")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
            true
        )
    }

    companion object {
        const val MAX_TABLETS = 3

        val ADVANCEMENT_LOCATIONS = listOf(
            Witchery.id("necro/2"),
            Witchery.id("necro/3"),
            Witchery.id("necro/4")
        )
        val ID: CustomPacketPayload.Type<ReadTabletC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("read_tablet"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ReadTabletC2SPayload> =
            StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                ReadTabletC2SPayload::tabletId,
                BlockPos.STREAM_CODEC,
                ReadTabletC2SPayload::tabletPos,
                ::ReadTabletC2SPayload
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}