package dev.sterner.witchery.datagen


import com.klikli_dev.modonomicon.api.datagen.BookProvider
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.WitcherySubBookProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class WitcheryBookProvider(
    packOutput: PackOutput?,
    registries: CompletableFuture<HolderLookup.Provider>?,
    lang: BiConsumer<String, String>
) : BookProvider(
    packOutput, registries, Witchery.MODID,
    listOf(
        WitcherySubBookProvider(lang)
    )
)