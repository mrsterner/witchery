package dev.sterner.witchery.fabric.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class WitcheryAdvancementProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricAdvancementProvider(output, registryLookup) {

    override fun generateAdvancement(registryLookup: HolderLookup.Provider?, consumer: Consumer<AdvancementHolder>) {

    }
}