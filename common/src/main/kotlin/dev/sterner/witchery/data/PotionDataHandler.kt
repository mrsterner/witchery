package dev.sterner.witchery.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import dev.architectury.registry.ReloadListenerRegistry
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object PotionDataHandler {
    fun getIngredientFromItem(item: ItemStack): WitcheryPotionIngredient? {
        return POTION_PAIR[item.item]
    }

    val LOADER = PotionResourceReloadListener(Gson(), "potion")
    val POTION_PAIR = mutableMapOf<Item, WitcheryPotionIngredient>()

    fun registerListener() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, object : PreparableReloadListener {
            override fun getName() = "potion"

            override fun reload(
                preparationBarrier: PreparableReloadListener.PreparationBarrier,
                resourceManager: ResourceManager,
                preparationsProfiler: ProfilerFiller,
                reloadProfiler: ProfilerFiller,
                backgroundExecutor: Executor,
                gameExecutor: Executor
            ): CompletableFuture<Void> {
                return LOADER.reload(
                    preparationBarrier,
                    resourceManager,
                    preparationsProfiler,
                    reloadProfiler,
                    backgroundExecutor,
                    gameExecutor
                )
            }
        })
    }

    class PotionResourceReloadListener(gson: Gson, directory: String) : SimpleJsonResourceReloadListener(gson, directory) {

        override fun apply(
            `object`: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            `object`.forEach { (file, element) ->
                try {
                    if (element.isJsonArray)
                        element.asJsonArray.map(JsonElement::getAsJsonObject).forEach { parseJson(it, file) }
                    else if (element.isJsonObject)
                        parseJson(element.asJsonObject, file)
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.fillInStackTrace())
                }
            }
        }

        private fun parseJson(json: JsonObject, file: ResourceLocation) {
            val result = WitcheryPotionIngredient.CODEC.parse(JsonOps.INSTANCE, json)

            result.resultOrPartial { _ ->

            }?.let { ingredient ->
                val item = ingredient.get().item.item
                if (item != Items.AIR && ingredient.isPresent) {
                    POTION_PAIR[item] = ingredient.get()
                }
            }
        }
    }
}