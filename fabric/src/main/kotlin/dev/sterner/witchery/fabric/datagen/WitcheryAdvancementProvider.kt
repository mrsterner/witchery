package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancements.*
import net.minecraft.advancements.critereon.ChangeDimensionTrigger
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class WitcheryAdvancementProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricAdvancementProvider(output, registryLookup) {

    override fun generateAdvancement(registryLookup: HolderLookup.Provider?, consumer: Consumer<AdvancementHolder>) {
        val root = rootAdvancement.save(consumer, "witchery:root")
        seedAdvancement.parent(root).save(consumer, "witchery:seeds")
        ovenAdvancement.parent(root).save(consumer, "witchery:oven")
        cauldronAdvancement.parent(root).save(consumer, "witchery:cauldron")
        mutandisAdvancement.parent(root).save(consumer, "witchery:mutandis")
        whiffOfMagicAdvancement.parent(root).save(consumer, "witchery:whiff_of_magic")
        brazierAdvancement.parent(root).save(consumer, "witchery:brazier")
        val gypsum = gypsumAdvancement.parent(root).save(consumer, "witchery:gypsum")
        val ritual = chalkAdvancement.parent(gypsum).save(consumer, "witchery:chalk")
        necromantic.parent(ritual).save(consumer, "witchery:necromantic")
        val spirit = spiritWorld.parent(root).save(consumer, "witchery:spirit_world")
        disturbed.parent(spirit).save(consumer, "witchery:disturbed")

        val vamp1 = makeVampTornPageAdvancement("1", null, consumer)
        val vamp2 = makeVampTornPageAdvancement("2", vamp1, consumer)
        val vamp3 = makeVampTornPageAdvancement("3", vamp2, consumer)
        val vamp4 = makeVampTornPageAdvancement("4", vamp3, consumer)
        val vamp5 = makeVampTornPageAdvancement("5", vamp4, consumer)
        val vamp6 = makeVampTornPageAdvancement("6", vamp5, consumer)
        val vamp7 = makeVampTornPageAdvancement("7", vamp6, consumer)
        val vamp8 = makeVampTornPageAdvancement("8", vamp7, consumer)
        val vamp9 = makeVampTornPageAdvancement("9", vamp8, consumer)
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

        val cauldronAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.CAULDRON.get(),
                Component.translatable("advancements.witchery.cauldron.title"),
                Component.translatable("advancements.witchery.cauldron.cauldron"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_iron_cauldron",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.CAULDRON.get())
            )
            .addCriterion(
                "has_copper_cauldron",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.COPPER_CAULDRON.get())
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

        val brazierAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.BRAZIER.get(),
                Component.translatable("advancements.witchery.brazier.title"),
                Component.translatable("advancements.witchery.brazier.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_brazier",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.BRAZIER.get())
            )

        val gypsumAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.GYPSUM.get(),
                Component.translatable("advancements.witchery.gypsum.title"),
                Component.translatable("advancements.witchery.gypsum.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_gypsum",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.GYPSUM.get())
            )

        val chalkAdvancement = Advancement.Builder.advancement()
            .display(
                WitcheryItems.RITUAL_CHALK.get(),
                Component.translatable("advancements.witchery.chalk.title"),
                Component.translatable("advancements.witchery.chalk.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_chalk",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.RITUAL_CHALK.get())
            )

        val necromantic = Advancement.Builder.advancement()
            .display(
                WitcheryItems.NECROMANTIC_STONE.get(),
                Component.translatable("advancements.witchery.necromantic.title"),
                Component.translatable("advancements.witchery.necromantic.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_necromantic",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.NECROMANTIC_STONE.get())
            )

        val disturbed = Advancement.Builder.advancement()
            .display(
                WitcheryItems.DISTURBED_COTTON.get(),
                Component.translatable("advancements.witchery.disturbed.title"),
                Component.translatable("advancements.witchery.disturbed.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_disturbed",
                InventoryChangeTrigger.TriggerInstance.hasItems(WitcheryItems.DISTURBED_COTTON.get())
            )

        val spiritWorld = Advancement.Builder.advancement()
            .display(
                WitcheryItems.DISTURBED_COTTON.get(),
                Component.translatable("advancements.witchery.spirit_world.title"),
                Component.translatable("advancements.witchery.spirit_world.description"),
                Witchery.id("textures/block/rowan_planks.png"),
                AdvancementType.TASK,
                true,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion(
                "has_enter_dream",
                ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(WitcheryWorldgenKeys.DREAM)
            )
            .addCriterion(
                "has_enter_nightmare",
                ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(WitcheryWorldgenKeys.NIGHTMARE)
            )


        fun makeVampTornPageAdvancement(
            id: String,
            parent: AdvancementHolder?,
            consumer: Consumer<AdvancementHolder>
        ): AdvancementHolder {
            val advancement = Advancement.Builder.advancement()
                .display(
                    WitcheryItems.TORN_PAGE.get(),
                    Component.translatable("advancements.witchery.vampire_${id}.title"),
                    Component.translatable("advancements.witchery.vampire_${id}.description"),
                    Witchery.id("textures/block/rowan_planks.png"),
                    AdvancementType.TASK,
                    false,
                    false,
                    true
                )

                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion(
                    "impossible_${id}",
                    CriteriaTriggers.IMPOSSIBLE.createCriterion(ImpossibleTrigger.TriggerInstance())
                )

            if (parent != null) {
                advancement.parent(parent)
            }

            return advancement.save(consumer, "witchery:vampire/$id")
        }
    }

}