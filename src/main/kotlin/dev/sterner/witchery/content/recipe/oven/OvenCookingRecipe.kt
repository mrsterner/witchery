package dev.sterner.witchery.content.recipe.oven

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level

class OvenCookingRecipe(
    val ingredient: Ingredient,
    val extraIngredient: Ingredient,
    val result: ItemStack,
    val extraOutput: ItemStack,
    val extraOutputChance: Float,
    val experience: Float,
    val cookingTime: Int
) : Recipe<SingleRecipeInput> {


    override fun matches(input: SingleRecipeInput, level: Level): Boolean {
        return ingredient.test(input.item())
    }

    override fun assemble(input: SingleRecipeInput, registries: HolderLookup.Provider): ItemStack {
        return result.copy()
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return this.result
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.OVEN_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get()
    }

    companion object {
        const val NAME: String = "oven_cooking"
    }

    class Serializer : RecipeSerializer<OvenCookingRecipe> {
        override fun codec(): MapCodec<OvenCookingRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, OvenCookingRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<OvenCookingRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<OvenCookingRecipe> ->
                    obj.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter { it.ingredient },
                        Ingredient.CODEC_NONEMPTY.fieldOf("extraIngredient").orElse(Ingredient.of())
                            .forGetter { it.extraIngredient },
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter { it.result },
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("extraOutput").forGetter { it.extraOutput },
                        Codec.FLOAT.fieldOf("extraOutputChance").orElse(1.0F).forGetter { it.extraOutputChance },
                        Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter { it.experience },
                        Codec.INT.fieldOf("cookingTime").forGetter { it.cookingTime },
                    ).apply(obj, ::OvenCookingRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OvenCookingRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }
}