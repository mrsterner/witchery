package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.data.ErosionHandler
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryBloodProvider(
    dataOutput: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricCodecDataProvider<BloodPoolHandler.BloodData>(
    dataOutput,
    registriesFuture,
    PackOutput.Target.DATA_PACK,
    DIRECTORY,
    BloodPoolHandler.BloodData.CODEC
) {

    companion object {
        val DIRECTORY: String = "blood_pool"
    }

    override fun getName(): String {
        return DIRECTORY
    }

    override fun configure(
        provider: BiConsumer<ResourceLocation, BloodPoolHandler.BloodData>,
        lookup: HolderLookup.Provider?
    ) {
        makeBlood(provider, EntityType.PILLAGER, 5, 0)
        makeBlood(provider, EntityType.ILLUSIONER, 5, 0)
        makeBlood(provider, EntityType.VINDICATOR, 5, 0)
        makeBlood(provider, EntityType.WANDERING_TRADER, 5, 2)
        makeBlood(provider, EntityType.VILLAGER, 5, 2)
        makeBlood(provider, EntityType.SNIFFER, 5, 2)
        makeBlood(provider, EntityType.SHEEP, 3, 1)
        makeBlood(provider, EntityType.HORSE, 4, 1)
        makeBlood(provider, EntityType.DONKEY, 4, 1)
        makeBlood(provider, EntityType.MULE, 4, 1)
        makeBlood(provider, EntityType.COW, 3, 1)
        makeBlood(provider, EntityType.WOLF, 2, 1)
        makeBlood(provider, EntityType.PIG, 3, 1)
        makeBlood(provider, EntityType.CHICKEN, 2, 1)
    }

    //Quiality:
    // 0 bad: poison chance, (from illagers)
    // 1 normal: cant fill to max, (from sheep)
    // 2 pure: fill to max (from villagers)
    private fun makeBlood(
        provider: BiConsumer<ResourceLocation, BloodPoolHandler.BloodData>,
        entityType: EntityType<*>,
        bloodDrops: Int,
        quality: Int
    ) {
        val fromId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        provider.accept(Witchery.id(fromId.path), BloodPoolHandler.BloodData(entityType, bloodDrops, quality))
    }
}