package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
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
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class WitcheryAdvancementProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricAdvancementProvider(output, registryLookup) {

    override fun generateAdvancement(registryLookup: HolderLookup.Provider?, consumer: Consumer<AdvancementHolder>) {
        val root = rootAdvancement.save(consumer, "witchery:root")
        seedAdvancement.parent(root).save(consumer, "witchery:seeds")
        ovenAdvancement.parent(root).save(consumer, "witchery:oven")
        mutandisAdvancement.parent(root).save(consumer, "witchery:mutandis")
        whiffOfMagicAdvancement.parent(root).save(consumer, "witchery:whiff_of_magic")
    }

    companion object {
        val rootAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.GUIDEBOOK.get(),
                Component.translatable("advancements.witchery.root.title"),
                Component.translatable("advancements.witchery.root.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .addCriterion("has_crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))

        val seedAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.MANDRAKE_SEEDS.get(),
                Component.translatable("advancements.witchery.seeds.title"),
                Component.translatable("advancements.witchery.seeds.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.AND)
            .addCriterion(
                "has_mandrake",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.MANDRAKE_SEEDS.get())
            )
            .addCriterion(
                "has_belladonna",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.BELLADONNA_SEEDS.get())
            )
            .addCriterion(
                "has_water_artichoke",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.WATER_ARTICHOKE_SEEDS.get())
            )


        val ovenAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.IRON_WITCHES_OVEN.get(),
                Component.translatable("advancements.witchery.oven.title"),
                Component.translatable("advancements.witchery.oven.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_iron_oven",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.IRON_WITCHES_OVEN.get())
            )
            .addCriterion(
                "has_copper_oven",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.COPPER_WITCHES_OVEN.get())
            )


        val mutandisAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.MUTANDIS.get(),
                Component.translatable("advancements.witchery.mutandis.title"),
                Component.translatable("advancements.witchery.mutandis.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion("has_mutandis", InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.MUTANDIS.get()))


        val whiffOfMagicAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.WHIFF_OF_MAGIC.get(),
                Component.translatable("advancements.witchery.whiff_of_magic.title"),
                Component.translatable("advancements.witchery.whiff_of_magic.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_whiff_of_magic",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.WHIFF_OF_MAGIC.get())
            )
    }

}