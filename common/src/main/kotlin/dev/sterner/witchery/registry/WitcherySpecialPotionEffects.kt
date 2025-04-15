package dev.sterner.witchery.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SpecialPotion
import net.minecraft.resources.ResourceLocation

object WitcherySpecialPotionEffects {

    val ID = Witchery.id("special_potion_effect")

    val SPECIALS: Registrar<SpecialPotion> = RegistrarManager.get(Witchery.MODID).builder<SpecialPotion>(ID)
        .syncToClients().build()

    val CODEC: Codec<SpecialPotion> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<SpecialPotion> ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter { special -> special.id }
        ).apply(instance) { resourceLocation ->
            SPECIALS.get(resourceLocation)
        }
    }

    fun init() {
    }
}