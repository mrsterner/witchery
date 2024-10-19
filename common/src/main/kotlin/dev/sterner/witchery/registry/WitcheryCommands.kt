package dev.sterner.witchery.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.commands.InfusionArgumentType
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component


object WitcheryCommands {

    val COMMAND_ARGUMENTS = DeferredRegister.create(Witchery.MODID, Registries.COMMAND_ARGUMENT_TYPE)

    val INFUSION_TYPE = COMMAND_ARGUMENTS.register("infusion_type") {
        registerByClass(InfusionArgumentType::class.java, SingletonArgumentInfo.contextFree(::InfusionArgumentType))
    }

    fun <A : ArgumentType<*>?, T : ArgumentTypeInfo.Template<A>?, I : ArgumentTypeInfo<A, T>?> registerByClass(
        infoClass: Class<A>?,
        argumentTypeInfo: I
    ): I {
        ArgumentTypeInfos.BY_CLASS[infoClass] = argumentTypeInfo
        return argumentTypeInfo
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext, selection: Commands.CommandSelection) {



        dispatcher.register(
            Commands.literal("witchery").then(
                Commands.literal("infusion")
                    .requires { it.hasPermission(2) }
                    .then(
                        Commands.argument("infusionType", InfusionArgumentType.infusionType())
                            .executes { ctx ->
                                val infusionType = InfusionArgumentType.getInfusionType(ctx, "infusionType")

                                PlayerInfusionDataAttachment.setPlayerInfusion(ctx.source.player!!, InfusionData(infusionType))

                                ctx.source.sendSuccess(
                                    {
                                        Component.literal("Selected infusion type: ${infusionType.serializedName}")
                                    }, false
                                )
                                1
                            }
                    )
            )
        )
    }
}