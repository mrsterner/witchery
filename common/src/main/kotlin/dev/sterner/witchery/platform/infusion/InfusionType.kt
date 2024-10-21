package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.EntityHitResult

enum class InfusionType : StringRepresentable {
    NONE,
    LIGHT {
        override fun onHoldRightClick(player: Player): Boolean {
            val data = LightInfusionDataAttachment.isInvisible(player)

            if (PlayerInfusionDataAttachment.getInfusionCharge(player) > 200 && !data.isInvisible) {
                LightInfusionDataAttachment.setInvisible(player, true, 10)
                PlayerInfusionDataAttachment.decreaseInfusionCharge(player, 200)
            } else if (PlayerInfusionDataAttachment.getInfusionCharge(player) > 2 && data.invisibleTimer > 4) {
                LightInfusionDataAttachment.setInvisible(player, true, 6)
                PlayerInfusionDataAttachment.decreaseInfusionCharge(player, 2)
            }
            return false
        }
    },
    OVERWORLD {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Boolean {
            return false
        }
    },
    INFERNAL {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Boolean {
            return false
        }
    },
    OTHERWHERE {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Boolean {
            return false
        }
    };

    open fun onHoldRightClick(player: Player): Boolean {
        return false
    }

    open fun onReleaseRightClick(player: Player, secondsHeld: Int): Boolean {
        return false
    }

    open fun leftClickEntity(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Boolean {
        return false
    }

    open fun leftClickBlock(player: Player, blockPos: BlockPos?, direction: Direction?): Boolean {
        return false
    }

    open fun onReleaseRightClickShift(player: Player, secondsHeld: Int): Boolean {
        return false
    }

    open fun leftClickEntityShift(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Boolean {
        return false
    }

    open fun leftClickBlockShift(player: Player, blockPos: BlockPos?, direction: Direction?): Boolean {
        return false
    }

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CODEC: Codec<InfusionType> = StringRepresentable.fromEnum(::values)
    }
}