package dev.sterner.witchery.content.recipe.cauldron

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class CauldronInfusionRecipe(
    val brewInput: ItemStack,
    val infusionItem: ItemStack,
    val outputItem: ItemStack,
    val altarPower: Int
) : Recipe<CauldronInfusionRecipeInput> {

    override fun matches(input: CauldronInfusionRecipeInput, level: Level): Boolean {
        if (!ItemStack.isSameItem(brewInput, input.brewStack)) {
            return false
        }

        return ItemStack.isSameItem(infusionItem, input.thrownItem)
    }

    override fun assemble(input: CauldronInfusionRecipeInput, registries: HolderLookup.Provider): ItemStack {
        return outputItem.copy()
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return outputItem
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.CAULDRON_INFUSION_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.CAULDRON_INFUSION_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<CauldronInfusionRecipe> {
        override fun codec(): MapCodec<CauldronInfusionRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, CauldronInfusionRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<CauldronInfusionRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<CauldronInfusionRecipe> ->
                    obj.group(
                        ItemStack.CODEC.fieldOf("brewInput").forGetter { it.brewInput },
                        ItemStack.CODEC.fieldOf("infusionItem").forGetter { it.infusionItem },
                        ItemStack.CODEC.fieldOf("outputItem").forGetter { it.outputItem },
                        Codec.INT.fieldOf("altarPower").forGetter { it.altarPower }
                    ).apply(obj, ::CauldronInfusionRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CauldronInfusionRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec())
        }
    }

    companion object {
        const val NAME: String = "cauldron_infusion"
    }
}