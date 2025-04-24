package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.data.FetishEffectHandler
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryFetishProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<FetishEffectHandler.Data>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    FetishEffectHandler.Data.CODEC
) {

    companion object {
        val DIRECTORY: String = "fetish"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, FetishEffectHandler.Data>,
        lookup: HolderLookup.Provider?
    ) {
        provider.accept(Witchery.id("shrieking"),
            FetishEffectHandler.Data(
                specterCount = 3,
                bansheeCount = 2,
                effectLocation =  Witchery.id("shrieking")
            )
        )
        provider.accept(Witchery.id("ghost_walking"),
            FetishEffectHandler.Data(
                spiritCount = 3,
                specterCount = 1,
                bansheeCount = 1,
                effectLocation =  Witchery.id("ghost_walking")
            )
        )
        provider.accept(Witchery.id("disorientation"),
            FetishEffectHandler.Data(
                spiritCount = 3,
                poltergeistCount = 2,
                effectLocation =  Witchery.id("disorientation")
            )
        )
        provider.accept(Witchery.id("sentinel"),
            FetishEffectHandler.Data(
                spiritCount = 3,
                specterCount = 3,
                effectLocation =  Witchery.id("sentinel")
            )
        )
        provider.accept(Witchery.id("voodoo_protection"),
            FetishEffectHandler.Data(
                spiritCount = 3,
                specterCount = 1,
                bansheeCount = 1,
                poltergeistCount = 1,
                effectLocation =  Witchery.id("voodoo_protection")
            )
        )
        provider.accept(Witchery.id("summon_death"),
            FetishEffectHandler.Data(
                spiritCount = 5,
                specterCount = 5,
                bansheeCount = 5,
                poltergeistCount = 5,
                effectLocation =  Witchery.id("summon_death")
            )
        )
    }
}