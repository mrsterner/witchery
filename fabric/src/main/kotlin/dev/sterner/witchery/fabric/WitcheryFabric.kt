package dev.sterner.witchery.fabric

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import dev.architectury.registry.client.gui.ClientTooltipComponentRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.BloodSplashParticle
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.particle.ZzzParticle
import dev.sterner.witchery.fabric.client.*
import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.fabric.registry.WitcheryOxidizables
import dev.sterner.witchery.platform.fabric.WitcheryFluidHandlerFabric
import dev.sterner.witchery.registry.*
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.world.level.levelgen.GenerationStep
import java.io.IOException


class WitcheryFabric : ModInitializer, ClientModInitializer {

    override fun onInitialize() {
        WitcheryFabricAttachmentRegistry.init()
        Witchery.init()
        WitcheryEntityDataSerializers.register()
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(WitcheryCreativeModeTabs::modifyExistingTabs)

        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_LOG.get(), WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ROWAN_WOOD.get(), WitcheryBlocks.STRIPPED_ROWAN_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_LOG.get(), WitcheryBlocks.STRIPPED_ALDER_LOG.get())
        StrippableBlockRegistry.register(WitcheryBlocks.ALDER_WOOD.get(), WitcheryBlocks.STRIPPED_ALDER_WOOD.get())

        StrippableBlockRegistry.register(WitcheryBlocks.HAWTHORN_LOG.get(), WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
        StrippableBlockRegistry.register(
            WitcheryBlocks.HAWTHORN_WOOD.get(),
            WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get()
        )

        WitcheryFlammability.register()
        WitcheryOxidizables.register()

        BiomeModifications.addFeature(
            BiomeSelectors.tag(ConventionalBiomeTags.IS_PLAINS),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            WitcheryWorldgenKeys.WISPY_PLACED_KEY
        )

        FluidStorage.SIDED.registerForBlockEntity(
            { blockEntity, _ ->
                WitcheryFluidHandlerFabric(blockEntity.fluidTank, 1)
            },
            WitcheryBlockEntityTypes.CAULDRON.get()
        )
    }

    override fun onInitializeClient() {
        Witchery.initClient()

        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.SPINNING_WHEEL.get(),
            SpinningWheelDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.BROOM.get(), BroomDynamicRenderer())
        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get(),
            DreamWeaverDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.DREAM_WEAVER_OF_FASTING.get(),
            DreamWeaverDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get(),
            DreamWeaverDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get(),
            DreamWeaverDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(
            WitcheryItems.DREAM_WEAVER_OF_INTENSITY.get(),
            DreamWeaverDynamicRenderer()
        )
        BuiltinItemRendererRegistry.INSTANCE.register(WitcheryItems.DREAM_WEAVER.get(), DreamWeaverDynamicRenderer())

        ArmorRenderer.register(
            WitchesRobesArmorRendererFabric(),
            WitcheryItems.WITCHES_ROBES.get(),
            WitcheryItems.WITCHES_HAT.get(),
            WitcheryItems.WITCHES_SLIPPERS.get(),
            WitcheryItems.BABA_YAGAS_HAT.get()
        )
        ArmorRenderer.register(
            VampireArmorRendererFabric(),
            WitcheryItems.DRESS_COAT.get(),
            WitcheryItems.TOP_HAT.get(),
            WitcheryItems.OXFORD_BOOTS.get(),
            WitcheryItems.TROUSERS.get()
        )
        ArmorRenderer.register(
            HunterArmorRendererFabric(),
            WitcheryItems.HUNTER_HELMET.get(),
            WitcheryItems.HUNTER_CHESTPLATE.get(),
            WitcheryItems.HUNTER_LEGGINGS.get(),
            WitcheryItems.HUNTER_BOOTS.get()
        )

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.COLOR_BUBBLE.get()
        ) { sprite: FabricSpriteProvider? ->
            ColorBubbleParticle.Provider(
                sprite!!
            )
        }

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.ZZZ.get()
        ) { sprite: FabricSpriteProvider? ->
            ZzzParticle.Provider(
                sprite!!
            )
        }

        ParticleFactoryRegistry.getInstance().register(
            WitcheryParticleTypes.SPLASHING_BLOOD.get()
        ) { sprite: FabricSpriteProvider? ->
            BloodSplashParticle.ParticleFactory(
                sprite!!
            )
        }

        CoreShaderRegistrationCallback.EVENT.register(this::registerShaders)
        //ModelLoadingPlugin.register(WitcheryFabricModelLoaderPlugin())

    }

    @Throws(IOException::class)
    private fun registerShaders(ctx: CoreShaderRegistrationCallback.RegistrationContext) {
        ctx.register(
            Witchery.id("spirit_portal"), DefaultVertexFormat.NEW_ENTITY
        ) { shaderInstance: ShaderInstance ->
            WitcheryShaders.spiritPortal = shaderInstance
        }
    }
}


