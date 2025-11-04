package dev.sterner.witchery.content.recipe.distillery

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.recipe.AltarUserRecipe
import dev.sterner.witchery.content.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level


class DistilleryCraftingRecipe(
    val inputItems: List<ItemStack>,
    val outputItems: List<ItemStack>,
    override val altarPower: Int,
    val cookingTime: Int,
    val jarConsumption: Int
) :
    Recipe<MultipleItemRecipeInput>, AltarUserRecipe {


    override fun matches(input: MultipleItemRecipeInput, level: Level): Boolean {

        val filteredInputItems = inputItems.filter { !it.isEmpty }
        val filteredInputList = input.list.filter { !it.isEmpty }

        if (filteredInputList.size != filteredInputItems.size) {
            return false
        }

        return filteredInputList.all { ingredient ->
            filteredInputItems.any {
                ItemStack.isSameItem(
                    it,
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
        return WitcheryRecipeSerializers.DISTILLERY_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.DISTILLERY_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<DistilleryCraftingRecipe> {
        override fun codec(): MapCodec<DistilleryCraftingRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DistilleryCraftingRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<DistilleryCraftingRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<DistilleryCraftingRecipe> ->
                    obj.group(
                        ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter { it.inputItems },
                        ItemStack.CODEC.listOf().fieldOf("outputItems").forGetter { it.outputItems },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                        Codec.INT.fieldOf("cookingTime").forGetter { recipe -> recipe.cookingTime },
                        Codec.INT.fieldOf("jarConsumption").forGetter { recipe -> recipe.jarConsumption }
                    ).apply(obj, ::DistilleryCraftingRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DistilleryCraftingRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "distillery_crafting"
    }
}