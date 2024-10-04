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

        val root = makeBeeAdvancement("root", Items.HONEYCOMB, null, AdvancementType.TASK, consumer)
    }

    private fun makeBeeAdvancement(name: String, icon: Item, parent: AdvancementHolder?, type: AdvancementType,consumer: Consumer<AdvancementHolder>) : AdvancementHolder {
        val advancement = Advancement.Builder.advancement()
            .display(icon,
                Component.translatable("advancements.witchery.$name.title"),
                Component.translatable("advancements.witchery.$name.description"),
                ResourceLocation.withDefaultNamespace("textures/block/honey_block.png"),
                type,
                true,
                false,
                true
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion("has_$name", InventoryChangeTrigger.TriggerInstance.hasItems(icon))

        if (parent != null) {
            advancement.parent(parent)
        }

        return advancement.save(consumer, "witchery:witchery/$name")
    }

    private fun makeBeeAdvancement(icon: Item, parent: AdvancementHolder, task: AdvancementType, consumer: Consumer<AdvancementHolder>) : AdvancementHolder? {
        return makeBeeAdvancement(BuiltInRegistries.ITEM.getKey(icon).path.lowercase(), icon, parent, task, consumer)
    }

    private fun makeBeeAdvancement(icon: Item, parent: AdvancementHolder, consumer: Consumer<AdvancementHolder>) : AdvancementHolder? {
        return makeBeeAdvancement(BuiltInRegistries.ITEM.getKey(icon).path.lowercase(), icon, parent, AdvancementType.TASK, consumer)
    }
}