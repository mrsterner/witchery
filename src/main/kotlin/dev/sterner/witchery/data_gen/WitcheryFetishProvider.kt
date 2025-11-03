package dev.sterner.witchery.data_gen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data.FetishEffectReloadListener
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.server.packs.PackType
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture

class WitcheryFetishProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<FetishEffectReloadListener.Data>(
    output,
    PackOutput.Target.DATA_PACK,
    "fetish",
    PackType.SERVER_DATA,
    FetishEffectReloadListener.Data.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun getName(): String {
        return "fetish"
    }

    override fun gather() {
        unconditional(
            Witchery.id("shrieking"),
            FetishEffectReloadListener.Data(
                specterCount = 3,
                bansheeCount = 2,
                effectLocation = Witchery.id("shrieking")
            )
        )
        unconditional(
            Witchery.id("ghost_walking"),
            FetishEffectReloadListener.Data(
                poltergeist = 3,
                specterCount = 1,
                bansheeCount = 1,
                effectLocation = Witchery.id("ghost_walking")
            )
        )
        unconditional(
            Witchery.id("disorientation"),
            FetishEffectReloadListener.Data(
                poltergeist = 5,
                effectLocation = Witchery.id("disorientation")
            )
        )
        unconditional(
            Witchery.id("sentinel"),
            FetishEffectReloadListener.Data(
                poltergeist = 3,
                specterCount = 3,
                effectLocation = Witchery.id("sentinel")
            )
        )
        unconditional(
            Witchery.id("voodoo_protection"),
            FetishEffectReloadListener.Data(
                poltergeist = 3,
                specterCount = 1,
                bansheeCount = 2,
                effectLocation = Witchery.id("voodoo_protection")
            )
        )
        unconditional(
            Witchery.id("summon_death"),
            FetishEffectReloadListener.Data(
                poltergeist = 6,
                specterCount = 6,
                bansheeCount = 6,
                effectLocation = Witchery.id("summon_death")
            )
        )
    }
}