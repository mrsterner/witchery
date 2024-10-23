package dev.sterner.witchery.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.util.CodecUtils.CODEC
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block
import java.util.*

data class DynamicRitual(
    val ritual: Optional<Ritual>,
    val altarPower: Int,
    val blocks: Map<Char, Block>,
    val pattern: List<String>,
    val celestialConditions: List<Celestial> = listOf(),
    val inputItems: List<Ingredient> = listOf(),
    val inputEntities: List<EntityType<*>> = listOf(),
    val outputItems: List<Ingredient> = listOf(),
    val outputEntities: List<EntityType<*>> = listOf(),
    val isInfinite: Boolean = false,
    val commands: List<CommandType> = listOf(),
    val floatingOutput: Boolean = false,
    val ticks: Int = 0
) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Ritual.CODEC.optionalFieldOf("ritual").forGetter(DynamicRitual::ritual),
                Codec.INT.fieldOf("altarPower").forGetter(DynamicRitual::altarPower),
                Codec.unboundedMap(Char.CODEC, BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("blockMapping")
                    .forGetter(DynamicRitual::blocks),
                Codec.STRING.listOf().fieldOf("pattern").forGetter(DynamicRitual::pattern),
                Celestial.CELESTIAL_CODEC.listOf().optionalFieldOf("celestialConditions", listOf())
                    .forGetter(DynamicRitual::celestialConditions),
                Ingredient.CODEC.listOf().optionalFieldOf("inputItems", listOf())
                    .forGetter(DynamicRitual::inputItems),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().optionalFieldOf("inputEntities", listOf())
                    .forGetter(DynamicRitual::inputEntities),
                Ingredient.CODEC.listOf().optionalFieldOf("outputItems", listOf())
                    .forGetter(DynamicRitual::outputItems),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().optionalFieldOf("outputEntities", listOf())
                    .forGetter(DynamicRitual::outputEntities),
                Codec.BOOL.optionalFieldOf("isInfinite", false).forGetter(DynamicRitual::isInfinite),
                CommandType.CODEC.listOf().optionalFieldOf("commands", listOf())
                    .forGetter(DynamicRitual::commands),
                Codec.BOOL.optionalFieldOf("floatingOutput", false).forGetter(DynamicRitual::floatingOutput),
                Codec.INT.optionalFieldOf("ticks", 0).forGetter(DynamicRitual::ticks)
            ).apply(instance, ::DynamicRitual)
        }
    }
}