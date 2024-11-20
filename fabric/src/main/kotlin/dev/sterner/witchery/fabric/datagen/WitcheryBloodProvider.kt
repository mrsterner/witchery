package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.data.ErosionHandler
import dev.sterner.witchery.registry.WitcheryEntityTypes
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
        makeBlood(provider, EntityType.PLAYER, 3, 2)
        makeBlood(provider, EntityType.PILLAGER, 5, 0)
        makeBlood(provider, EntityType.WITCH, 5, 0)
        makeBlood(provider, EntityType.ZOMBIE_VILLAGER, 5, 0)
        makeBlood(provider, EntityType.ZOMBIFIED_PIGLIN, 5, 0)
        makeBlood(provider, EntityType.ZOMBIE_HORSE, 5, 0)
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
        makeBlood(provider, EntityType.RABBIT, 2, 1)
        makeBlood(provider, EntityType.ARMADILLO, 2, 1)
        makeBlood(provider, EntityType.TURTLE, 2, 1)
        makeBlood(provider, EntityType.BAT, 1, 1)
        makeBlood(provider, EntityType.FROG, 1, 0)
        makeBlood(provider, EntityType.SQUID, 1, 0)
        makeBlood(provider, EntityType.GLOW_SQUID, 1, 0)
        makeBlood(provider, EntityType.AXOLOTL, 1, 1)
        makeBlood(provider, EntityType.PARROT, 1, 1)
        makeBlood(provider, EntityType.DOLPHIN, 2, 1)
        makeBlood(provider, EntityType.CAT, 2, 1)
        makeBlood(provider, EntityType.FOX, 2, 1)
        makeBlood(provider, EntityType.OCELOT, 2, 1)
        makeBlood(provider, EntityType.CAMEL, 3, 1)
        makeBlood(provider, EntityType.GOAT, 3, 1)
        makeBlood(provider, EntityType.HOGLIN, 3, 1)
        makeBlood(provider, EntityType.ZOGLIN, 3, 0)
        makeBlood(provider, EntityType.LLAMA, 3, 1)
        makeBlood(provider, EntityType.TRADER_LLAMA, 3, 1)
        makeBlood(provider, EntityType.MOOSHROOM, 3, 0)
        makeBlood(provider, EntityType.PIGLIN, 3, 0)
        makeBlood(provider, EntityType.PIGLIN_BRUTE, 3, 0)
        makeBlood(provider, EntityType.RAVAGER, 4, 0)
        makeBlood(provider, EntityType.POLAR_BEAR, 4, 1)
        makeBlood(provider, EntityType.PANDA, 3, 1)

        makeBlood(provider, WitcheryEntityTypes.VAMPIRE.get(), 6, 1)
        makeBlood(provider, WitcheryEntityTypes.WEREWOLF.get(), 4, 1)
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