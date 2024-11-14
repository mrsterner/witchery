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
        makeBlood(provider, EntityType.VILLAGER, 5)
        makeBlood(provider, EntityType.SHEEP, 3)
    }

    private fun makeBlood(
        provider: BiConsumer<ResourceLocation, BloodPoolHandler.BloodData>,
        entityType: EntityType<*>,
        bloodDrops: Int
    ) {
        val fromId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        provider.accept(Witchery.id(fromId.path), BloodPoolHandler.BloodData(entityType, bloodDrops))
    }
}