package dev.sterner.witchery.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.commands.CurseArgumentType
import dev.sterner.witchery.commands.InfusionArgumentType
import dev.sterner.witchery.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.data_attachment.PlatformUtils
import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionType
import dev.sterner.witchery.data_attachment.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.data_attachment.transformation.SoulPoolPlayerAttachment
import dev.sterner.witchery.handler.CurseHandler
import dev.sterner.witchery.handler.FamiliarHandler
import dev.sterner.witchery.handler.ManifestationHandler
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.handler.affliction.LichdomLeveling
import dev.sterner.witchery.handler.affliction.VampireLeveling
import dev.sterner.witchery.handler.affliction.VampireLeveling.levelToBlood
import dev.sterner.witchery.handler.affliction.WerewolfLeveling
import dev.sterner.witchery.handler.infusion.InfusionHandler
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryCommands {

    val COMMAND_ARGUMENTS: DeferredRegister<ArgumentTypeInfo<*, *>> =
        DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Witchery.MODID)

    val INFUSION_TYPE = COMMAND_ARGUMENTS.register("infusion_type", Supplier {
        registerByClass(InfusionArgumentType::class.java, SingletonArgumentInfo.contextFree(::InfusionArgumentType))
    })

    val CURSE_TYPE = COMMAND_ARGUMENTS.register("curse_type", Supplier {
        registerByClass(CurseArgumentType::class.java, SingletonArgumentInfo.contextFree(::CurseArgumentType))
    })

    private fun <A : ArgumentType<*>?, T : ArgumentTypeInfo.Template<A>?, I : ArgumentTypeInfo<A, T>?> registerByClass(
        infoClass: Class<A>?,
        argumentTypeInfo: I
    ): I {
        val byClass: MutableMap<Class<*>, ArgumentTypeInfo<*, *>> = PlatformUtils.getByClass()
        byClass[infoClass as Class<*>] = argumentTypeInfo as ArgumentTypeInfo<*, *>

        return argumentTypeInfo
    }



    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        context: CommandBuildContext,
        selection: Commands.CommandSelection
    ) {
        dispatcher.register(
            Commands.literal("witchery")
                .then(registerInfusionCommands())
                .then(registerManifestationCommands())
                .then(registerCurseCommands())
                .then(registerVampireCommands())
                .then(registerWerewolfCommands())
                .then(registerLichdomCommands())
        )
    }

    private fun registerInfusionCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("infusion")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("set")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("infusion", InfusionArgumentType.infusionType())
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val infusionType = InfusionArgumentType.getInfusionType(ctx, "infusion")
                                        InfusionPlayerAttachment.setPlayerInfusion(
                                            player,
                                            InfusionPlayerAttachment.Data(infusionType)
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
                                val currentInfusion = InfusionPlayerAttachment.getPlayerInfusion(player)
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
                                        if (InfusionPlayerAttachment.getPlayerInfusion(player).type != InfusionType.NONE) {
                                            InfusionHandler.increaseInfusionCharge(player, amount)
                                        }
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
                                        val infusionType = InfusionArgumentType.getInfusionType(ctx, "infusionType")
                                        player.hurt(player.level().damageSources().magic(), 100f)
                                        if (player.health > 0) {
                                            InfusionPlayerAttachment.setPlayerInfusion(
                                                player,
                                                InfusionPlayerAttachment.Data(infusionType)
                                            )
                                        }
                                        1
                                    }
                            )
                    )
            )
    }

    private fun registerManifestationCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("manifestation")
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
                                        ManifestationHandler.setHasRiteOfManifestation(player, status)
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
                                val status = ManifestationPlayerAttachment.getData(player).hasRiteOfManifestation
                                ctx.source.sendSuccess(
                                    { Component.literal("Current manifestation status: $status for player ${player.name.string}") },
                                    false
                                )
                                1
                            }
                    )
            )
    }

    private fun registerCurseCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("curse")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("apply")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("curse", CurseArgumentType.curseType())
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val curseType = CurseArgumentType.getCurse(ctx, "curse")
                                        val commandSender = ctx.source.player
                                        val cat = if (commandSender != null) {
                                            FamiliarHandler.getFamiliarEntityType(
                                                commandSender.uuid,
                                                commandSender.serverLevel()
                                            ) == EntityType.CAT
                                        } else {
                                            false
                                        }
                                        CurseHandler.addCurse(
                                            player,
                                            commandSender,
                                            WitcheryCurseRegistry.CURSES.registry.get().getKey(curseType)!!,
                                            cat
                                        )
                                        1
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("remove")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("curse", CurseArgumentType.curseType())
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val curseType = CurseArgumentType.getCurse(ctx, "curse")
                                        CurseHandler.removeCurse(player, curseType)
                                        1
                                    }
                            )
                    )
            )
    }

    private fun registerLichdomCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("lichdom")
            .requires { it.hasPermission(2) }
            .then(Commands.literal("level")
                .then(Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = context.source.playerOrException
                            val level = AfflictionPlayerAttachment.getData(player).getLevel(AfflictionTypes.LICHDOM)
                            context.source.sendSuccess(
                                { Component.literal("Level: $level for ${player.name.string}") },
                                true
                            )
                            1
                        }
                    ))

                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                            .executes { context ->

                                val level = IntegerArgumentType.getInteger(context, "level")
                                val player = context.source.playerOrException

                                LichdomLeveling.setLevel(player, level)
                                LichdomLeveling.updateModifiers(player, level)

                                context.source.sendSuccess(
                                    { Component.literal("Set lichdom level to $level for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                        )
                    ))
            )
            .then(Commands.literal("soul")

                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument(
                            "level",
                            IntegerArgumentType.integer(
                                0,
                                LichdomLeveling.LEVEL_REQUIREMENTS.map { it.key }.max()
                            )
                        )
                            .executes { context ->

                                val level = IntegerArgumentType.getInteger(context, "level")
                                val player = context.source.playerOrException

                                val data = SoulPoolPlayerAttachment.getData(player)

                                SoulPoolPlayerAttachment.setData(
                                    player,
                                    SoulPoolPlayerAttachment.Data(
                                        data.maxSouls,
                                        Mth.clamp(level, 0, data.maxSouls)
                                    )
                                )

                                context.source.sendSuccess(
                                    { Component.literal("Set soul level to $level for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                        )
                    ))


                .then(
                    Commands.literal("get")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes { context ->
                                val player = context.source.playerOrException

                                val data = SoulPoolPlayerAttachment.getData(player)
                                player.sendSystemMessage(Component.literal("Soul Level: " + data.soulPool + "/" + data.maxSouls))
                                1
                            })
                )
            )
    }

    private fun registerVampireCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("vampire")
            .requires { it.hasPermission(2) }
            .then(Commands.literal("level")
                .then(Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = context.source.playerOrException
                            val level = AfflictionPlayerAttachment.getData(player).getVampireLevel()
                            context.source.sendSuccess(
                                { Component.literal("Level: $level for ${player.name.string}") },
                                true
                            )
                            1
                        }
                    ))

                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                            .executes { context ->

                                val level = IntegerArgumentType.getInteger(context, "level")
                                val player = context.source.playerOrException

                                VampireLeveling.setLevel(player, level)
                                VampireLeveling.updateModifiers(player, level, false)
                                val maxBlood = levelToBlood(level)
                                BloodPoolLivingEntityAttachment.setData(
                                    player,
                                    BloodPoolLivingEntityAttachment.Data(maxBlood, maxBlood)
                                )

                                context.source.sendSuccess(
                                    { Component.literal("Set vampire level to $level for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                        )
                    ))
            )
            .then(Commands.literal("blood")

                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument(
                            "level",
                            IntegerArgumentType.integer(
                                0,
                                VampireLeveling.LEVEL_REQUIREMENTS.map { it.key }.max()
                            )
                        )
                            .executes { context ->

                                val level = IntegerArgumentType.getInteger(context, "level")
                                val player = context.source.playerOrException

                                val data = BloodPoolLivingEntityAttachment.getData(player)

                                BloodPoolLivingEntityAttachment.setData(
                                    player,
                                    BloodPoolLivingEntityAttachment.Data(
                                        data.maxBlood,
                                        Mth.clamp(level, 0, data.maxBlood)
                                    )
                                )

                                context.source.sendSuccess(
                                    { Component.literal("Set blood level to $level for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                        )
                    ))


                .then(
                    Commands.literal("get")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes { context ->
                                val player = context.source.playerOrException

                                val data = BloodPoolLivingEntityAttachment.getData(player)
                                player.sendSystemMessage(Component.literal("Blood Level: " + data.bloodPool + "/" + data.maxBlood))
                                1
                            })
                )
            )
    }

    private fun registerWerewolfCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("werewolf")
            .requires { it.hasPermission(2) }
            .then(Commands.literal("level")
                .then(Commands.literal("try_curse")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = context.source.playerOrException
                            val currentLevel = AfflictionPlayerAttachment.getData(player).getWerewolfLevel()
                            if (currentLevel == 0) {
                                WerewolfLeveling.increaseWerewolfLevel(player)
                            }
                            1
                        }
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument(
                            "level",
                            IntegerArgumentType.integer(
                                0,
                                WerewolfLeveling.LEVEL_REQUIREMENTS.map { it.key }.max()
                            )
                        )
                            .executes { context ->

                                val level = IntegerArgumentType.getInteger(context, "level")
                                val player = context.source.playerOrException

                                WerewolfLeveling.setLevel(player, level)

                                context.source.sendSuccess(
                                    { Component.literal("Set werewolf level to $level for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                        )
                    ))

                .then(Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = context.source.playerOrException
                            val level = AfflictionPlayerAttachment.getData(player).getWerewolfLevel()
                            context.source.sendSuccess(
                                { Component.literal("Level $level for ${player.name.string}") },
                                true
                            )
                            1
                        }
                    )
                )
            )

    }
}