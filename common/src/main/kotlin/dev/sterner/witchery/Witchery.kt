package dev.sterner.witchery

import com.mojang.logging.LogUtils
import dev.architectury.registry.ReloadListenerRegistry
import dev.sterner.witchery.registry.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
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
    }

    @JvmStatic
    @Environment(EnvType.CLIENT)
    fun initClient() {

    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MODID, name)
    }
}
