package dev.sterner.witchery.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class CurseArgumentType : ArgumentType<Curse> {

    override fun parse(reader: StringReader): Curse {
        val input = ResourceLocation.read(reader)

        return WitcheryCurseRegistry.CURSES.registry.get().get(input)
            ?: throw CommandSyntaxException(
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                Component.literal("Curse with ID $input does not exist.")
            )
    }

    override fun getExamples(): Collection<String> {
        return WitcheryCurseRegistry.CURSES.entries.map { it.key!!.location().toString().lowercase() }
    }


    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val input = builder.remaining

        WitcheryCurseRegistry.CURSES.entries.forEach { curse ->
            val curseId = curse.get().toString()
            if (curseId.startsWith(input)) {
                builder.suggest(curseId)
            }
        }

        return builder.buildFuture()
    }

    companion object {
        fun curseType(): CurseArgumentType {
            return CurseArgumentType()
        }

        fun getCurse(context: CommandContext<CommandSourceStack>, name: String): Curse {
            return context.getArgument(name, Curse::class.java)
        }
    }
}