package dev.sterner.witchery.content.recipe.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.block.ritual.CommandType
import dev.sterner.witchery.content.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.core.registry.WitcheryRitualRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class RitualRecipe(
    val ritualType: Ritual?,
    val inputItems: List<ItemStack>,
    val inputEntities: List<EntityType<*>>,
    val outputItems: List<ItemStack>,
    val outputEntities: List<EntityType<*>>,
    val altarPower: Int,
    val altarPowerPerSecond: Int = 0,
    val covenCount: Int,
    val commands: Set<CommandType>,
    val isInfinite: Boolean,
    val floatingItemOutput: Boolean,
    val ticks: Int,
    val pattern: List<String>,
    val blockMapping: Map<Char, Block>,
    val conditions: RitualConditions
) : Recipe<MultipleItemRecipeInput> {

    val celestialConditions: Set<Celestial>
        get() = conditions.celestialConditions

    val weather: Set<Weather>
        get() = conditions.weather

    val requireCat: Boolean
        get() = conditions.requireCat

    val ritualData: CompoundTag
        get() = conditions.ritualData

    override fun matches(input: MultipleItemRecipeInput, level: Level): Boolean {
        return true
    }

    override fun assemble(input: MultipleItemRecipeInput, registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.RITUAL_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<RitualRecipe> {
        override fun codec(): MapCodec<RitualRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> {
            return STREAM_CODEC
        }

        companion object {

            private val COMMANDS_SET_CODEC: Codec<Set<CommandType>> = CommandType.CODEC.listOf().xmap(
                { it.toSet() },
                { it.toList() }
            )

            val CODEC: MapCodec<RitualRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<RitualRecipe> ->
                    obj.group(
                        WitcheryRitualRegistry.CODEC.fieldOf("ritual").forGetter { it.ritualType },
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.listOf().fieldOf("inputItems").forGetter { it.inputItems },
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().orElse(listOf()).fieldOf("inputEntities")
                            .forGetter { it.inputEntities },
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.listOf().orElse(listOf()).fieldOf("outputItems")
                            .forGetter { it.outputItems },
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().orElse(listOf()).fieldOf("outputEntities")
                            .forGetter { it.outputEntities },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                        Codec.INT.fieldOf("altarPowerPerTick").orElse(0).forGetter { recipe -> recipe.altarPowerPerSecond },
                        Codec.INT.fieldOf("covenCount").forGetter { recipe -> recipe.covenCount },
                        COMMANDS_SET_CODEC.fieldOf("commands").orElse(setOf(CommandType.DEFAULT))
                            .forGetter { recipe -> recipe.commands },
                        Codec.BOOL.fieldOf("isInfinite").orElse(false).forGetter { recipe -> recipe.isInfinite },
                        Codec.BOOL.fieldOf("floatingItemOutput").orElse(false)
                            .forGetter { recipe -> recipe.floatingItemOutput },
                        Codec.INT.fieldOf("ticks").orElse(0).forGetter { recipe -> recipe.ticks },
                        Codec.STRING.listOf().fieldOf("pattern").forGetter { recipe -> recipe.pattern },
                        Codec.unboundedMap(SYMBOL_CODEC, BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("blockMapping")
                            .forGetter { recipe -> recipe.blockMapping },
                        RitualConditions.CODEC.fieldOf("conditions").orElse(RitualConditions())
                            .forGetter { recipe -> recipe.conditions }
                    ).apply(obj, ::RitualRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "ritual"

        val SYMBOL_CODEC: Codec<Char> = Codec.STRING.comapFlatMap(
            { string: String ->
                if (string.length != 1) {
                    return@comapFlatMap DataResult.error<Char> { "Invalid key entry: '$string' is an invalid symbol (must be 1 character only)." }
                } else {
                    return@comapFlatMap if (" " == string) DataResult.error<Char> { "Invalid key entry: ' ' is a reserved symbol." } else DataResult.success<Char>(
                        string[0]
                    )
                }
            },
            { obj: Char? -> java.lang.String.valueOf(obj) })
    }

    enum class Celestial : StringRepresentable {
        DAY,
        NIGHT,
        FULL_MOON,
        NEW_MOON,
        WAXING,
        WANING;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val CELESTIAL_CODEC: Codec<Celestial> = StringRepresentable.fromEnum(Celestial::values)

            val CELESTIAL_SET_CODEC: Codec<Set<Celestial>> = CELESTIAL_CODEC.listOf().xmap(
                { it.toSet() },
                { it.toList() }
            )
        }
    }

    enum class Weather : StringRepresentable {
        CLEAR,
        RAIN,
        STORM;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val WEATHER_CODEC: Codec<Weather> = StringRepresentable.fromEnum(Weather::values)

            val WEATHER_SET_CODEC: Codec<Set<Weather>> = WEATHER_CODEC.listOf().xmap(
                { it.toSet() },
                { it.toList() }
            )
        }
    }
}