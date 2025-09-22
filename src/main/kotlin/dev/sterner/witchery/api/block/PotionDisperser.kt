package dev.sterner.witchery.api.block

import dev.sterner.witchery.item.potion.WitcheryPotionIngredient
import net.minecraft.world.item.alchemy.PotionContents
import java.util.*

interface PotionDisperser {

    fun getPotionContents(): List<PotionContents>
    fun setPotionContents(contents: List<PotionContents>)

    fun getSpecialPotions(): List<WitcheryPotionIngredient>
    fun setSpecialPotions(potions: List<WitcheryPotionIngredient>)

    fun getActiveEffects(): MutableList<ActiveEffect>

    fun getOwner(): Optional<UUID>
    fun setOwner(owner: Optional<UUID>)

    fun isInfiniteMode(): Boolean
    fun setInfiniteMode(infinite: Boolean)

    fun getDispersalRadius(): Double

    fun shouldConsumePower(): Boolean


}