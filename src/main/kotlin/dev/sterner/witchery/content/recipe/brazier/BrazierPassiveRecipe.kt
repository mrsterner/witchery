package dev.sterner.witchery.content.recipe.brazier

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.recipe.AltarUserRecipe
import dev.sterner.witchery.content.recipe.MultipleItemRecipeInput
import dev.sterner.witchery.core.api.BrazierPassive
import dev.sterner.witchery.core.registry.WitcheryBrazierRegistry
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.core.registry.WitcheryRitualRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class BrazierPassiveRecipe(
    val passive: BrazierPassive,
    val inputItems: List<ItemStack>,
    override val altarPower: Int,
) : Recipe<MultipleItemRecipeInput>, AltarUserRecipe {

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
        return WitcheryRecipeSerializers.BRAZIER_PASSIVE_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.BRAZIER_PASSIVE_RECIPE_TYPE.get()
    }

    class Serializer : RecipeSerializer<BrazierPassiveRecipe> {
        override fun codec(): MapCodec<BrazierPassiveRecipe> {
            return CODEC
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BrazierPassiveRecipe> {
            return STREAM_CODEC
        }

        companion object {
            val CODEC: MapCodec<BrazierPassiveRecipe> =
                RecordCodecBuilder.mapCodec { obj: RecordCodecBuilder.Instance<BrazierPassiveRecipe> ->
                    obj.group(
                        WitcheryBrazierRegistry.CODEC.fieldOf("ritual").forGetter { it.passive },
                        ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter { it.inputItems },
                        Codec.INT.fieldOf("altarPower").forGetter { recipe -> recipe.altarPower },
                    ).apply(obj, ::BrazierPassiveRecipe)
                }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BrazierPassiveRecipe> =
                ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
                )
        }
    }

    companion object {
        const val NAME: String = "brazier_passive"
    }
}