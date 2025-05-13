package dev.sterner.witchery.fabric

import dev.sterner.witchery.Witchery
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.function.Supplier


class WitcheryModelLoaderPlugin : ModelLoadingPlugin {

    val WORLD_MODEL: ModelResourceLocation = createModel("death_sickle")
    val GUI_MODEL: ResourceLocation = Witchery.id("item/death_sickle_gui")

    override fun onInitializeModelLoader(pluginContext: ModelLoadingPlugin.Context) {
        pluginContext.addModels(GUI_MODEL)

        pluginContext.modifyModelAfterBake().register({ original, context ->
            if (WORLD_MODEL == context.topLevelId()) {
                val guiModel = context.baker().bake(GUI_MODEL, context.settings())
                return@register WitcheryBakedModel(original, guiModel!!)
            }
            original
        })
    }

    class WitcheryBakedModel(heldModel: BakedModel?, guiModel: BakedModel) : ForwardingBakedModel() {
        private val guiModel: BakedModel

        init {
            this.wrapped = heldModel
            this.guiModel = guiModel
        }

        override fun emitItemQuads(
            stack: ItemStack?,
            randomSupplier: Supplier<RandomSource?>?,
            context: RenderContext
        ) {
            if (ITEM_GUI_CONTEXTS.contains(context.itemTransformationMode())) {
                guiModel.emitItemQuads(stack, randomSupplier, context)
                return
            }
            super.emitItemQuads(stack, randomSupplier, context)
        }

        override fun isVanillaAdapter(): Boolean {
            return false
        }

        companion object {
            private val ITEM_GUI_CONTEXTS: MutableSet<ItemDisplayContext?> = EnumSet.of<ItemDisplayContext?>(
                ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED
            )
        }
    }

    private fun createModel(baseName: String): ModelResourceLocation {
        return ModelResourceLocation( Witchery.id(baseName), "inventory")
    }
}