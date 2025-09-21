package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.critter_snare.CritterSnareBlock
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.alchemy.PotionContents
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.*
import java.util.function.Supplier


object WitcheryDataComponents {

    val DATA: DeferredRegister<DataComponentType<*>> =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Witchery.MODID)

    val GLOBAL_POS_COMPONENT = DATA.register("global_pos", Supplier {
        DataComponentType.builder<GlobalPos>().persistent(GlobalPos.CODEC).build()
    })

    val ENTITY_ID_COMPONENT = DATA.register("entity_uuid", Supplier {
        DataComponentType.builder<String>().persistent(Codec.STRING).build()
    })

    val ENTITY_NAME_COMPONENT = DATA.register("entity_name", Supplier {
        DataComponentType.builder<String>().persistent(Codec.STRING).build()
    })

    val EXPIRED_TAGLOCK = DATA.register("expired_taglock", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val TIMESTAMP = DATA.register("timestamp", Supplier {
        DataComponentType.builder<Long>().persistent(Codec.LONG).build()
    })

    val ATTUNED = DATA.register("attuned", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val HAS_SOUP = DATA.register("has_soup", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val HAS_OINTMENT = DATA.register("has_ointment", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val PLAYER_UUID = DATA.register("player_uuid", Supplier {
        DataComponentType.builder<UUID>().persistent(UUIDUtil.CODEC).build()
    })

    val BLOOD = DATA.register("blood", Supplier {
        DataComponentType.builder<UUID>().persistent(UUIDUtil.CODEC).build()
    })

    val VAMPIRE_BLOOD = DATA.register("vampire_blood", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val CHICKEN_BLOOD = DATA.register("chicken_blood", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val HAS_SUN = DATA.register("has_sun", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val CANE_BLOOD_AMOUNT = DATA.register("cane_blood_amount", Supplier {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    })

    val UNSHEETED = DATA.register("unsheeted", Supplier {
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build()
    })

    val DUAL_POTION_CONTENT = DATA.register("dual_potion_content", Supplier {
        DataComponentType.builder<DualPotionContents>().persistent(DualPotionContents.CODEC).build()
    })

    val LEECH_EFFECT = DATA.register("leech_effect", Supplier {
        DataComponentType.builder<MobEffectInstance>().persistent(MobEffectInstance.CODEC).build()
    })

    val BANSHEE_COUNT = DATA.register("banshee_count", Supplier {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    })

    val SPECTRE_COUNT = DATA.register("spectre_count", Supplier {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    })

    val POLTERGEIST_COUNT = DATA.register("poltergeist_count", Supplier {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    })

    val SPIRIT_COUNT = DATA.register("spirit_count", Supplier {
        DataComponentType.builder<Int>().persistent(Codec.INT).build()
    })

    val CAPTURED_ENTITY = DATA.register("captured_entity", Supplier {
        DataComponentType.builder<CritterSnareBlock.CapturedEntity>().persistent(CritterSnareBlock.CapturedEntity.CODEC)
            .build()
    })

    val WITCHERY_POTION_CONTENT = DATA.register("witchery_potion_content", Supplier {
        DataComponentType.builder<List<WitcheryPotionIngredient>>()
            .persistent(WitcheryPotionIngredient.CODEC.listOf())
            .build()
    })

    data class DualPotionContents(
        val positive: Optional<PotionContents>,
        val negative: Optional<PotionContents>
    ) {
        companion object {
            val CODEC: Codec<DualPotionContents> = RecordCodecBuilder.create { instance ->
                instance.group(
                    PotionContents.CODEC.optionalFieldOf("positive").forGetter { it.positive },
                    PotionContents.CODEC.optionalFieldOf("negative").forGetter { it.negative }
                ).apply(instance, ::DualPotionContents)
            }
        }
    }
}