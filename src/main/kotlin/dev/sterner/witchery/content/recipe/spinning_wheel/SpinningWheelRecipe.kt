package dev.sterner.witchery.content.recipe.spinning_wheel

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.recipe.AltarUserRecipe
import dev.sterner.witchery.content.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
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

class SpinningWheelRecipe(
    val inputItems: List<ItemStack>,
    val outputItem: ItemStack,
    override val altarPower: Int,
    val cookingTime: Int
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
        return WitcheryRecipeSerializers.SPINNING_WHEEL_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.SPINNING_WHEEL_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<SpinningWheelRecipe> {
        override fun codec(): MapCodec<SpinningWheelRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SpinningWheelRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<SpinningWheelRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<SpinningWheelRecipe> ->
                    obj.group(
                        ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter { it.inputItems },
                        ItemStack.CODEC.fieldOf("outputItem").forGetter { it.outputItem },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                        Codec.INT.fieldOf("cookingTime").forGetter { recipe -> recipe.cookingTime },
                    ).apply(obj, ::SpinningWheelRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SpinningWheelRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "spinning_wheel"
    }
}