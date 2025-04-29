package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryDamageSources
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageType
import java.util.concurrent.CompletableFuture

class WitcheryDamageTypeTagProvider(
    output: FabricDataOutput?,
    completableFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricTagProvider<DamageType>(output, Registries.DAMAGE_TYPE, completableFuture) {
    override fun addTags(wrapperLookup: HolderLookup.Provider) {

        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK)
            .addOptional(WitcheryDamageSources.IN_SUN)
    }
}