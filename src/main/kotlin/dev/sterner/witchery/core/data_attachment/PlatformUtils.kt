package dev.sterner.witchery.core.data_attachment

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.ArgumentTypeInfosInvoker
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.WoodType
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForgeMod
import top.theillusivec4.curios.api.CuriosApi

object PlatformUtils {

    @JvmStatic
    fun isDevEnv(): Boolean {
        return !FMLEnvironment.production
    }


}