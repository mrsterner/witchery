package dev.sterner.witchery.core.registry

import com.google.common.collect.ImmutableSet
import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object WitcheryVillagers {

    val POIS: DeferredRegister<PoiType> = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Witchery.MODID)
    val PROFESSIONS: DeferredRegister<VillagerProfession> = DeferredRegister.create(Registries.VILLAGER_PROFESSION, Witchery.MODID)


    val FORTUNE_TELLER_POI = POIS.register("fortune_teller_poi", Supplier {
        PoiType(setOf(WitcheryBlocks.CRYSTAL_BALL.get().defaultBlockState()), 1, 1)
    })

    val FORTUNE_TELLER_PROFESSION = PROFESSIONS.register("fortune_teller", Supplier {
        VillagerProfession(
            "fortune_teller",
            { holder -> holder.`is`(FORTUNE_TELLER_POI.key) },
            { holder -> holder.`is`(FORTUNE_TELLER_POI.key) },
            ImmutableSet.of(),
            ImmutableSet.of(),
            SoundEvents.VILLAGER_WORK_CLERIC
        )
    })
}