package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data.BloodPoolReloadListener
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.server.packs.PackType
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.JsonCodecProvider
import java.util.concurrent.CompletableFuture

class WitcheryBloodProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper
) : JsonCodecProvider<BloodPoolReloadListener.BloodData>(
    output,
    PackOutput.Target.DATA_PACK,
    "blood_pool",
    PackType.SERVER_DATA,
    BloodPoolReloadListener.BloodData.CODEC,
    lookupProvider,
    Witchery.MODID,
    existingFileHelper
) {

    override fun gather() {
        // Players & Mobs
        makeBlood(EntityType.PLAYER, 3, 2)
        makeBlood(EntityType.PILLAGER, 5, 0)
        makeBlood(EntityType.WITCH, 5, 0)
        makeBlood(EntityType.ZOMBIE_VILLAGER, 5, 0)
        makeBlood(EntityType.ZOMBIFIED_PIGLIN, 5, 0)
        makeBlood(EntityType.ZOMBIE_HORSE, 5, 0)
        makeBlood(EntityType.ILLUSIONER, 5, 0)
        makeBlood(EntityType.VINDICATOR, 5, 0)
        makeBlood(EntityType.WANDERING_TRADER, 5, 2)
        makeBlood(EntityType.VILLAGER, 5, 2)
        makeBlood(EntityType.SNIFFER, 5, 2)

        // Animals
        makeBlood(EntityType.SHEEP, 3, 1)
        makeBlood(EntityType.HORSE, 4, 1)
        makeBlood(EntityType.DONKEY, 4, 1)
        makeBlood(EntityType.MULE, 4, 1)
        makeBlood(EntityType.COW, 3, 1)
        makeBlood(EntityType.WOLF, 2, 1)
        makeBlood(EntityType.PIG, 3, 1)
        makeBlood(EntityType.CHICKEN, 2, 1)
        makeBlood(EntityType.RABBIT, 2, 1)
        makeBlood(EntityType.ARMADILLO, 2, 1)
        makeBlood(EntityType.TURTLE, 2, 1)
        makeBlood(EntityType.BAT, 1, 1)
        makeBlood(EntityType.FROG, 1, 0)
        makeBlood(EntityType.SQUID, 1, 0)
        makeBlood(EntityType.GLOW_SQUID, 1, 0)
        makeBlood(EntityType.AXOLOTL, 1, 1)
        makeBlood(EntityType.PARROT, 1, 1)
        makeBlood(EntityType.DOLPHIN, 2, 1)
        makeBlood(EntityType.CAT, 2, 1)
        makeBlood(EntityType.FOX, 2, 1)
        makeBlood(EntityType.OCELOT, 2, 1)
        makeBlood(EntityType.CAMEL, 3, 1)
        makeBlood(EntityType.GOAT, 3, 1)
        makeBlood(EntityType.HOGLIN, 3, 1)
        makeBlood(EntityType.ZOGLIN, 3, 0)
        makeBlood(EntityType.LLAMA, 3, 1)
        makeBlood(EntityType.TRADER_LLAMA, 3, 1)
        makeBlood(EntityType.MOOSHROOM, 3, 0)
        makeBlood(EntityType.PIGLIN, 3, 0)
        makeBlood(EntityType.PIGLIN_BRUTE, 3, 0)
        makeBlood(EntityType.RAVAGER, 4, 0)
        makeBlood(EntityType.POLAR_BEAR, 4, 1)
        makeBlood(EntityType.PANDA, 3, 1)

        // Witchery mobs
        makeBlood(WitcheryEntityTypes.VAMPIRE.get(), 6, 1)
        makeBlood(WitcheryEntityTypes.WEREWOLF.get(), 4, 1)
    }

    private fun makeBlood(entityType: EntityType<*>, bloodDrops: Int, quality: Int) {
        val id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        unconditional(Witchery.id(id.path), BloodPoolReloadListener.BloodData(entityType, bloodDrops, quality))
    }
}
