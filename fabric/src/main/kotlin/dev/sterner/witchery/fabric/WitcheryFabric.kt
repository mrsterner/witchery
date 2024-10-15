package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.attachment.MutandisLevelAttachment
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.platform.MutandisLevelDataAttachmentPlatform
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryParticleTypes
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.renderer.RenderType


class WitcheryFabric : ModInitializer, ClientModInitializer {

    companion object {
        @Suppress("UnstableApiUsage")
        val LEVEL_DATA_TYPE: AttachmentType<MutandisLevelAttachment> =
            AttachmentRegistry.builder<MutandisLevelAttachment>()
                .persistent(MutandisLevelDataAttachmentPlatform.CODEC)
                .initializer { MutandisLevelAttachment() }
                .buildAndRegister(MutandisLevelDataAttachmentPlatform.ID)
    }

    override fun onInitialize() {
        Witchery.init()
    }

    override fun onInitializeClient() {
        Witchery.initClient()

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.COLOR_BUBBLE.get()
        ) { sprite: FabricSpriteProvider? ->
            ColorBubbleParticle.Provider(
                sprite!!
            )
        }
    }
}


