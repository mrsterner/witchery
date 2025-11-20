package dev.sterner.witchery.content.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.content.item.MirrorItem
import dev.sterner.witchery.content.item.WaystoneItem
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryRecipeSerializers
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level

class LinkedMirrorRecipe(
    private val mirror: Ingredient = Ingredient.of(WitcheryItems.MIRROR.get()),
    private val waystone: Ingredient = Ingredient.of(WitcheryItems.WAYSTONE.get()),
) : CustomRecipe(CraftingBookCategory.MISC) {

    override fun matches(input: CraftingInput, level: Level): Boolean {
        var foundMirror = false
        var foundWaystone = false
        var itemCount = 0

        for (i in 0 until input.size()) {
            val stack = input.getItem(i)
            if (!stack.isEmpty) {
                itemCount++

                if (mirror.test(stack)) {
                    if (foundMirror) return false
                    foundMirror = true
                } else if (waystone.test(stack)) {
                    if (foundWaystone) return false
                    if (WaystoneItem.getGlobalPos(stack) == null) return false
                    foundWaystone = true
                } else {
                    return false
                }
            }
        }

        return foundMirror && foundWaystone && itemCount == 2
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var waystoneStack: ItemStack? = null

        for (i in 0 until input.size()) {
            val stack = input.getItem(i)
            if (waystone.test(stack)) {
                waystoneStack = stack
                break
            }
        }

        if (waystoneStack == null) return ItemStack.EMPTY

        val linkedPos = WaystoneItem.getGlobalPos(waystoneStack) ?: return ItemStack.EMPTY

        val result = ItemStack(WitcheryItems.MIRROR.get())
        MirrorItem.setLinkedPos(result, linkedPos)

        return result
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return ItemStack(WitcheryItems.MIRROR.get())
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return WitcheryRecipeSerializers.MIRROR_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return WitcheryRecipeTypes.MIRROR_RECIPE_TYPE.get()
    }

    override fun isSpecial(): Boolean {
        return true
    }

    class Serializer : RecipeSerializer<LinkedMirrorRecipe> {
        private val CODEC: MapCodec<LinkedMirrorRecipe> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Ingredient.CODEC.fieldOf("mirror").forGetter { it.mirror },
                Ingredient.CODEC.fieldOf("waystone").forGetter { it.waystone }
            ).apply(instance, ::LinkedMirrorRecipe)
        }

        private val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, LinkedMirrorRecipe> =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                { it.mirror },
                Ingredient.CONTENTS_STREAM_CODEC,
                { it.waystone },
                ::LinkedMirrorRecipe
            )

        override fun codec(): MapCodec<LinkedMirrorRecipe> = CODEC

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, LinkedMirrorRecipe> = STREAM_CODEC
    }

    companion object {
        const val NAME: String = "link_mirror"
    }
}