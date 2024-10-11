package dev.sterner.witchery.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class ItemStackWithColor(val itemStack: ItemStack, val color: Int, val order: Int) {

    companion object {
        val INGREDIENT_WITH_COLOR_CODEC: Codec<ItemStackWithColor> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("itemStack").forGetter { it.itemStack },
                Codec.INT.fieldOf("color").forGetter { it.color },
                Codec.INT.fieldOf("order").forGetter { it.order }
            ).apply(instance, ::ItemStackWithColor)
        }
    }
}