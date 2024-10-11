package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates

class WitcheryModelProvider(output: FabricDataOutput?) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {

    }

    override fun generateItemModels(genetaror: ItemModelGenerators) {
        genetaror.generateFlatItem(WitcheryItems.GUIDEBOOK.get(), ModelTemplates.FLAT_ITEM)
        genetaror.generateFlatItem(WitcheryItems.MUTANDIS.get(), ModelTemplates.FLAT_ITEM)
    }
}