package dev.sterner.witchery.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.sterner.witchery.platform.infusion.InfusionType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

class InfusionArgumentType : ArgumentType<InfusionType> {

    override fun parse(reader: StringReader): InfusionType {
        val input = reader.readUnquotedString().lowercase()

        return InfusionType.entries.find { it.serializedName == input }
            ?: throw CommandSyntaxException(
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(),
                Component.literal("Invalid infusion type: $input")
            )
    }

    override fun getExamples(): Collection<String> {
        return InfusionType.entries.map { it.serializedName.lowercase() }
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val input = builder.remaining.lowercase()

        InfusionType.entries.forEach { type ->
            if (type.serializedName.lowercase().startsWith(input)) {
                builder.suggest(type.serializedName.lowercase())
            }
        }

        return builder.buildFuture()
    }

    companion object {
        fun infusionType(): InfusionArgumentType {
            return InfusionArgumentType()
        }

        fun getInfusionType(context: CommandContext<CommandSourceStack>, name: String): InfusionType {
            return context.getArgument(name, InfusionType::class.java)
        }
    }
}