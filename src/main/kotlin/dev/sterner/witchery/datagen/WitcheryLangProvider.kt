package dev.sterner.witchery.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class WitcheryLangProvider(output: PackOutput, modid: String, locale: String) :
    LanguageProvider(output, modid, locale) {

    override fun addTranslations() {
        add("itemGroup.witchery", "Witchery")
    }
}