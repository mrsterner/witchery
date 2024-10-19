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
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Int {
            return 0
        }
    },
    OVERWORLD {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Int {
            return 0
        }
    },
    INFERNAL {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Int {
            return 0
        }
    },
    OTHERWHERE {
        override fun onReleaseRightClick(player: Player, secondsHeld: Int): Int {
            return 0
        }
    };


    open fun onReleaseRightClick(player: Player, secondsHeld: Int): Int {
        return 0
    }

    open fun leftClickEntity(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Int {
        return 0
    }

    open fun leftClickBlock(player: Player, blockPos: BlockPos?, direction: Direction?): Int {
        return 0
    }
    open fun onReleaseRightClickShift(player: Player, secondsHeld: Int): Int {
        return 0
    }

    open fun leftClickEntityShift(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Int {
        return 0
    }

    open fun leftClickBlockShift(player: Player, blockPos: BlockPos?, direction: Direction?): Int {
        return 0
    }

    override fun getSerializedName(): String {
        return name.lowercase()
    }



    companion object {
        val CODEC: Codec<InfusionType> = StringRepresentable.fromEnum(::values)
    }
}