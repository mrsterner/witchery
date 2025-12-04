package dev.sterner.witchery.content.block.crystal_ball

import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.tarot.TarotPlayerAttachment
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class CrystalBall(properties: Properties) : Block(properties) {

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {

        if (!level.isClientSide) {
            val curseData = CursePlayerAttachment.getData(player)
            if (curseData.playerCurseList.isEmpty()) {
                player.displayClientMessage(
                    Component.translatable(
                        "witchery.curse.free",
                        player.displayName
                    ).withStyle(ChatFormatting.GREEN),
                    false
                )
            } else {
                curseData.playerCurseList.forEach { curse ->
                    player.displayClientMessage(
                        Component.translatable(
                            "witchery.curse.afflicted",
                            player.displayName,
                            Component.translatable("witchery.curse.${curse.curseId.path}.name").withStyle(ChatFormatting.RED)
                        ),
                        false
                    )
                }
            }

            val tarot = TarotPlayerAttachment.getData(player)
            if (tarot.drawnCards.isNotEmpty()) {
                for (i in tarot.drawnCards.indices) {
                    val cardNumber = tarot.drawnCards[i]
                    val isReversed = tarot.reversedCards.getOrNull(i) ?: false
                    val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)

                    val nameText: String = effect?.getDisplayName(isReversed)?.string ?: "Unknown Card"
                    val desc: Component = effect?.getDescription(isReversed) ?: Component.literal("No description.")

                    val nameComponent = if (isReversed && nameText.endsWith("(Reversed)")) {
                        val mainName = nameText.removeSuffix("(Reversed)").trim()
                        val reversedText = "(Reversed)"

                        Component.literal(mainName).withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(" $reversedText").withStyle(ChatFormatting.RED))
                    } else {
                        Component.literal(nameText).withStyle(ChatFormatting.GOLD)
                    }

                    val message = nameComponent.withStyle { style ->
                        style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, desc))
                    }

                    player.displayClientMessage(message, false)
                }
            }

        }

        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    companion object {
        val base = Shapes.create(4.0 / 16, 0.0, 4.0 / 16, 12.0 / 16, 3.0 / 16, 12.0 / 16)
        val glass = Shapes.create(3.5 / 16, 4.0 / 16, 3.5 / 16, 12.5 / 16, 13.0 / 16, 12.5 / 16)

        val SHAPE = Shapes.join(base, glass, BooleanOp.OR)
    }
}