package dev.sterner.witchery.registry

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.sterner.witchery.client.model.AltarBlockEntityModel
import dev.sterner.witchery.client.model.AltarClothBlockEntityModel
import dev.sterner.witchery.client.model.BabaYagaEntityModel
import dev.sterner.witchery.client.model.BansheeEntityModel
import dev.sterner.witchery.client.model.BearTrapModel
import dev.sterner.witchery.client.model.BloodCrucibleModel
import dev.sterner.witchery.client.model.BoatModels
import dev.sterner.witchery.client.model.BroomEntityModel
import dev.sterner.witchery.client.model.ChainModel
import dev.sterner.witchery.client.model.CoffinModel
import dev.sterner.witchery.client.model.DeathEntityModel
import dev.sterner.witchery.client.model.DemonEntityModel
import dev.sterner.witchery.client.model.DistilleryGemModel
import dev.sterner.witchery.client.model.DreamWeaverBlockEntityModel
import dev.sterner.witchery.client.model.EntEntityModel
import dev.sterner.witchery.client.model.GlassContainerModel
import dev.sterner.witchery.client.model.HornedHuntsmanModel
import dev.sterner.witchery.client.model.HunterArmorModel
import dev.sterner.witchery.client.model.HuntsmanSpearModel
import dev.sterner.witchery.client.model.ImpEntityModel
import dev.sterner.witchery.client.model.JarModel
import dev.sterner.witchery.client.model.LilithEntityModel
import dev.sterner.witchery.client.model.MandrakeEntityModel
import dev.sterner.witchery.client.model.MushroomLogModel
import dev.sterner.witchery.client.model.NightmareEntityModel
import dev.sterner.witchery.client.model.OwlEntityModel
import dev.sterner.witchery.client.model.ParasiticLouseEntityModel
import dev.sterner.witchery.client.model.PhylacteryEtherCoreModel
import dev.sterner.witchery.client.model.PhylacteryEtherModel
import dev.sterner.witchery.client.model.SpectreEntityModel
import dev.sterner.witchery.client.model.SpinningWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpinningWheelWheelBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalBlockEntityModel
import dev.sterner.witchery.client.model.SpiritPortalPortalModel
import dev.sterner.witchery.client.model.VampireArmorModel
import dev.sterner.witchery.client.model.VampireEntityModel
import dev.sterner.witchery.client.model.WerewolfAltarModel
import dev.sterner.witchery.client.model.WerewolfEntityModel
import dev.sterner.witchery.client.model.WitchesRobesModel
import dev.sterner.witchery.client.model.poppet.ArmorPoppetModel
import dev.sterner.witchery.client.model.poppet.HungerPoppetModel
import dev.sterner.witchery.client.model.poppet.VampiricPoppetModel
import dev.sterner.witchery.client.model.poppet.VoodooPoppetModel
import net.minecraft.client.model.BoatModel
import net.minecraft.client.model.BoatModel.createBodyModel
import net.minecraft.client.model.ChestBoatModel
import net.minecraft.client.model.ChestBoatModel.createBodyModel

object WitcheryModelLayers {
    fun register() {
        EntityModelLayerRegistry.register(AltarClothBlockEntityModel.LAYER_LOCATION) { AltarClothBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(AltarBlockEntityModel.LAYER_LOCATION) { AltarBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(PhylacteryEtherModel.LAYER_LOCATION) { PhylacteryEtherModel.createBodyLayer() }
        EntityModelLayerRegistry.register(PhylacteryEtherCoreModel.LAYER_LOCATION) { PhylacteryEtherCoreModel.createBodyLayer() }
        EntityModelLayerRegistry.register(MushroomLogModel.LAYER_LOCATION) { MushroomLogModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalBlockEntityModel.LAYER_LOCATION) { SpiritPortalBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpiritPortalPortalModel.LAYER_LOCATION) { SpiritPortalPortalModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WerewolfAltarModel.LAYER_LOCATION) { WerewolfAltarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(CoffinModel.LAYER_LOCATION) { CoffinModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BearTrapModel.LAYER_LOCATION) { BearTrapModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HuntsmanSpearModel.LAYER_LOCATION) { HuntsmanSpearModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ChainModel.LAYER_LOCATION) { ChainModel.createBodyLayer() }
        EntityModelLayerRegistry.register(JarModel.LAYER_LOCATION) { JarModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ArmorPoppetModel.LAYER_LOCATION) { ArmorPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HungerPoppetModel.LAYER_LOCATION) { HungerPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampiricPoppetModel.LAYER_LOCATION) { VampiricPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VoodooPoppetModel.LAYER_LOCATION) { VoodooPoppetModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WitchesRobesModel.LAYER_LOCATION) { WitchesRobesModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampireArmorModel.LAYER_LOCATION) { VampireArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HunterArmorModel.LAYER_LOCATION) { HunterArmorModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpinningWheelBlockEntityModel.LAYER_LOCATION) { SpinningWheelBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BloodCrucibleModel.LAYER_LOCATION) { BloodCrucibleModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DistilleryGemModel.LAYER_LOCATION) { DistilleryGemModel.createBodyLayer() }
        EntityModelLayerRegistry.register(GlassContainerModel.LAYER_LOCATION) { GlassContainerModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BroomEntityModel.LAYER_LOCATION) { BroomEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DreamWeaverBlockEntityModel.LAYER_LOCATION) { DreamWeaverBlockEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(MandrakeEntityModel.LAYER_LOCATION) { MandrakeEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ImpEntityModel.LAYER_LOCATION) { ImpEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(OwlEntityModel.LAYER_LOCATION) { OwlEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DemonEntityModel.LAYER_LOCATION) { DemonEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(EntEntityModel.LAYER_LOCATION) { EntEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BansheeEntityModel.LAYER_LOCATION) { BansheeEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(HornedHuntsmanModel.LAYER_LOCATION) { HornedHuntsmanModel.createBodyLayer() }
        EntityModelLayerRegistry.register(DeathEntityModel.LAYER_LOCATION) { DeathEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(SpectreEntityModel.LAYER_LOCATION) { SpectreEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BabaYagaEntityModel.LAYER_LOCATION) { BabaYagaEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(ParasiticLouseEntityModel.LAYER_LOCATION) { ParasiticLouseEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(WerewolfEntityModel.LAYER_LOCATION) { WerewolfEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(VampireEntityModel.LAYER_LOCATION) { VampireEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(NightmareEntityModel.LAYER_LOCATION) { NightmareEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(LilithEntityModel.LAYER_LOCATION) { LilithEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.register(BoatModels.ROWAN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_BOAT_LAYER, BoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ROWAN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.ALDER_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)
        EntityModelLayerRegistry.register(BoatModels.HAWTHORN_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel)

    }


}