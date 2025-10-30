package dev.sterner.witchery.data_gen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.common.data.ExistingFileHelper
import tallestegg.guardvillagers.GuardEntityType
import java.util.concurrent.CompletableFuture

class WitcheryEntityTypeTagProvider(
    output: PackOutput?,
    completableFuture: CompletableFuture<HolderLookup.Provider>?,
    existingFileHelper: ExistingFileHelper
) : EntityTypeTagsProvider(output, completableFuture, Witchery.MODID, existingFileHelper) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        tag(WitcheryTags.POSSESSABLE)
            .add(EntityType.ZOMBIE)
            .add(EntityType.ZOMBIE_VILLAGER)
            .add(EntityType.SKELETON)
            .add(EntityType.STRAY)
            .add(EntityType.HUSK)

        tag(EntityTypeTags.DISMOUNTS_UNDERWATER).add(
            WitcheryEntityTypes.CUSTOM_BOAT.get(),
            WitcheryEntityTypes.CUSTOM_CHEST_BOAT.get()
        )

        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
            .add(WitcheryEntityTypes.NIGHTMARE.get())
            .add(WitcheryEntityTypes.VAMPIRE.get())
            .add(WitcheryEntityTypes.DEATH.get())
            .add(WitcheryEntityTypes.BANSHEE.get())
            .add(WitcheryEntityTypes.SPECTRE.get())
            .add(WitcheryEntityTypes.SPECTRAL_PIG.get())

        tag(WitcheryTags.NECROMANCER_SUMMONABLE)
            .add(EntityType.ZOMBIE)
            .add(EntityType.ZOMBIE_HORSE)
            .add(EntityType.ZOMBIE_VILLAGER)
            .add(EntityType.ZOMBIFIED_PIGLIN)
            .add(EntityType.SKELETON)
            .add(EntityType.SKELETON_HORSE)
            .add(EntityType.WITHER_SKELETON)
            .add(EntityType.VILLAGER)
            .add(EntityType.PILLAGER)
            .addOptional(GuardEntityType.GUARD.id)

        tag(WitcheryTags.SCARED_BY_GROTESQUE)
            .add(EntityType.ZOMBIE)
            .add(EntityType.CREEPER)
            .add(EntityType.SKELETON)
            .add(EntityType.VILLAGER)
            .add(EntityType.COW)
            .add(EntityType.PIG)
            .add(EntityType.SHEEP)
            .add(EntityType.CHICKEN)
            .add(EntityType.ZOMBIE_VILLAGER)
            .add(EntityType.ZOMBIFIED_PIGLIN)

        tag(WitcheryTags.BESTIAL_CALL_BLACKLIST)
            .add(EntityType.SNIFFER)
            .add(EntityType.ZOMBIE_HORSE)
            .add(EntityType.HOGLIN)
            .add(EntityType.STRIDER)
    }
}