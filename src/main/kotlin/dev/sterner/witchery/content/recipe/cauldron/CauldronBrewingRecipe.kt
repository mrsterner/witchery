package dev.sterner.witchery.content.recipe.cauldron

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level


class CauldronBrewingRecipe(
    val inputItems: List<ItemStackWithColor>,
    val outputItem: ItemStack,
    val altarPower: Int,
    val dimensionKey: Set<String>
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
        return outputItem
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return outputItem
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.CAULDRON_BREWING_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<CauldronBrewingRecipe> {
        override fun codec(): MapCodec<CauldronBrewingRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<CauldronBrewingRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<CauldronBrewingRecipe> ->
                    obj.group(
                        ItemStackWithColor.INGREDIENT_WITH_COLOR_CODEC.listOf().fieldOf("inputItems")
                            .forGetter { it.inputItems },
                        ItemStack.CODEC.fieldOf("outputItem").forGetter { it.outputItem },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                        Codec.STRING.listOf().fieldOf("dimensionKey")
                            .xmap({ it.toSet() }, { it.toList() })
                            .forGetter { it.dimensionKey }
                    ).apply(obj, ::CauldronBrewingRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "cauldron_brewing"
    }
}