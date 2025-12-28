package dev.sterner.witchery.core.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.InventorySlots
import dev.sterner.witchery.core.commands.CurseArgumentType
import dev.sterner.witchery.core.commands.InfusionArgumentType
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity
import dev.sterner.witchery.content.entity.player_shell.SoulShellPlayerEntity.Companion.disableFlight
import dev.sterner.witchery.features.blood.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment
import dev.sterner.witchery.features.necromancy.SoulPoolPlayerAttachment
import dev.sterner.witchery.core.util.WitcheryUtil
import dev.sterner.witchery.data_gen.WitcheryAdvancementProvider
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.curse.CurseHandler
import dev.sterner.witchery.features.familiar.FamiliarHandler
import dev.sterner.witchery.features.spirit_world.ManifestationHandler
import dev.sterner.witchery.features.affliction.lich.LichdomLeveling
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling.levelToBlood
import dev.sterner.witchery.features.affliction.werewolf.WerewolfLeveling
import dev.sterner.witchery.features.coven.CovenHandler
import dev.sterner.witchery.features.coven.CovenPlayerAttachment
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionHandler
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import dev.sterner.witchery.features.petrification.PetrificationHandler
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment
import dev.sterner.witchery.features.possession.EntityAiToggle
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import dev.sterner.witchery.features.tarot.TarotPlayerAttachment
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
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
        val byClass: MutableMap<Class<*>, ArgumentTypeInfo<*, *>> = WitcheryUtil.getByClass()
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
                .then(registerCovenCommands())
                .then(registerTarotCommands())
                .then(registerPetrificationCommands())
                .then(registerDebugCommands())
        )
    }

    private fun registerDebugCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("debug")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("setHunger")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("amount", IntegerArgumentType.integer(0))
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val amount = IntegerArgumentType.getInteger(ctx, "amount")
                                        player.foodData.foodLevel = amount

                                        ctx.source.sendSuccess(
                                            { Component.literal("Set ${player.name.string}'s hunger to $amount") },
                                            true
                                        )
                                        1
                                    }
                            )
                    )
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
                                        InfusionPlayerAttachment.setData(
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
                                val currentInfusion = InfusionPlayerAttachment.getData(player)
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
                                        if (InfusionPlayerAttachment.getData(player).type != InfusionType.NONE) {
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
                                            InfusionPlayerAttachment.setData(
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
                                            WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curseType)!!,
                                            cat
                                        )
                                        1
                                    }
                                    .then(
                                        Commands.argument("power", IntegerArgumentType.integer(0, 13))
                                            .executes { ctx ->
                                                val player = EntityArgument.getPlayer(ctx, "player")
                                                val curseType = CurseArgumentType.getCurse(ctx, "curse")
                                                val power = IntegerArgumentType.getInteger(ctx, "power")
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
                                                    WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curseType)!!,
                                                    cat,
                                                    24000,
                                                    power
                                                )
                                                1
                                            }
                                    )
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
                                        CurseHandler.removeCurse(player, curseType, null, true)
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
                                val curseData = CursePlayerAttachment.getData(player).playerCurseList

                                val message = if (curseData.isEmpty()) {
                                    Component.literal("No curses on ${player.name.string}")
                                } else {
                                    val curseNames = curseData.joinToString(", ") { curse ->
                                        WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.javaClass?.simpleName
                                            ?: curse.curseId.toString()
                                    }
                                    Component.literal("Curses on ${player.name.string}: $curseNames")
                                }

                                ctx.source.sendSuccess({ message }, false)
                                1
                            }
                    )
            )
            .then(
                Commands.literal("clear")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                CurseHandler.removeAllCurses(player)
                                1
                            }
                    )
            )

    }

    private fun registerLichdomCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("lichdom")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("level")
                    .then(
                        Commands.literal("get")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .executes { context ->
                                        val player = context.source.playerOrException
                                        val level =
                                            AfflictionPlayerAttachment.getData(player).getLichLevel()
                                        context.source.sendSuccess(
                                            { Component.literal("Level: $level for ${player.name.string}") },
                                            true
                                        )
                                        1
                                    }
                            ))

                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument("level", IntegerArgumentType.integer(0))
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
            .then(
                Commands.literal("soul")

                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument(
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
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .executes { context ->
                                        val player = context.source.playerOrException

                                        val data = SoulPoolPlayerAttachment.getData(player)
                                        player.sendSystemMessage(Component.literal("Soul Level: " + data.soulPool + "/" + data.maxSouls))
                                        1
                                    })
                    )
            ).then(
                Commands.literal("soul_form")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("enable", BoolArgumentType.bool())
                                .executes { ctx ->
                                    val enable = BoolArgumentType.getBool(ctx, "enable")
                                    val player = ctx.source.playerOrException

                                    val isSoulForm = AfflictionPlayerAttachment.getData(player).isSoulForm()
                                    val isVagrant = AfflictionPlayerAttachment.getData(player).isVagrant()
                                    if (isSoulForm && !isVagrant && !enable) {
                                        disableFlight(player)
                                        InventorySlots.unlockAll(player)
                                        player.abilities.flying = false
                                        player.onUpdateAbilities()

                                        AfflictionPlayerAttachment.smartUpdate(player) {
                                            withSoulForm(false).withVagrant(false)
                                        }
                                    } else if (!isSoulForm && !isVagrant && enable) {
                                        LichdomSpecificEventHandler.activateSoulForm(player)
                                    } else if (isVagrant && enable) {
                                        val possessionComponent = PossessionComponentAttachment.get(player)
                                        val host = possessionComponent.getHost()

                                        if (host != null) {
                                            possessionComponent.stopPossessing(false)
                                            EntityAiToggle.toggleAi(host, EntityAiToggle.POSSESSION_MECHANISM_ID, false, false)
                                        }

                                        AfflictionPlayerAttachment.smartUpdate(player) {
                                            withSoulForm(true).withVagrant(false)
                                        }

                                        InventorySlots.lockAll(player)
                                        SoulShellPlayerEntity.enableFlight(player)
                                        player.abilities.flying = true

                                        val random = player.random
                                        player.deltaMovement = player.deltaMovement.add(
                                            (random.nextDouble() - 0.5) * 0.1,
                                            0.2 + random.nextDouble() * 0.1,
                                            (random.nextDouble() - 0.5) * 0.1
                                        )
                                        player.hurtMarked = true
                                        player.onUpdateAbilities()
                                    }

                                    1
                                }
                            )
                            .then(Commands.literal("cure")
                                .executes { ctx ->
                                    val player = ctx.source.playerOrException
                                    val possess = PossessionComponentAttachment.get(player)
                                    possess.startCuring()

                                    1
                                }
                            )
                    )
            )
    }

    private fun registerVampireCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("vampire")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("level")
                    .then(
                        Commands.literal("get")
                            .then(
                                Commands.argument("player", EntityArgument.player())
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

                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument("level", IntegerArgumentType.integer(0))
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
            .then(
                Commands.literal("blood")

                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument(
                                            "level",
                                            IntegerArgumentType.integer(
                                                0,
                                                levelToBlood(VampireLeveling.LEVEL_REQUIREMENTS.map { it.key }.max())
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
                            .then(
                                Commands.argument("player", EntityArgument.player())
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
            .then(
                Commands.literal("level")
                    .then(
                        Commands.literal("try_curse")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .executes { context ->
                                        val player = context.source.playerOrException
                                        val currentLevel = AfflictionPlayerAttachment.getData(player).getWerewolfLevel()
                                        if (currentLevel == 0) {
                                            WitcheryUtil.grantAdvancementCriterion(player, Witchery.id("werewolf/1"), "impossible_1")
                                            WerewolfLeveling.increaseWerewolfLevel(player)
                                        }
                                        1
                                    }
                            )
                    )
                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("player", EntityArgument.player())
                                    .then(
                                        Commands.argument(
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

                    .then(
                        Commands.literal("get")
                            .then(
                                Commands.argument("player", EntityArgument.player())
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

    private fun registerCovenCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("coven")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("info")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                val data = CovenPlayerAttachment.getData(player)

                                ctx.source.sendSuccess(
                                    { Component.literal("=== Coven Info for ${player.name.string} ===") },
                                    false
                                )

                                val witchCount = data.covenWitches.size
                                val activeWitches = data.covenWitches.count { it.isActive }
                                ctx.source.sendSuccess(
                                    { Component.literal("Coven Witches: $activeWitches/$witchCount active") },
                                    false
                                )

                                if (data.playerMembers.isNotEmpty()) {
                                    ctx.source.sendSuccess(
                                        { Component.literal("Player Members (${data.playerMembers.size}):") },
                                        false
                                    )
                                    data.playerMembers.forEach { memberUuid ->
                                        val member = ctx.source.server.playerList.getPlayer(memberUuid)
                                        val memberName = member?.name?.string ?: "Offline Player"
                                        ctx.source.sendSuccess(
                                            { Component.literal("  - $memberName") },
                                            false
                                        )
                                    }
                                } else {
                                    ctx.source.sendSuccess(
                                        { Component.literal("No player members") },
                                        false
                                    )
                                }

                                1
                            }
                    )
            )
            .then(
                Commands.literal("witches")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                val data = CovenPlayerAttachment.getData(player)

                                if (data.covenWitches.isEmpty()) {
                                    ctx.source.sendSuccess(
                                        { Component.literal("${player.name.string} has no coven witches") },
                                        false
                                    )
                                } else {
                                    ctx.source.sendSuccess(
                                        { Component.literal("=== Coven Witches for ${player.name.string} ===") },
                                        false
                                    )
                                    data.covenWitches.forEachIndexed { index, witch ->
                                        val status = if (witch.isActive) "Active" else "Dead"
                                        val health = if (witch.isActive) "${witch.health.toInt()} HP" else "0 HP"
                                        ctx.source.sendSuccess(
                                            { Component.literal("[$index] ${witch.name.string} - $status ($health)") },
                                            false
                                        )
                                    }
                                }
                                1
                            }
                    )
            )
            .then(
                Commands.literal("members")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                val data = CovenPlayerAttachment.getData(player)

                                if (data.playerMembers.isEmpty()) {
                                    ctx.source.sendSuccess(
                                        { Component.literal("${player.name.string} has no player members in their coven") },
                                        false
                                    )
                                } else {
                                    ctx.source.sendSuccess(
                                        { Component.literal("=== Player Members for ${player.name.string} ===") },
                                        false
                                    )
                                    data.playerMembers.forEach { memberUuid ->
                                        val member = ctx.source.server.playerList.getPlayer(memberUuid)
                                        val memberName = member?.name?.string ?: "Offline"
                                        val status = if (member != null) "Online" else "Offline"
                                        ctx.source.sendSuccess(
                                            { Component.literal("  - $memberName ($status)") },
                                            false
                                        )
                                    }
                                }
                                1
                            }
                    )
            )
            .then(
                Commands.literal("add_player")
                    .then(
                        Commands.argument("leader", EntityArgument.player())
                            .then(
                                Commands.argument("member", EntityArgument.player())
                                    .executes { ctx ->
                                        val leader = EntityArgument.getPlayer(ctx, "leader")
                                        val member = EntityArgument.getPlayer(ctx, "member")

                                        if (CovenHandler.addPlayerToCoven(leader, member)) {
                                            ctx.source.sendSuccess(
                                                { Component.literal("Added ${member.name.string} to ${leader.name.string}'s coven") },
                                                true
                                            )
                                        } else {
                                            ctx.source.sendFailure(
                                                Component.literal("Failed to add ${member.name.string} to coven")
                                            )
                                        }
                                        1
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("remove_player")
                    .then(
                        Commands.argument("leader", EntityArgument.player())
                            .then(
                                Commands.argument("member", EntityArgument.player())
                                    .executes { ctx ->
                                        val leader = EntityArgument.getPlayer(ctx, "leader")
                                        val member = EntityArgument.getPlayer(ctx, "member")

                                        if (CovenHandler.removePlayerFromCoven(leader, member.uuid)) {
                                            ctx.source.sendSuccess(
                                                { Component.literal("Removed ${member.name.string} from ${leader.name.string}'s coven") },
                                                true
                                            )
                                        } else {
                                            ctx.source.sendFailure(
                                                Component.literal("Failed to remove ${member.name.string} from coven")
                                            )
                                        }
                                        1
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("resurrect_witch")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("index", IntegerArgumentType.integer(0))
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val index = IntegerArgumentType.getInteger(ctx, "index")

                                        if (CovenHandler.resurrectWitch(player, index)) {
                                            ctx.source.sendSuccess(
                                                { Component.literal("Resurrected witch at index $index for ${player.name.string}") },
                                                true
                                            )
                                        } else {
                                            ctx.source.sendFailure(
                                                Component.literal("Failed to resurrect witch (invalid index or witch is already alive)")
                                            )
                                        }
                                        1
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("clear")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                val data = CovenPlayerAttachment.getData(player)

                                data.playerMembers.toList().forEach { memberUuid ->
                                    CovenHandler.removePlayerFromCoven(player, memberUuid)
                                }

                                for (i in data.covenWitches.size - 1 downTo 0) {
                                    CovenHandler.removeWitchFromCoven(player, i)
                                }

                                ctx.source.sendSuccess(
                                    { Component.literal("Cleared all coven members for ${player.name.string}") },
                                    true
                                )
                                1
                            }
                    )
            )
    }

    private fun registerTarotCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("tarot")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("apply")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .then(
                                Commands.argument("cardNumber", IntegerArgumentType.integer(1, 22))
                                    .then(
                                        Commands.argument("reversed", BoolArgumentType.bool())
                                            .executes { ctx ->
                                                val player = EntityArgument.getPlayer(ctx, "player")
                                                val cardNumber = IntegerArgumentType.getInteger(ctx, "cardNumber")
                                                val reversed = BoolArgumentType.getBool(ctx, "reversed")
                                                applyTarotCard(ctx, player, cardNumber, reversed)
                                            }
                                    )
                                    .executes { ctx ->
                                        val player = EntityArgument.getPlayer(ctx, "player")
                                        val cardNumber = IntegerArgumentType.getInteger(ctx, "cardNumber")
                                        applyTarotCard(ctx, player, cardNumber, false)
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("clear")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                clearTarotCards(ctx, player)
                            }
                    )
            )
            .then(
                Commands.literal("list")
                    .executes { ctx ->
                        listTarotCards(ctx)
                    }
            )
            .then(
                Commands.literal("info")
                    .then(
                        Commands.argument("player", EntityArgument.player())
                            .executes { ctx ->
                                val player = EntityArgument.getPlayer(ctx, "player")
                                showTarotInfo(ctx, player)
                            }
                    )
            )
    }

    fun registerPetrificationCommands(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("petrification")
            .requires { it.hasPermission(2) }
            .then(
                Commands.literal("apply")
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .then(
                                Commands.argument("duration", IntegerArgumentType.integer(1, 72000))
                                    .executes { ctx ->
                                        applyPetrification(
                                            ctx,
                                            EntityArgument.getEntity(ctx, "target"),
                                            IntegerArgumentType.getInteger(ctx, "duration")
                                        )
                                    }
                            )
                            .executes { ctx ->
                                applyPetrification(
                                    ctx,
                                    EntityArgument.getEntity(ctx, "target"),
                                    6000
                                )
                            }
                    )
            )
            .then(
                Commands.literal("remove")
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes { ctx ->
                                removePetrification(
                                    ctx,
                                    EntityArgument.getEntity(ctx, "target")
                                )
                            }
                    )
            )
            .then(
                Commands.literal("check")
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes { ctx ->
                                checkPetrification(
                                    ctx,
                                    EntityArgument.getEntity(ctx, "target")
                                )
                            }
                    )
            )
    }

    private fun applyPetrification(
        ctx: CommandContext<CommandSourceStack>,
        target: net.minecraft.world.entity.Entity,
        duration: Int
    ): Int {
        if (target !is LivingEntity) {
            ctx.source.sendFailure(Component.literal("Target must be a living entity"))
            return 0
        }

        PetrificationHandler.petrify(target, duration)

        val minutes = duration / 1200
        val seconds = (duration % 1200) / 20

        ctx.source.sendSuccess(
            {
                Component.literal("Petrified ${target.name.string} for ${minutes}m ${seconds}s")
            },
            true
        )

        return 1
    }

    private fun removePetrification(
        ctx: CommandContext<CommandSourceStack>,
        target: net.minecraft.world.entity.Entity
    ): Int {
        if (target !is LivingEntity) {
            ctx.source.sendFailure(Component.literal("Target must be a living entity"))
            return 0
        }

        PetrificationHandler.unpetrify(target)

        ctx.source.sendSuccess(
            {
                Component.literal("Removed petrification from ${target.name.string}")
            },
            true
        )

        return 1
    }

    private fun checkPetrification(
        ctx: CommandContext<CommandSourceStack>,
        target: net.minecraft.world.entity.Entity
    ): Int {
        if (target !is LivingEntity) {
            ctx.source.sendFailure(Component.literal("Target must be a living entity"))
            return 0
        }

        val data = PetrifiedEntityAttachment.getData(target)

        if (!data.isPetrified()) {
            ctx.source.sendSuccess(
                {
                    Component.literal("${target.name.string} is not petrified")
                },
                false
            )
            return 0
        }

        val ticksRemaining = data.petrificationTicks
        val minutes = ticksRemaining / 1200
        val seconds = (ticksRemaining % 1200) / 20

        ctx.source.sendSuccess(
            {
                Component.literal(
                    "${target.name.string} is petrified with ${minutes}m ${seconds}s remaining"
                )
            },
            false
        )

        return 1
    }

    private fun applyTarotCard(
        ctx: CommandContext<CommandSourceStack>,
        target: ServerPlayer,
        cardNumber: Int,
        reversed: Boolean
    ): Int {
        val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)

        if (effect == null) {
            ctx.source.sendFailure(
                Component.literal("Invalid card number: $cardNumber. Must be between 1 and 22.")
                    .withStyle(ChatFormatting.RED)
            )
            return 0
        }

        val currentData = TarotPlayerAttachment.getData(target)
        val newCards = currentData.drawnCards.toMutableList()
        val newReversed = currentData.reversedCards.toMutableList()

        val existingIndex = newCards.indexOf(cardNumber)
        if (existingIndex != -1) {
            newCards.removeAt(existingIndex)
            newReversed.removeAt(existingIndex)
        }

        newCards.add(cardNumber)
        newReversed.add(reversed)

        if (newCards.size > 3) {
            newCards.removeAt(0)
            newReversed.removeAt(0)
        }

        val newData = TarotPlayerAttachment.Data(
            drawnCards = newCards,
            reversedCards = newReversed,
            readingTimestamp = target.level().gameTime
        )

        TarotPlayerAttachment.setData(target, newData)

        val cardName = effect.getDisplayName(reversed).string
        ctx.source.sendSuccess(
            {
                Component.literal("Applied tarot card ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(cardName).withStyle(ChatFormatting.GOLD))
                    .append(" to ${target.name.string}")
            },
            true
        )

        target.displayClientMessage(
            Component.literal("You have been granted: ")
                .withStyle(ChatFormatting.GRAY)
                .append(effect.getDisplayName(reversed).copy().withStyle(ChatFormatting.GOLD)),
            false
        )

        return 1
    }

    private fun clearTarotCards(
        ctx: CommandContext<CommandSourceStack>,
        target: ServerPlayer
    ): Int {
        val newData = TarotPlayerAttachment.Data(
            drawnCards = emptyList(),
            reversedCards = emptyList(),
            readingTimestamp = 0L
        )

        TarotPlayerAttachment.setData(target, newData)

        ctx.source.sendSuccess(
            {
                Component.literal("Cleared all tarot cards from ${target.name.string}")
                    .withStyle(ChatFormatting.GREEN)
            },
            true
        )

        target.displayClientMessage(
            Component.literal("Your tarot cards have been cleared.")
                .withStyle(ChatFormatting.GRAY),
            false
        )

        return 1
    }

    private fun listTarotCards(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.source.sendSuccess(
            {
                Component.literal("=== Available Tarot Cards ===")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            },
            false
        )

        for (i in 1..22) {
            val effect = WitcheryTarotEffects.getByCardNumber(i)
            if (effect != null) {
                ctx.source.sendSuccess(
                    {
                        Component.literal("$i. ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(effect.getDisplayName(false).copy().withStyle(ChatFormatting.WHITE))
                    },
                    false
                )
            }
        }

        return 1
    }

    private fun showTarotInfo(
        ctx: CommandContext<CommandSourceStack>,
        target: ServerPlayer
    ): Int {
        val data = TarotPlayerAttachment.getData(target)

        if (data.drawnCards.isEmpty()) {
            ctx.source.sendSuccess(
                {
                    Component.literal("${target.name.string} has no active tarot cards")
                        .withStyle(ChatFormatting.GRAY)
                },
                false
            )
            return 1
        }

        ctx.source.sendSuccess(
            {
                Component.literal("=== Tarot Cards for ${target.name.string} ===")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            },
            false
        )

        val timeRemaining = TarotPlayerAttachment.THREE_DAYS - (target.level().gameTime - data.readingTimestamp)
        val daysRemaining = timeRemaining / 24000L
        val hoursRemaining = (timeRemaining % 24000L) / 1000L

        ctx.source.sendSuccess(
            {
                Component.literal("Time Remaining: ${daysRemaining}d ${hoursRemaining}h")
                    .withStyle(ChatFormatting.YELLOW)
            },
            false
        )

        data.drawnCards.forEachIndexed { index, cardNumber ->
            val isReversed = data.reversedCards.getOrNull(index) ?: false
            val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)

            if (effect != null) {
                ctx.source.sendSuccess(
                    {
                        Component.literal("${index + 1}. ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(
                                Component.literal(effect.getDisplayName(isReversed).string)
                                    .withStyle(ChatFormatting.WHITE)
                            )
                    },
                    false
                )
                ctx.source.sendSuccess(
                    {
                        Component.literal("   ")
                            .append(effect.getDescription(isReversed).copy().withStyle(ChatFormatting.DARK_GRAY))
                    },
                    false
                )
            }
        }

        return 1
    }
}