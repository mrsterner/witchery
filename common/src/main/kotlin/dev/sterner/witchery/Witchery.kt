package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.core.item.ArchitecturyBucketItem
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.BlockEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.particle.ParticleProviderRegistry
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.item.ItemPropertiesRegistry
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.MandrakeEntityModel
import dev.sterner.witchery.client.particle.ColorBubbleParticle
import dev.sterner.witchery.client.renderer.AltarBlockEntityRenderer
import dev.sterner.witchery.client.renderer.CauldronBlockEntityRenderer
import dev.sterner.witchery.client.renderer.MandrakeEntityRenderer
import dev.sterner.witchery.client.screen.OvenScreen
import dev.sterner.witchery.entity.MandrakeEntity
import dev.sterner.witchery.item.WaystoneItem
import dev.sterner.witchery.registry.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.slf4j.Logger


object Witchery {
    const val MODID: String = "witchery"

    val LOGGER: Logger = LogUtils.getLogger()

    @JvmStatic
    fun init() {
        WitcheryBlocks.BLOCKS.register()
        WitcheryBlockEntityTypes.BLOCK_ENTITY_TYPES.register()
        WitcheryItems.ITEMS.register()
        WitcheryEntityTypes.ENTITY_TYPES.register()
        WitcherySounds.SOUNDS.register()
        WitcheryCreativeModeTabs.TABS.register()
        WitcheryParticleTypes.PARTICLES.register()
        WitcheryRecipeTypes.RECIPE_TYPES.register()
        WitcheryRecipeSerializers.RECIPE_SERIALIZERS.register()
        WitcheryMenuTypes.MENU_TYPES.register()
        WitcheryDataComponents.DATA.register()

        WitcheryPayloads.register()

        EntityAttributeRegistry.register(WitcheryEntityTypes.MANDRAKE, MandrakeEntity::createAttributes)

        ClientLifecycleEvent.CLIENT_SETUP.register{
            MenuRegistry.registerScreenFactory(WitcheryMenuTypes.OVEN_MENU_TYPE.get(),
                ::OvenScreen
            )
        }

        InteractionEvent.INTERACT_ENTITY.register(::interactEntityWaystone)
        BlockEvent.PLACE.register(::makeAltar)
    }

    private fun makeAltar(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity?): EventResult? {

        if (blockState.`is`(BlockTags.WOOL_CARPETS) && level.getBlockState(blockPos.below()).`is`(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get())) {
            val directions = listOf(
                Pair(Direction.NORTH, Direction.SOUTH),
                Pair(Direction.EAST, Direction.WEST)
            )

            for (dirPair in directions) {
                val firstDir = dirPair.first
                val secondDir = dirPair.second

                val validStructure = check3x2AltarStructure(level, blockPos.below(), firstDir, secondDir)

                if (validStructure != null) {
                    val middleBlockPos = validStructure[1]
                    executeActionOnMiddleAltar(level, middleBlockPos)
                    return EventResult.interruptTrue()
                }
            }
        }
        return EventResult.pass()
    }


    private fun hasCarpetOnTop(level: Level, pos: BlockPos): Boolean {
        val blockAbovePos = pos.above()
        val blockAboveState = level.getBlockState(blockAbovePos)
        return blockAboveState.`is`(BlockTags.WOOL_CARPETS)
    }

    private fun executeActionOnMiddleAltar(level: Level, middlePos: BlockPos) {
        println("3x2 altar structure complete at $middlePos!")
    }

    private fun interactEntityWaystone(player: Player, entity: Entity?, interactionHand: InteractionHand?): EventResult? {
        if (player.mainHandItem.`is`(WitcheryItems.WAYSTONE.get()) && interactionHand == InteractionHand.MAIN_HAND) {
            if (entity is Player) {
                WaystoneItem.bindPlayer(entity, player.mainHandItem)
                return EventResult.interruptTrue()
            } else if (entity is LivingEntity) {
                WaystoneItem.bindLivingEntity(entity, player.mainHandItem)
                return EventResult.interruptTrue()
            }
        }

        return EventResult.pass()
    }

    @JvmStatic
    @Environment(EnvType.CLIENT)
    fun initClient() {
        EntityModelLayerRegistry.register(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }

        EntityRendererRegistry.register(WitcheryEntityTypes.MANDRAKE) { MandrakeEntityRenderer(it) }
        EntityModelLayerRegistry.register(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }

        BlockEntityRendererRegistry.register(
            WitcheryBlockEntityTypes.ALTAR.get(),
            ::AltarBlockEntityRenderer
        )

        BlockEntityRendererRegistry.register(
            WitcheryBlockEntityTypes.CAULDRON.get(),
            ::CauldronBlockEntityRenderer
        )

        ParticleProviderRegistry.register(WitcheryParticleTypes.COLOR_BUBBLE.get(), ColorBubbleParticle::Provider)

        ItemPropertiesRegistry.register(
            WitcheryItems.WAYSTONE.get(),
            ResourceLocation.fromNamespaceAndPath(MODID, "is_bound")
        ) { itemStack, _, _, _ ->
            var ret = 0f
            val customData = itemStack.get(WitcheryDataComponents.GLOBAL_POS_COMPONENT.get())
            val customData2 = itemStack.get(WitcheryDataComponents.ENTITY_ID_COMPONENT.get())
            if (WaystoneItem.getPlayerProfile(itemStack) != null || customData2 != null) {
                ret = 2.0f
            } else if (customData != null) {
                ret = 1.0f
            }
            ret
        }
    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, name)
    }
}
