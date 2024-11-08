package dev.sterner.witchery.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.commands.InfusionArgumentType
import dev.sterner.witchery.platform.PlayerMiscDataAttachment
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component


object WitcheryCommands {

    val COMMAND_ARGUMENTS: DeferredRegister<ArgumentTypeInfo<*, *>> = DeferredRegister.create(Witchery.MODID, Registries.COMMAND_ARGUMENT_TYPE)

    val INFUSION_TYPE = COMMAND_ARGUMENTS.register("infusion_type") {
        registerByClass(InfusionArgumentType::class.java, SingletonArgumentInfo.contextFree(::InfusionArgumentType))
    }

    private fun <A : ArgumentType<*>?, T : ArgumentTypeInfo.Template<A>?, I : ArgumentTypeInfo<A, T>?> registerByClass(
        infoClass: Class<A>?,
        argumentTypeInfo: I
    ): I {
        ArgumentTypeInfos.BY_CLASS[infoClass] = argumentTypeInfo
        return argumentTypeInfo
    }

    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        context: CommandBuildContext,
        selection: Commands.CommandSelection
    ) {
        dispatcher.register(
            Commands.literal("witchery")
                .then(
                    Commands.literal("infusion")
                        .requires { it.hasPermission(2) }
                        .then(
                            Commands.literal("set")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .then(
                                            Commands.argument("infusionType", InfusionArgumentType.infusionType())
                                                .executes { ctx ->
                                                    val player = EntityArgument.getPlayer(ctx, "player")
                                                    val infusionType =
                                                        InfusionArgumentType.getInfusionType(ctx, "infusionType")

                                                    PlayerInfusionDataAttachment.setPlayerInfusion(
                                                        player,
                                                        InfusionData(infusionType)
                                                    )

                                                    ctx.source.sendSuccess(
                                                        { Component.literal("Selected infusion type: ${infusionType.serializedName} for player ${player.name.string}") },
                                                        false
                                                    )
                                                    1
                                                }
                                        )
                                )
                        )
                        .then(
                            Commands.literal("get")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .executes { ctx ->
                                            val player = EntityArgument.getPlayer(ctx, "player")
                                            val currentInfusion = PlayerInfusionDataAttachment.getPlayerInfusion(player)

                                            ctx.source.sendSuccess(
                                                { Component.literal("Current infusion type: ${currentInfusion.type.serializedName} for player ${player.name.string}") },
                                                false
                                            )
                                            1
                                        }
                                )
                        )
                        .then(
                            Commands.literal("increase")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .then(
                                            Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes { ctx ->
                                                    val player = EntityArgument.getPlayer(ctx, "player")
                                                    val amount = IntegerArgumentType.getInteger(ctx, "amount")

                                                    if (PlayerInfusionDataAttachment.getPlayerInfusion(player).type != InfusionType.NONE) {
                                                        PlayerInfusionDataAttachment.increaseInfusionCharge(player, amount)
                                                    }

                                                    ctx.source.sendSuccess(
                                                        { Component.literal("Increased infusion charge by $amount for player ${player.name.string}") },
                                                        false
                                                    )
                                                    1
                                                }
                                        )
                                )
                        )
                        .then(
                            Commands.literal("setAndKill")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .then(
                                            Commands.argument("infusionType", InfusionArgumentType.infusionType())
                                                .executes { ctx ->
                                                    val player = EntityArgument.getPlayer(ctx, "player")
                                                    val infusionType =
                                                        InfusionArgumentType.getInfusionType(ctx, "infusionType")

                                                    player.hurt(player.level().damageSources().magic(), 100f)
                                                    if (player.health > 0) {
                                                        PlayerInfusionDataAttachment.setPlayerInfusion(
                                                            player,
                                                            InfusionData(infusionType)
                                                        )
                                                    }

                                                    ctx.source.sendSuccess(
                                                        { Component.literal("Set infusion type: ${infusionType.serializedName} and dealt damage to player ${player.name.string}") },
                                                        false
                                                    )
                                                    1
                                                }
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("manifestation")
                        .requires { it.hasPermission(2) }
                        .then(
                            Commands.literal("set")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .then(
                                            Commands.argument("status", BoolArgumentType.bool())
                                                .executes { ctx ->
                                                    val player = EntityArgument.getPlayer(ctx, "player")
                                                    val status = BoolArgumentType.getBool(ctx, "status")

                                                    PlayerMiscDataAttachment.setData(player, PlayerMiscDataAttachment.Data(status))

                                                    ctx.source.sendSuccess(
                                                        { Component.literal("Set manifestation status to $status for player ${player.name.string}") },
                                                        false
                                                    )
                                                    1
                                                }
                                        )
                                )
                        )
                        .then(
                            Commands.literal("get")
                                .then(
                                    Commands.argument("player", EntityArgument.player())
                                        .executes { ctx ->
                                            val player = EntityArgument.getPlayer(ctx, "player")
                                            val status = PlayerMiscDataAttachment.getData(player).hasRiteOfManifestation

                                            ctx.source.sendSuccess(
                                                { Component.literal("Current manifestation status: $status for player ${player.name.string}") },
                                                false
                                            )
                                            1
                                        }
                                )
                        )
                )
        )
    }
}