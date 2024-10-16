package dev.sterner.witchery.recipe.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.api.Ritual
import dev.sterner.witchery.block.ritual.CommandContext
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import dev.sterner.witchery.registry.WitcheryRitualRegistry
import dev.sterner.witchery.ritual.EmptyRitual
import dev.sterner.witchery.ritual.PushMobsRitual
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
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
    val commands: Set<CommandType>,
    val isInfinite: Boolean,
    val floatingItemOutput: Boolean,
    val ticks: Int,
    val pattern: List<String>,
    val blockMapping: Map<Char, Block>,
    val celestialConditions: Set<Celestial>
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
                    CommandContext.CODEC.fieldOf("context").orElse(CommandContext.NOTHING).forGetter(CommandType::ctx)
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
                        COMMANDS_SET_CODEC.fieldOf("commands").orElse(setOf(CommandType.DEFAULT))
                            .forGetter { recipe -> recipe.commands },
                        Codec.BOOL.fieldOf("isInfinite").orElse(false).forGetter { recipe -> recipe.isInfinite },
                        Codec.BOOL.fieldOf("floatingItemOutput").orElse(false)
                            .forGetter { recipe -> recipe.floatingItemOutput },
                        Codec.INT.fieldOf("ticks").orElse(0).forGetter { recipe -> recipe.ticks },
                        Codec.STRING.listOf().fieldOf("pattern").forGetter { recipe -> recipe.pattern },
                        Codec.unboundedMap(SYMBOL_CODEC, BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("blockMapping")
                            .forGetter { recipe -> recipe.blockMapping },
                        Celestial.CELESTIAL_SET_CODEC.fieldOf("celestialConditions").orElse(setOf()).forGetter { recipe -> recipe.celestialConditions }


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

        fun fromNbt(tag: CompoundTag, registries: HolderLookup.Provider): RitualRecipe? {
            // Load ritual type
            val ritualType = WitcheryRitualRegistry.getSadImplementation(tag)//TODO this is horrible

            // Load input items
            val inputItems = tag.getList("inputItems", 10).map { ItemStack.parse(registries, it as CompoundTag).get() }

            // Load input entities
            val inputEntities = tag.getList("inputEntities", 8).map { EntityType.byString(it.asString).orElse(null)!! }

            // Load output items
            val outputItems = tag.getList("outputItems", 10).map { ItemStack.parse(registries, it as CompoundTag).get() }

            // Load output entities
            val outputEntities = tag.getList("outputEntities", 8).map { EntityType.byString(it.asString).orElse(null)!! }

            // Load altar power
            val altarPower = tag.getInt("altarPower")

            // Load commands
            val commands = tag.getList("commands", 10).map {
                val commandTag = it as CompoundTag
                CommandType(commandTag.getString("command"), commandTag.getString("type"), CommandContext.valueOf(commandTag.getString("ctx")))
            }.toSet()

            // Load booleans
            val isInfinite = tag.getBoolean("isInfinite")
            val floatingItemOutput = tag.getBoolean("floatingItemOutput")

            // Load ticks
            val ticks = tag.getInt("ticks")

            // Load pattern
            val pattern = tag.getList("pattern", 8).map { it.asString }

            // Load block mapping
            val blockMapping = tag.getCompound("blockMapping").allKeys.associate { key ->
                key[0] to BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString(key)))
            }

            // Load celestial conditions
            val celestialConditions = tag.getList("celestialConditions", 8).map {
                Celestial.valueOf(it.asString.uppercase())
            }.toSet()

            return RitualRecipe(
                ritualType,
                inputItems,
                inputEntities,
                outputItems,
                outputEntities,
                altarPower,
                commands,
                isInfinite,
                floatingItemOutput,
                ticks,
                pattern,
                blockMapping,
                celestialConditions
            )
        }
    }

    fun toNbt(provider: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()

        // Store the ritual type (if not null)
        ritualType?.let { tag.putString("ritualType", it.id.toString()) }

        // Store input items
        val inputItemsTag = ListTag()
        inputItems.forEach { inputItemsTag.add(it.save(provider, CompoundTag())) }
        tag.put("inputItems", inputItemsTag)

        // Store input entities
        val inputEntitiesTag = ListTag()
        inputEntities.forEach { inputEntitiesTag.add(StringTag.valueOf(EntityType.getKey(it).toString())) }
        tag.put("inputEntities", inputEntitiesTag)

        // Store output items
        val outputItemsTag = ListTag()
        outputItems.forEach { outputItemsTag.add(it.save(provider, CompoundTag())) }
        tag.put("outputItems", outputItemsTag)

        // Store output entities
        val outputEntitiesTag = ListTag()
        outputEntities.forEach { outputEntitiesTag.add(StringTag.valueOf(EntityType.getKey(it).toString())) }
        tag.put("outputEntities", outputEntitiesTag)

        // Store altar power
        tag.putInt("altarPower", altarPower)

        // Store commands (serialize to string)
        val commandsTag = ListTag()
        commands.forEach { commandsTag.add(CompoundTag().apply { putString("command", it.command) }) }
        tag.put("commands", commandsTag)

        // Store booleans
        tag.putBoolean("isInfinite", isInfinite)
        tag.putBoolean("floatingItemOutput", floatingItemOutput)

        // Store ticks
        tag.putInt("ticks", ticks)

        // Store pattern
        val patternTag = ListTag()
        pattern.forEach { patternTag.add(StringTag.valueOf(it)) }
        tag.put("pattern", patternTag)

        // Store block mapping
        val blockMappingTag = CompoundTag()
        blockMapping.forEach { (key, block) -> blockMappingTag.putString(key.toString(), block.`arch$registryName`().toString()) }
        tag.put("blockMapping", blockMappingTag)

        // Store celestial conditions
        val celestialTag = ListTag()
        celestialConditions.forEach { celestialTag.add(StringTag.valueOf(it.name)) }
        tag.put("celestialConditions", celestialTag)

        return tag
    }



    enum class Celestial : StringRepresentable {
        DAY,
        NIGHT,
        FULL_MOON,
        NEW_MOON;

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
}