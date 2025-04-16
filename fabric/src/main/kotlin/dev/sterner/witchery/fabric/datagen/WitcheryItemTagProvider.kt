package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class WitcheryItemTagProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.ItemTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {

        getOrCreateTagBuilder(WitcheryTags.WEREWOLF_ALTAR_ITEM)
            .add(Items.GOLD_INGOT)
            .add(Items.MUTTON)

        getOrCreateTagBuilder(WitcheryTags.PLACEABLE_POPPETS)
            .add(WitcheryItems.VAMPIRIC_POPPET.get())
            .add(WitcheryItems.ARMOR_PROTECTION_POPPET.get())
            .add(WitcheryItems.HUNGER_PROTECTION_POPPET.get())
            .add(WitcheryItems.VOODOO_PROTECTION_POPPET.get())
            .add(WitcheryItems.VOODOO_POPPET.get())

        getOrCreateTagBuilder(WitcheryTags.SPIRIT_WORLD_TRANSFERABLE)
            .add(WitcheryItems.ICY_NEEDLE.get())
            .add(WitcheryItems.MELLIFLUOUS_HUNGER.get())
            .add(WitcheryItems.WISPY_COTTON.get())
            .add(WitcheryItems.DISTURBED_COTTON.get())
            .add(WitcheryItems.BREW_FLOWING_SPIRIT.get())

        getOrCreateTagBuilder(WitcheryTags.LEAF_ITEMS).add(
            WitcheryItems.ROWAN_LEAVES.get(),
            WitcheryItems.ROWAN_BERRY_LEAVES.get(),
            WitcheryItems.ALDER_LEAVES.get(),
            WitcheryItems.HAWTHORN_LEAVES.get()
        )

        getOrCreateTagBuilder(WitcheryTags.ROWAN_LOG_ITEMS).add(
            WitcheryItems.ROWAN_LOG.get(),
            WitcheryItems.ROWAN_WOOD.get(),
            WitcheryItems.STRIPPED_ROWAN_LOG.get(),
            WitcheryItems.STRIPPED_ROWAN_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.ALDER_LOG_ITEMS).add(
            WitcheryItems.ALDER_LOG.get(),
            WitcheryItems.ALDER_WOOD.get(),
            WitcheryItems.STRIPPED_ALDER_LOG.get(),
            WitcheryItems.STRIPPED_ALDER_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.HAWTHORN_LOG_ITEMS).add(
            WitcheryItems.HAWTHORN_LOG.get(),
            WitcheryItems.HAWTHORN_WOOD.get(),
            WitcheryItems.STRIPPED_HAWTHORN_LOG.get(),
            WitcheryItems.STRIPPED_HAWTHORN_WOOD.get()
        )

        getOrCreateTagBuilder(WitcheryTags.CANDELABRA_ITEMS).add(
            WitcheryItems.IRON_CANDELABRA.get(),
            WitcheryItems.WHITE_IRON_CANDELABRA.get(),
            WitcheryItems.ORANGE_IRON_CANDELABRA.get(),
            WitcheryItems.MAGENTA_IRON_CANDELABRA.get(),
            WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.get(),
            WitcheryItems.YELLOW_IRON_CANDELABRA.get(),
            WitcheryItems.LIME_IRON_CANDELABRA.get(),
            WitcheryItems.PINK_IRON_CANDELABRA.get(),
            WitcheryItems.GRAY_IRON_CANDELABRA.get(),
            WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.get(),
            WitcheryItems.CYAN_IRON_CANDELABRA.get(),
            WitcheryItems.PURPLE_IRON_CANDELABRA.get(),
            WitcheryItems.BLUE_IRON_CANDELABRA.get(),
            WitcheryItems.BROWN_IRON_CANDELABRA.get(),
            WitcheryItems.GREEN_IRON_CANDELABRA.get(),
            WitcheryItems.RED_IRON_CANDELABRA.get(),
            WitcheryItems.BLACK_IRON_CANDELABRA.get()
        )

        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
            .addTag(WitcheryTags.ROWAN_LOG_ITEMS)
            .addTag(WitcheryTags.ALDER_LOG_ITEMS)
            .addTag(WitcheryTags.HAWTHORN_LOG_ITEMS)

        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES).add(
            WitcheryItems.ROWAN_FENCE.get(),
            WitcheryItems.ALDER_FENCE.get(),
            WitcheryItems.HAWTHORN_FENCE.get()
        )

        getOrCreateTagBuilder(ItemTags.FENCE_GATES).add(
            WitcheryItems.ROWAN_FENCE_GATE.get(),
            WitcheryItems.ALDER_FENCE_GATE.get(),
            WitcheryItems.HAWTHORN_FENCE_GATE.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES).add(
            WitcheryItems.ROWAN_PRESSURE_PLATE.get(),
            WitcheryItems.ALDER_PRESSURE_PLATE.get(),
            WitcheryItems.HAWTHORN_PRESSURE_PLATE.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS).add(
            WitcheryItems.ROWAN_SLAB.get(),
            WitcheryItems.ALDER_SLAB.get(),
            WitcheryItems.HAWTHORN_SLAB.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS).add(
            WitcheryItems.ROWAN_STAIRS.get(),
            WitcheryItems.ALDER_STAIRS.get(),
            WitcheryItems.HAWTHORN_STAIRS.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS).add(
            WitcheryItems.ROWAN_BUTTON.get(),
            WitcheryItems.ALDER_BUTTON.get(),
            WitcheryItems.HAWTHORN_BUTTON.get()
        )

        getOrCreateTagBuilder(ItemTags.PLANKS).add(
            WitcheryItems.ROWAN_PLANKS.get(),
            WitcheryItems.ALDER_PLANKS.get(),
            WitcheryItems.HAWTHORN_PLANKS.get()
        )

        getOrCreateTagBuilder(ItemTags.LEAVES)
            .addTag(WitcheryTags.LEAF_ITEMS)

        getOrCreateTagBuilder(ItemTags.WOODEN_DOORS).add(
            WitcheryItems.ROWAN_DOOR.get(),
            WitcheryItems.ALDER_DOOR.get(),
            WitcheryItems.HAWTHORN_DOOR.get()
        )

        getOrCreateTagBuilder(ItemTags.WOODEN_TRAPDOORS).add(
            WitcheryItems.ROWAN_TRAPDOOR.get(),
            WitcheryItems.ALDER_TRAPDOOR.get(),
            WitcheryItems.HAWTHORN_TRAPDOOR.get()
        )

        getOrCreateTagBuilder(ItemTags.SAPLINGS).add(
            WitcheryItems.ROWAN_SAPLING.get(),
            WitcheryItems.ALDER_SAPLING.get(),
            WitcheryItems.HAWTHORN_SAPLING.get()
        )

        getOrCreateTagBuilder(ItemTags.SIGNS).add(
            WitcheryItems.ROWAN_SIGN.get(),
            WitcheryItems.ALDER_SIGN.get(),
            WitcheryItems.HAWTHORN_SIGN.get()
        )

        getOrCreateTagBuilder(ItemTags.HANGING_SIGNS).add(
            WitcheryItems.ROWAN_HANGING_SIGN.get(),
            WitcheryItems.ALDER_HANGING_SIGN.get(),
            WitcheryItems.HAWTHORN_HANGING_SIGN.get()
        )

        getOrCreateTagBuilder(ItemTags.BOATS).add(
            WitcheryItems.ROWAN_BOAT.get(),
            WitcheryItems.ALDER_BOAT.get(),
            WitcheryItems.HAWTHORN_BOAT.get()
        )

        getOrCreateTagBuilder(ItemTags.CHEST_BOATS).add(
            WitcheryItems.ROWAN_CHEST_BOAT.get(),
            WitcheryItems.ALDER_CHEST_BOAT.get(),
            WitcheryItems.HAWTHORN_CHEST_BOAT.get()
        )

        getOrCreateTagBuilder(ItemTags.DYEABLE).add(
            WitcheryItems.IRON_CANDELABRA.get()
        )

        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.GARLIC.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(ItemTags.CHICKEN_FOOD).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS).add(
            WitcheryItems.GUIDEBOOK.get()
        )

        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS).add(
            WitcheryItems.GUIDEBOOK.get()
        )

        getOrCreateTagBuilder(ItemTags.PARROT_FOOD).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(ItemTags.FOX_FOOD)
            .add(WitcheryItems.ROWAN_BERRIES.get())

        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED).add(
            WitcheryItems.GOLDEN_CHALK.get(),
            WitcheryItems.ARTHANA.get(),
            WitcheryItems.CHALICE.get(),
            WitcheryItems.GOLDEN_THREAD.get()
        )

        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE)
            .add(WitcheryItems.ARTHANA.get())

        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE).add(
            WitcheryItems.ARTHANA.get(),
            WitcheryItems.ICY_NEEDLE.get(),
            WitcheryItems.BONE_NEEDLE.get(),
            WitcheryItems.RITUAL_CHALK.get(),
            WitcheryItems.GOLDEN_CHALK.get(),
            WitcheryItems.INFERNAL_CHALK.get(),
            WitcheryItems.OTHERWHERE_CHALK.get()
        )

        getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE)
            .add(WitcheryItems.ARTHANA.get())

        getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE)
            .add(WitcheryItems.ARTHANA.get())

        getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE)
            .add(WitcheryItems.ARTHANA.get())

        getOrCreateTagBuilder(ItemTags.BEE_FOOD)
            .add(WitcheryItems.BLOOD_POPPY.get())

        getOrCreateTagBuilder(ItemTags.SMALL_FLOWERS)
            .add(WitcheryItems.BLOOD_POPPY.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"))).add(
            WitcheryItems.MANDRAKE_SEEDS.get(),
            WitcheryItems.BELLADONNA_SEEDS.get(),
            WitcheryItems.SNOWBELL_SEEDS.get(),
            WitcheryItems.WATER_ARTICHOKE_SEEDS.get(),
            WitcheryItems.WOLFSBANE_SEEDS.get(),
            WitcheryItems.WORMWOOD_SEEDS.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops"))).add(
            WitcheryItems.MANDRAKE_ROOT.get(),
            WitcheryItems.BELLADONNA_FLOWER.get(),
            WitcheryItems.WATER_ARTICHOKE_GLOBE.get(),
            WitcheryItems.GARLIC.get(),
            WitcheryItems.WOLFSBANE.get(),
            WitcheryItems.WORMWOOD.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts"))).add(
            WitcheryItems.WOOD_ASH.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools"))).add(
            WitcheryItems.BONE_NEEDLE.get(),
            WitcheryItems.ICY_NEEDLE.get(),
            WitcheryItems.WAYSTONE.get(),
            WitcheryItems.TAGLOCK.get(),
            WitcheryItems.GOLDEN_CHALK.get(),
            WitcheryItems.RITUAL_CHALK.get(),
            WitcheryItems.INFERNAL_CHALK.get(),
            WitcheryItems.OTHERWHERE_CHALK.get(),
            WitcheryItems.ARTHANA.get()
        )

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/white")))
            .add(WitcheryItems.WHITE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/orange")))
            .add(WitcheryItems.ORANGE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(
            TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "dyed/magenta")
            )
        )
            .add(WitcheryItems.MAGENTA_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(
            TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "dyed/light_blue")
            )
        )
            .add(WitcheryItems.LIGHT_BLUE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/yellow")))
            .add(WitcheryItems.YELLOW_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/lime")))
            .add(WitcheryItems.LIME_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/pink")))
            .add(WitcheryItems.PINK_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/gray")))
            .add(WitcheryItems.GRAY_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(
            TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "dyed/light_gray")
            )
        )
            .add(WitcheryItems.LIGHT_GRAY_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/cyan")))
            .add(WitcheryItems.CYAN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/purple")))
            .add(WitcheryItems.PURPLE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/blue")))
            .add(WitcheryItems.BLUE_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/brown")))
            .add(WitcheryItems.BROWN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/green")))
            .add(WitcheryItems.GREEN_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/red")))
            .add(WitcheryItems.RED_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/black")))
            .add(WitcheryItems.BLACK_IRON_CANDELABRA.get())

        getOrCreateTagBuilder(WitcheryTags.WOODEN_WEAPONS)
            .add(Items.WOODEN_AXE)
            .add(Items.WOODEN_SWORD)
            .add(Items.STICK)
            .add(Items.FISHING_ROD)
            .add(Items.WOODEN_PICKAXE)
            .add(Items.WOODEN_HOE)
            .add(Items.WOODEN_SHOVEL)
    }
}