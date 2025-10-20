package dev.sterner.witchery.data_gen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDamageSources
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.DamageTypeTagsProvider
import net.minecraft.tags.DamageTypeTags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class WitcheryDamageTypeTagProvider(
    output: PackOutput?,
    completableFuture: CompletableFuture<HolderLookup.Provider>?,
    existingFileHelper: ExistingFileHelper
) : DamageTypeTagsProvider(output, completableFuture, Witchery.MODID, existingFileHelper) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        tag(DamageTypeTags.NO_KNOCKBACK)
            .add(WitcheryDamageSources.IN_SUN)
    }
}