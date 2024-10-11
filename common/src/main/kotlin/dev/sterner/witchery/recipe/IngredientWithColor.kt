package dev.sterner.witchery.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.BiFunction

class IngredientWithColor(val ingredient: Ingredient, val color: Int, val order: Int) {

    companion object {
        val INGREDIENT_WITH_COLOR_CODEC: Codec<IngredientWithColor> = RecordCodecBuilder.create { instance ->
            instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter { it.ingredient },
                Codec.INT.fieldOf("color").forGetter { it.color },
                Codec.INT.fieldOf("order").forGetter { it.order }
            ).apply(instance, ::IngredientWithColor)
        }
    }
}