package dev.sterner.witchery.registry

import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.sterner.witchery.entity.*
import net.minecraft.world.entity.animal.Pig

object WitcheryEntityAttributes {
    fun register(){
        EntityAttributeRegistry.register(WitcheryEntityTypes.MANDRAKE, MandrakeEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.IMP, ImpEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.DEMON, DemonEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.OWL, OwlEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.ENT, EntEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.BANSHEE, BansheeEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.SPECTRE, SpectreEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.SPECTRAL_PIG, Pig::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.NIGHTMARE, NightmareEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.VAMPIRE, VampireEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.WEREWOLF, WerewolfEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.LILITH, LilithEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.COVEN_WITCH, CovenWitchEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.ELLE, ElleEntity::createAttributes)
        EntityAttributeRegistry.register(WitcheryEntityTypes.PARASITIC_LOUSE, ParasiticLouseEntity::createAttributes)
    }
}