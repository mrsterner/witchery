package dev.sterner.witchery.core.registry

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping


object WitcheryKeyMappings {

    val BROOM_DISMOUNT_KEYMAPPING: KeyMapping = KeyMapping(
        "key.witchery.dismount",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_X,
        "key.categories.witchery"
    )

    val OPEN_ABILITY_SELECTION = KeyMapping(
        "key.witchery.open_ability_selection",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_V,
        "key.categories.witchery"
    )

    val UTILITY_BUTTON = KeyMapping(
        "key.witchery.utility_button",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_LALT,
        "key.categories.witchery"
    )

}