package dev.sterner.witchery.datagen

import dev.sterner.witchery.Witchery
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class WitcheryModelProvider(output: PackOutput?, existingFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, Witchery.MODID, existingFileHelper) {

    override fun registerStatesAndModels() {

    }

}