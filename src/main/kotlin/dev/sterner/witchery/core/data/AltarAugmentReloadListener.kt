package dev.sterner.witchery.core.data

import dev.sterner.witchery.content.block.ChaliceBlock
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.Optional

object AltarAugmentReloadListener {

    val LOADER = AltarAugmentResourceReloadListener(Gson(), "altar_augments")
    val AUGMENTS = mutableListOf<AltarAugment>()

    class AltarAugmentResourceReloadListener(gson: Gson, directory: String) :
        SimpleJsonResourceReloadListener(gson, directory) {

        override fun apply(
            objects: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            AUGMENTS.clear()

            objects.forEach { (file, element) ->
                try {
                    if (element.isJsonArray) {
                        element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                    } else if (element.isJsonObject) {
                        parseJson(element.asJsonObject, file)
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("Error parsing altar augment $file: ${e.message}", e)
                }
            }
        }

        private fun parseJson(json: JsonObject, file: ResourceLocation) {
            val data = AltarAugment.CODEC.decode(JsonOps.INSTANCE, json)
                .getOrThrow { IllegalArgumentException("Failed to decode altar augment: $it") }
                .first

            AUGMENTS.add(data)
        }
    }

    fun getAugment(state: BlockState): AltarAugment? {
        return AUGMENTS.firstOrNull { augment ->
            augment.matches(state)
        }
    }

    data class AltarAugment(
        val block: Optional<ResourceLocation>,
        val tag: Optional<ResourceLocation>,
        val requiresLit: Boolean,
        val requiredCandleCount: Optional<Int>,
        val requiresSoup: Boolean,
        val bonus: AugmentBonus,
        val category: AugmentCategory
    ) {
        fun matches(state: BlockState): Boolean {
            if (block.isPresent) {
                val expectedBlock = BuiltInRegistries.BLOCK.get(block.get())
                if (state.block != expectedBlock) return false
            }

            if (tag.isPresent) {
                val blockTag = TagKey.create(BuiltInRegistries.BLOCK.key(), tag.get())
                if (!state.`is`(blockTag)) return false
            }

            if (requiresLit) {
                if (!state.hasProperty(BlockStateProperties.LIT)) return false
                if (!state.getValue(BlockStateProperties.LIT)) return false
            }

            if (requiredCandleCount.isPresent) {
                if (!state.hasProperty(BlockStateProperties.CANDLES)) return false
                if (state.getValue(BlockStateProperties.CANDLES) != requiredCandleCount.get()) return false
            }

            if (requiresSoup) {
                if (!state.hasProperty(ChaliceBlock.HAS_SOUP)) return false
                if (!state.getValue(ChaliceBlock.HAS_SOUP)) return false
            }

            return true
        }

        companion object {
            val CODEC: Codec<AltarAugment> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("block").forGetter(AltarAugment::block),
                    ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(AltarAugment::tag),
                    Codec.BOOL.optionalFieldOf("requires_lit", false).forGetter(AltarAugment::requiresLit),
                    Codec.INT.optionalFieldOf("required_candle_count").forGetter(AltarAugment::requiredCandleCount),
                    Codec.BOOL.optionalFieldOf("requires_soup", false).forGetter(AltarAugment::requiresSoup),
                    AugmentBonus.CODEC.fieldOf("bonus").forGetter(AltarAugment::bonus),
                    AugmentCategory.CODEC.fieldOf("category").forGetter(AltarAugment::category)
                ).apply(instance, ::AltarAugment)
            }
        }
    }

    data class AugmentBonus(
        val lightBonus: Double,
        val headBonus: Double,
        val chaliceBonus: Double,
        val rangeMultiplier: Double,
        val hasPentacle: Boolean,
        val hasInfinityEgg: Boolean
    ) {
        companion object {
            val CODEC: Codec<AugmentBonus> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.DOUBLE.optionalFieldOf("light_bonus", 0.0).forGetter(AugmentBonus::lightBonus),
                    Codec.DOUBLE.optionalFieldOf("head_bonus", 0.0).forGetter(AugmentBonus::headBonus),
                    Codec.DOUBLE.optionalFieldOf("chalice_bonus", 0.0).forGetter(AugmentBonus::chaliceBonus),
                    Codec.DOUBLE.optionalFieldOf("range_multiplier", 1.0).forGetter(AugmentBonus::rangeMultiplier),
                    Codec.BOOL.optionalFieldOf("has_pentacle", false).forGetter(AugmentBonus::hasPentacle),
                    Codec.BOOL.optionalFieldOf("has_infinity_egg", false).forGetter(AugmentBonus::hasInfinityEgg)
                ).apply(instance, ::AugmentBonus)
            }
        }
    }

    enum class AugmentCategory {
        LIGHT,
        HEAD,
        CHALICE,
        RANGE,
        SPECIAL;

        companion object {
            val CODEC: Codec<AugmentCategory> = Codec.STRING.xmap(
                { name -> valueOf(name.uppercase()) },
                { it.name.lowercase() }
            )
        }
    }
}