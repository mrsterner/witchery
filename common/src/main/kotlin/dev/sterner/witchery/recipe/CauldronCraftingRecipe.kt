package dev.sterner.witchery.recipe

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
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level


class CauldronCraftingRecipe(
    val inputItems: List<ItemStackWithColor>,
    val outputItems: List<Ingredient>,
    val altarPower: Int
) :
    Recipe<MultipleItemRecipeInput> {

    override fun matches(input: MultipleItemRecipeInput, level: Level): Boolean {

        val filteredInputItems = inputItems.filter { !it.itemStack.isEmpty }
        val filteredInputList = input.list.filter { !it.isEmpty }

        if (filteredInputList.size != filteredInputItems.size) {
            return false
        }

        return filteredInputList.all { ingredient ->
            filteredInputItems.any {
                ItemStack.isSameItem(
                    it.itemStack,
                    ingredient
                )
            }
        }
    }

    override fun assemble(input: MultipleItemRecipeInput, registries: HolderLookup.Provider): ItemStack {
        return Items.AIR.defaultInstance
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return Items.AIR.defaultInstance
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.CAULDRON_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<CauldronCraftingRecipe> {
        override fun codec(): MapCodec<CauldronCraftingRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, CauldronCraftingRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<CauldronCraftingRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<CauldronCraftingRecipe> ->
                    obj.group(
                        ItemStackWithColor.INGREDIENT_WITH_COLOR_CODEC.listOf().fieldOf("inputItems")
                            .forGetter { it.inputItems },
                        Ingredient.CODEC.listOf().fieldOf("outputItems").forGetter { it.outputItems },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower }
                    ).apply(obj, ::CauldronCraftingRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CauldronCraftingRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "cauldron_crafting"
    }
}