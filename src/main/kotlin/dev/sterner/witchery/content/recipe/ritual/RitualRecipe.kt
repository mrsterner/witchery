package dev.sterner.witchery.content.recipe.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.block.ritual.CommandType
import dev.sterner.witchery.content.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRitualRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
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
    val covenCount: Int,
    val commands: Set<CommandType>,
    val isInfinite: Boolean,
    val floatingItemOutput: Boolean,
    val ticks: Int,
    val pattern: List<String>,
    val blockMapping: Map<Char, Block>,
    val celestialConditions: Set<Celestial>,
    val weather: Set<Weather>,
    val requireCat: Boolean
) : Recipe<MultipleItemRecipeInput> {

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

            private val COMMAND_TYPE_CODEC: Codec<CommandType> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.STRING.fieldOf("command").forGetter(CommandType::command),
                    Codec.STRING.fieldOf("type").forGetter(CommandType::type),
                ).apply(instance, ::CommandType)
            }

            private val COMMANDS_SET_CODEC: Codec<Set<CommandType>> = COMMAND_TYPE_CODEC.listOf().xmap(
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
                        Celestial.CELESTIAL_SET_CODEC.fieldOf("celestialConditions").orElse(setOf())
                            .forGetter { recipe -> recipe.celestialConditions },
                        Weather.WEATHER_SET_CODEC.fieldOf("weather").orElse(setOf())
                            .forGetter { recipe -> recipe.weather },
                        Codec.BOOL.fieldOf("requireCat").forGetter { it.requireCat }


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

        fun fromNbt(tag: CompoundTag, registries: HolderLookup.Provider): RitualRecipe {
            val ritualType =
                WitcheryRitualRegistry.RITUAL_REGISTRY.get(ResourceLocation.tryParse(tag.getString("id")))

            val inputItems = if (tag.contains("inputItems")) {
                tag.getList("inputItems", 10).map { ItemStack.parse(registries, it as CompoundTag).get() }
            } else {
                emptyList()
            }

            val inputEntities = if (tag.contains("inputEntities")) {
                tag.getList("inputEntities", 8).map { EntityType.byString(it.asString).orElse(null)!! }
            } else {
                emptyList()
            }

            val outputItems = if (tag.contains("outputItems")) {
                tag.getList("outputItems", 10).map { ItemStack.parse(registries, it as CompoundTag).get() }
            } else {
                emptyList()
            }

            val outputEntities = if (tag.contains("outputEntities")) {
                tag.getList("outputEntities", 8).map { EntityType.byString(it.asString).orElse(null)!! }
            } else {
                emptyList()
            }

            val altarPower = if (tag.contains("altarPower")) tag.getInt("altarPower") else 0
            val covenCount = if (tag.contains("covenCount")) tag.getInt("covenCount") else 0

            val commands = tag.getList("commands", 10).mapNotNull { commandElement ->
                val commandTag = commandElement as CompoundTag
                val command = commandTag.getString("command")
                val commandType = commandTag.getString("type")

                if (command.isNotBlank() && commandType.isNotBlank()) {
                    CommandType(command, commandType)
                } else {
                    null
                }
            }.toSet()

            val isInfinite = if (tag.contains("isInfinite")) tag.getBoolean("isInfinite") else false
            val floatingItemOutput =
                if (tag.contains("floatingItemOutput")) tag.getBoolean("floatingItemOutput") else false

            val ticks = if (tag.contains("ticks")) tag.getInt("ticks") else 0

            val pattern = if (tag.contains("pattern")) {
                tag.getList("pattern", 8).map { it.asString }
            } else {
                emptyList<String>()
            }

            // Load block mapping (optional)
            val blockMapping = if (tag.contains("blockMapping")) {
                tag.getCompound("blockMapping").allKeys.associate { key ->
                    key[0] to BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString(key)))
                }
            } else {
                emptyMap()
            }

            val celestialConditions = if (tag.contains("celestialConditions")) {
                tag.getList("celestialConditions", 8).mapNotNull {
                    Celestial.valueOf(it.asString.uppercase())
                }.toSet()
            } else {
                emptySet()
            }

            val weather = if (tag.contains("weather")) {
                tag.getList("weather", 8).mapNotNull {
                    Weather.valueOf(it.asString.uppercase())
                }.toSet()
            } else {
                emptySet()
            }

            val requireCat = if (tag.contains("requireCat")) tag.getBoolean("requireCat") else false

            return RitualRecipe(
                ritualType,
                inputItems,
                inputEntities,
                outputItems,
                outputEntities,
                altarPower,
                covenCount,
                commands,
                isInfinite,
                floatingItemOutput,
                ticks,
                pattern,
                blockMapping,
                celestialConditions,
                weather,
                requireCat
            )
        }
    }

    fun toNbt(provider: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()

        ritualType?.let { tag.putString("ritualType", it.id.toString()) }

        inputItems.let {
            val inputItemsTag = ListTag()
            it.forEach { item -> inputItemsTag.add(item.save(provider, CompoundTag())) }
            tag.put("inputItems", inputItemsTag)
        }

        inputEntities.let {
            val inputEntitiesTag = ListTag()
            it.forEach { entity -> inputEntitiesTag.add(StringTag.valueOf(EntityType.getKey(entity).toString())) }
            tag.put("inputEntities", inputEntitiesTag)
        }

        outputItems.let {
            val outputItemsTag = ListTag()
            it.forEach { item -> outputItemsTag.add(item.save(provider, CompoundTag())) }
            tag.put("outputItems", outputItemsTag)
        }

        outputEntities.let {
            val outputEntitiesTag = ListTag()
            it.forEach { entity -> outputEntitiesTag.add(StringTag.valueOf(EntityType.getKey(entity).toString())) }
            tag.put("outputEntities", outputEntitiesTag)
        }

        tag.putInt("altarPower", altarPower)
        tag.putInt("covenCount", covenCount)

        commands.let {
            val commandsTag = ListTag()
            it.forEach { command ->
                commandsTag.add(CompoundTag().apply {
                    putString("command", command.command)
                    putString("type", command.type)
                })
            }
            tag.put("commands", commandsTag)
        }


        tag.putBoolean("isInfinite", isInfinite)
        tag.putBoolean("floatingItemOutput", floatingItemOutput)

        tag.putInt("ticks", ticks)

        pattern.let {
            val patternTag = ListTag()
            it.forEach { patternItem -> patternTag.add(StringTag.valueOf(patternItem)) }
            tag.put("pattern", patternTag)
        }

        blockMapping.let {
            val blockMappingTag = CompoundTag()
            it.forEach { (key, block) ->
                blockMappingTag.putString(key.toString(), block.builtInRegistryHolder().unwrapKey().get().toString())
            }
            tag.put("blockMapping", blockMappingTag)
        }

        celestialConditions.let {
            val celestialTag = ListTag()
            it.forEach { celestialCondition -> celestialTag.add(StringTag.valueOf(celestialCondition.name)) }
            tag.put("celestialConditions", celestialTag)
        }

        return tag
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