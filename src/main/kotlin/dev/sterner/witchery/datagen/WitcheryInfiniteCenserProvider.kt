package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.InfiniteCenserReloadListener
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture

class WitcheryInfiniteCenserProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<InfiniteCenserReloadListener.InfiniteCenserData>(
    output,
    PackOutput.Target.DATA_PACK,
    "infinite_censer",
    PackType.SERVER_DATA,
    InfiniteCenserReloadListener.InfiniteCenserData.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun getName(): String {
        return "infinite_censer"
    }

    override fun gather() {
        makePotion("night_vision", Potions.NIGHT_VISION)
        makePotion("luck", Potions.LUCK)
        makePotion("slow_falling", Potions.SLOW_FALLING)
        makePotion("long_night_vision", Potions.LONG_NIGHT_VISION)
        makePotion("fire_resistance", Potions.FIRE_RESISTANCE)
        makePotion("long_fire_resistance", Potions.LONG_FIRE_RESISTANCE)
        makePotion("water_breathing", Potions.WATER_BREATHING)
        makePotion("long_water_breathing", Potions.LONG_WATER_BREATHING)
        makePotion("invisibility", Potions.INVISIBILITY)
        makePotion("long_invisibility", Potions.LONG_INVISIBILITY)
        makePotion("long_speed", Potions.LONG_SWIFTNESS)
        makePotion("speed", Potions.SWIFTNESS)
        makePotion("strong_speed", Potions.STRONG_SWIFTNESS)
        makePotion("leaping", Potions.LONG_LEAPING)
        makePotion("long_leaping", Potions.LEAPING)
        makePotion("strong_leaping", Potions.STRONG_LEAPING)


        makeCustomPotion("fertile", Witchery.id("fertile"))
        makeCustomPotion("extinguish", Witchery.id("extinguish"))
        makeCustomPotion("harvest", Witchery.id("harvest"))
        makeCustomPotion("grow_flowers", Witchery.id("grow_flowers"))
        makeCustomPotion("till_land", Witchery.id("till_land"))
        makeCustomPotion("ender_bound", Witchery.id("ender_bound"))
        makeCustomPotion("grow_lily", Witchery.id("grow_lily"))
        makeCustomPotion("prune_leaves", Witchery.id("prune_leaves"))
        makeCustomPotion("plant_dropped_seeds", Witchery.id("plant_dropped_seeds"))
        makeCustomPotion("fell_tree", Witchery.id("fell_tree"))
        makeCustomPotion("love", Witchery.id("love"))
    }


    private fun makePotion(
        name: String,
        potion: Holder<Potion>
    ) {
        val potionId = potion.unwrapKey().get().location()
        unconditional(
            Witchery.id(name),
            InfiniteCenserReloadListener.InfiniteCenserData(potionId)
        )
    }

    private fun makeCustomPotion(
        name: String,
        potionId: ResourceLocation
    ) {
        unconditional(
            Witchery.id(name),
            InfiniteCenserReloadListener.InfiniteCenserData(potionId)
        )
    }
}