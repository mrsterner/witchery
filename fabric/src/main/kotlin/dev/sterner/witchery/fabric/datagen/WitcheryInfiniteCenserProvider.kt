package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryInfiniteCenserProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<InfiniteCenserReloadListener.InfiniteCenserData>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    InfiniteCenserReloadListener.InfiniteCenserData.CODEC
) {

    companion object {
        const val DIRECTORY: String = "infinite_censer"
    }

    override fun getName(): String {
        return "Infinite Censer Potions"
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, InfiniteCenserReloadListener.InfiniteCenserData>,
        lookup: HolderLookup.Provider?
    ) {

        makePotion(provider, "night_vision", Potions.NIGHT_VISION)
        makePotion(provider, "long_night_vision", Potions.LONG_NIGHT_VISION)
        makePotion(provider, "fire_resistance", Potions.FIRE_RESISTANCE)
        makePotion(provider, "long_fire_resistance", Potions.LONG_FIRE_RESISTANCE)
        makePotion(provider, "water_breathing", Potions.WATER_BREATHING)
        makePotion(provider, "long_water_breathing", Potions.LONG_WATER_BREATHING)
        makePotion(provider, "invisibility", Potions.INVISIBILITY)
        makePotion(provider, "long_invisibility", Potions.LONG_INVISIBILITY)

        makeCustomPotion(provider, "fertile", Witchery.id("fertile"))
    }

    private fun makePotion(
        provider: BiConsumer<ResourceLocation, InfiniteCenserReloadListener.InfiniteCenserData>,
        name: String,
        potion: Holder<Potion>
    ) {
        val potionId = potion.unwrapKey().get().location()
        provider.accept(
            Witchery.id(name),
            InfiniteCenserReloadListener.InfiniteCenserData(potionId)
        )
    }

    private fun makeCustomPotion(
        provider: BiConsumer<ResourceLocation, InfiniteCenserReloadListener.InfiniteCenserData>,
        name: String,
        potionId: ResourceLocation
    ) {
        provider.accept(
            Witchery.id(name),
            InfiniteCenserReloadListener.InfiniteCenserData(potionId)
        )
    }
}