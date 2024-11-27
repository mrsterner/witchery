package dev.sterner.witchery.fabric.client

/*
class WitcheryFabricModelLoaderPlugin : ModelLoadingPlugin {


    companion object {
        val CANE_BASE_MODEL = ModelResourceLocation.inventory(Witchery.id("cane_sword"))
        val CANE_GUI_MODEL = Witchery.id("item/cane_sword_gui")
        val CANE_UNSHEETED_MODEL = ModelResourceLocation.inventory(Witchery.id("cane_sword_unsheeted"))
        val CANE_GUI_UNSHEETED_MODEL = Witchery.id("item/cane_sword_gui_unsheeted")
    }

    override fun onInitializeModelLoader(pluginContext: ModelLoadingPlugin.Context?) {
        pluginContext?.addModels(CANE_GUI_MODEL)
        pluginContext?.addModels(CANE_GUI_UNSHEETED_MODEL)

        pluginContext!!.modifyModelAfterBake().register { original, context ->
            val guiModelLocation = if (context.topLevelId() == CANE_BASE_MODEL) {
                CANE_GUI_MODEL
            } else if (context.topLevelId() == CANE_UNSHEETED_MODEL) {
                CANE_GUI_UNSHEETED_MODEL
            } else {
                null
            }

            guiModelLocation?.let { guiModelLoc ->
                val guiModel = context.baker().bake(guiModelLoc, context.settings())
                if (original != null && guiModel != null) {
                    return@register WitcheryBakedModel(original, guiModel)
                }
            }
            original
        }
    }

    class WitcheryBakedModel(val heldModel: BakedModel?, val guiModel: BakedModel?) : ForwardingBakedModel() {

        private var transforms: ItemTransforms? = null

        init {
            this.wrapped = heldModel
            this.transforms = ItemTransforms(
                heldModel!!.transforms.thirdPersonLeftHand,
                heldModel!!.transforms.thirdPersonRightHand,
                heldModel!!.transforms.firstPersonLeftHand,
                heldModel!!.transforms.firstPersonRightHand,
                heldModel!!.transforms.head,
                guiModel!!.transforms.gui,
                guiModel!!.transforms.ground,
                guiModel!!.transforms.fixed
            )
        }

        companion object {
            val ITEM_GUI_CONTEXTS: Set<ItemDisplayContext> =
                EnumSet.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED)

        }

        override fun getTransforms(): ItemTransforms {
            return transforms!!
        }

        override fun emitItemQuads(
            stack: ItemStack?,
            randomSupplier: Supplier<RandomSource?>?,
            context: RenderContext
        ) {
            if (ITEM_GUI_CONTEXTS.contains(context.itemTransformationMode())) {
                guiModel!!.emitItemQuads(stack, randomSupplier, context)
                return
            }
            super.emitItemQuads(stack, randomSupplier, context)
        }

        override fun isVanillaAdapter(): Boolean {
            return false
        }
    }
}

 */