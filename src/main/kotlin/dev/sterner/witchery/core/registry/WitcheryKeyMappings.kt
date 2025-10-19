package dev.sterner.witchery.core.registry

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping


object WitcheryKeyMappings {

    val BROOM_DISMOUNT_KEYMAPPING: KeyMapping = KeyMapping(
        "key.witchery.dismount",  // The translation key of the name shown in the Controls screen
        InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
        InputConstants.KEY_X,  // The default keycode
        "category.witchery" // The category translation key used to categorize in the Controls screen
    )

    val OPEN_ABILITY_SELECTION = KeyMapping(
        "key.witchery.open_ability_selection",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_V, // V key by default
        "key.categories.witchery"
    )

}