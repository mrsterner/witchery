package dev.sterner.witchery.core.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.content.block.SuspiciousGraveyardDirtBlockEntity
import dev.sterner.witchery.content.block.altar.AltarBlockEntity
import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.content.block.arthana.ArthanaBlockEntity
import dev.sterner.witchery.content.block.bear_trap.BearTrapBlockEntity
import dev.sterner.witchery.content.block.blood_crucible.BloodCrucibleBlockEntity
import dev.sterner.witchery.content.block.blood_poppy.BloodPoppyBlockEntity
import dev.sterner.witchery.content.block.brazier.BrazierBlockEntity
import dev.sterner.witchery.content.block.cauldron.CauldronBlockEntity
import dev.sterner.witchery.content.block.censer.CenserBlockEntity
import dev.sterner.witchery.content.block.coffin.CoffinBlockEntity
import dev.sterner.witchery.content.block.critter_snare.CritterSnareBlockEntity
import dev.sterner.witchery.content.block.distillery.DistilleryBlockEntity
import dev.sterner.witchery.content.block.dream_weaver.DreamWeaverBlockEntity
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.content.block.grassper.GrassperBlockEntity
import dev.sterner.witchery.content.block.life_blood.LifeBloodBlockEntity
import dev.sterner.witchery.content.block.mushroom_log.MushroomLogBlockEntity
import dev.sterner.witchery.content.block.oven.OvenBlockEntity
import dev.sterner.witchery.content.block.oven.OvenFumeExtensionBlockEntity
import dev.sterner.witchery.content.block.phylactery.PhylacteryBlockEntity
import dev.sterner.witchery.content.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.content.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.content.block.signs.CustomHangingSignBE
import dev.sterner.witchery.content.block.signs.CustomSignBE
import dev.sterner.witchery.content.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.content.block.spining_wheel.SpinningWheelBlockEntity
import dev.sterner.witchery.content.block.spirit_portal.SpiritPortalBlockEntity
import dev.sterner.witchery.content.block.werewolf_altar.WerewolfAltarBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object WitcheryBlockEntityTypes {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Witchery.MODID)

    private fun <T : BlockEntity> reg(
        name: String,
        supplier: () -> BlockEntityType<T>
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        return BLOCK_ENTITY_TYPES.register(name, Supplier<BlockEntityType<T>> { supplier() })
    }

    val MULTI_BLOCK_COMPONENT = reg("multi_block_component") {
        BlockEntityType.Builder.of(
            { pos, state -> MultiBlockComponentBlockEntity(pos, state) },
            WitcheryBlocks.COMPONENT.get(),
            WitcheryBlocks.ALTAR_COMPONENT.get(),
            WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get(),
            WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get(),
            WitcheryBlocks.CAULDRON_COMPONENT.get(),
            WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get(),
            WitcheryBlocks.DISTILLERY_COMPONENT.get(),
            WitcheryBlocks.ANCIENT_TABLET_COMPONENT.get(),
            WitcheryBlocks.WEREWOLF_ALTAR_COMPONENT.get(),
            WitcheryBlocks.EFFIGY_COMPONENT.get(),
            WitcheryBlocks.MUSHROOM_LOG_COMPONENT.get()
        )
            .build(null)
    }

    val ALTAR = reg("altar") {
        BlockEntityType.Builder.of({ pos, state -> AltarBlockEntity(pos, state) }, WitcheryBlocks.ALTAR.get())
            .build(null)
    }

    val LIFE_BLOOD = reg("life_blood") {
        BlockEntityType.Builder.of({ pos, state -> LifeBloodBlockEntity(pos, state) }, WitcheryBlocks.LIFE_BLOOD.get(), WitcheryBlocks.LIFE_BLOOD_PLANT.get())
            .build(null)
    }


    val MUSHROOM_LOG = reg("mushroom_log") {
        BlockEntityType.Builder.of(
            { pos, state -> MushroomLogBlockEntity(pos, state) },
            WitcheryBlocks.MUSHROOM_LOG.get()
        )
            .build(null)
    }

    val BEAR_TRAP = reg("bear_trap") {
        BlockEntityType.Builder.of({ pos, state -> BearTrapBlockEntity(pos, state) }, WitcheryBlocks.BEAR_TRAP.get())
            .build(null)
    }

    val BLOOD_CRUCIBLE = reg("blood_crucible") {
        BlockEntityType.Builder.of(
            { pos, state -> BloodCrucibleBlockEntity(pos, state) },
            WitcheryBlocks.BLOOD_CRUCIBLE.get()
        )
            .build(null)
    }

    val CAULDRON = reg("cauldron") {
        BlockEntityType.Builder.of(
            { pos, state -> CauldronBlockEntity(pos, state) },
            WitcheryBlocks.CAULDRON.get(),
            WitcheryBlocks.COPPER_CAULDRON.get(),
            WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get()
        ).build(null)
    }

    val OVEN = reg("oven") {
        BlockEntityType.Builder.of(
            { pos, state -> OvenBlockEntity(pos, state) },
            WitcheryBlocks.IRON_WITCHES_OVEN.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get()
        ).build(null)
    }

    val OVEN_FUME_EXTENSION = reg("oven_fume_extension") {
        BlockEntityType.Builder.of(
            { pos, state -> OvenFumeExtensionBlockEntity(pos, state) },
            WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get()
        ).build(null)
    }

    val GOLDEN_CHALK = reg("golden_chalk") {
        BlockEntityType.Builder.of(
            { pos, state -> GoldenChalkBlockEntity(pos, state) },
            WitcheryBlocks.GOLDEN_CHALK_BLOCK.get()
        )
            .build(null)
    }

    val CUSTOM_SIGN = reg("custom_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_SIGN.get(),
            WitcheryBlocks.ALDER_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_SIGN.get(),
            WitcheryBlocks.HAWTHORN_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_SIGN.get()
        ).build(null)
    }

    val CUSTOM_HANGING_SIGN = reg("custom_hanging_sign") {
        BlockEntityType.Builder.of(
            { pos, state -> CustomHangingSignBE(pos, state) as SignBlockEntity },
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get()
        ).build(null)
    }

    val DISTILLERY = reg("distillery") {
        BlockEntityType.Builder.of({ pos, state -> DistilleryBlockEntity(pos, state) }, WitcheryBlocks.DISTILLERY.get())
            .build(null)
    }

    val WEREWOLF_ALTAR = reg("werewolf_altar") {
        BlockEntityType.Builder.of(
            { pos, state -> WerewolfAltarBlockEntity(pos, state) },
            WitcheryBlocks.WEREWOLF_ALTAR.get()
        )
            .build(null)
    }

    val BLOODY_POPPY = reg("blood_poppy") {
        BlockEntityType.Builder.of(::BloodPoppyBlockEntity, WitcheryBlocks.BLOOD_POPPY.get()).build(null)
    }

    val SPINNING_WHEEL = reg("spinning_wheel") {
        BlockEntityType.Builder.of(::SpinningWheelBlockEntity, WitcheryBlocks.SPINNING_WHEEL.get()).build(null)
    }

    val ARTHANA = reg("arthana") {
        BlockEntityType.Builder.of(::ArthanaBlockEntity, WitcheryBlocks.ARTHANA.get()).build(null)
    }

    val POPPET = reg("poppet") {
        BlockEntityType.Builder.of(::PoppetBlockEntity, WitcheryBlocks.POPPET.get()).build(null)
    }

    val SPIRIT_PORTAL = reg("spirit_portal") {
        BlockEntityType.Builder.of(::SpiritPortalBlockEntity, WitcheryBlocks.SPIRIT_PORTAL.get()).build(null)
    }

    val BRAZIER = reg("brazier") {
        BlockEntityType.Builder.of(::BrazierBlockEntity, WitcheryBlocks.BRAZIER.get()).build(null)
    }

    val DREAM_WEAVER = reg("dream_weaver") {
        BlockEntityType.Builder.of(
            ::DreamWeaverBlockEntity,
            WitcheryBlocks.DREAM_WEAVER.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_INTENSITY.get(),
            WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get()
        ).build(null)
    }

    val BRUSHABLE_BLOCK = reg("suspicious_graveyard_dirt") {
        BlockEntityType.Builder.of(
            { pos, state -> SuspiciousGraveyardDirtBlockEntity(pos, state) },
            WitcheryBlocks.SUSPICIOUS_GRAVEYARD_DIRT.get()
        ).build(null)
    }

    val SACRIFICIAL_CIRCLE = reg("sacrificial_circle") {
        BlockEntityType.Builder.of(
            { pos, state -> SacrificialBlockEntity(pos, state) },
            WitcheryBlocks.SACRIFICIAL_CIRCLE.get()
        ).build(null)
    }

    val GRASSPER = reg("grassper") {
        BlockEntityType.Builder.of(::GrassperBlockEntity, WitcheryBlocks.GRASSPER.get()).build(null)
    }

    val EFFIGY = reg("effigy") {
        BlockEntityType.Builder.of(
            ::EffigyBlockEntity,
            WitcheryBlocks.CLAY_EFFIGY.get(),
            WitcheryBlocks.SCARECROW.get(),
            WitcheryBlocks.WITCHS_LADDER.get()
        ).build(null)
    }

    val CRITTER_SNARE = reg("critter_snare") {
        BlockEntityType.Builder.of(::CritterSnareBlockEntity, WitcheryBlocks.CRITTER_SNARE.get()).build(null)
    }

    val COFFIN = reg("coffin") {
        BlockEntityType.Builder.of(::CoffinBlockEntity, WitcheryBlocks.COFFIN.get()).build(null)
    }

    val SOUL_CAGE = reg("soul_cage") {
        BlockEntityType.Builder.of(::SoulCageBlockEntity, WitcheryBlocks.SOUL_CAGE.get()).build(null)
    }

    val ANCIENT_SLATE = reg("ancient_slate") {
        BlockEntityType.Builder.of(::AncientTabletBlockEntity, WitcheryBlocks.ANCIENT_TABLET.get()).build(null)
    }

    val PHYLACTERY = reg("phylactery") {
        BlockEntityType.Builder.of(::PhylacteryBlockEntity, WitcheryBlocks.PHYLACTERY.get()).build(null)
    }

    val CENSER = reg("censer") {
        BlockEntityType.Builder.of(::CenserBlockEntity, WitcheryBlocks.CENSER.get()).build(null)
    }
}
