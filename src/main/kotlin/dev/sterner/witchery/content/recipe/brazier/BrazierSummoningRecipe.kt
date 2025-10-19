package dev.sterner.witchery.content.recipe.brazier

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class BrazierSummoningRecipe(
    val inputItems: List<ItemStack>,
    val outputEntities: List<EntityType<*>>,
    val altarPower: Int,
) : Recipe<MultipleItemRecipeInput> {

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
        return ItemStack.EMPTY
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return true
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.BRAZIER_SUMMONING_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<BrazierSummoningRecipe> {
        override fun codec(): MapCodec<BrazierSummoningRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BrazierSummoningRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<BrazierSummoningRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<BrazierSummoningRecipe> ->
                    obj.group(
                        ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter { it.inputItems },
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().orElse(listOf()).fieldOf("outputEntities")
                            .forGetter { it.outputEntities },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                    ).apply(obj, ::BrazierSummoningRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BrazierSummoningRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "brazier_summoning"
    }
}