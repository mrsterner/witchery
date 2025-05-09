package dev.sterner.witchery.handler

import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.entity.NightmareEntity
import dev.sterner.witchery.platform.NightmarePlayerAttachment.Data
import dev.sterner.witchery.platform.NightmarePlayerAttachment.getData
import dev.sterner.witchery.platform.NightmarePlayerAttachment.setData
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.levelgen.Heightmap
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

object NightmareHandler {

    fun registerEvents() {
        TickEvent.PLAYER_PRE.register(NightmareHandler::tick)
    }

    fun tick(player: Player?) {
        if (player?.level()?.dimension() == WitcheryWorldgenKeys.NIGHTMARE && player.level() is ServerLevel) {
            val data = getData(player)
            val level = player.level()
            if (!data.hasNightmare) {
                val nightmare = WitcheryEntityTypes.NIGHTMARE.get().create(level)
                nightmare!!.entityData.set(NightmareEntity.NIGHTMARE_TARGET, Optional.of(player.uuid))

                val distance = 16 + level.random.nextInt(8)
                val angle = level.random.nextDouble() * Math.PI * 2

                val offsetX = (cos(angle) * distance).toInt()
                val offsetZ = (sin(angle) * distance).toInt()
                val spawnX = player.blockX + offsetX
                val spawnZ = player.blockZ + offsetZ

                val spawnY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ)
                nightmare.moveTo(spawnX + 0.5, spawnY.toDouble() + 1, spawnZ + 0.5)


                level.addFreshEntity(nightmare)
                setData(player, Data(true, Optional.of(nightmare.uuid)))
            }

            if (level is ServerLevel) {

                val nightmareUuid = data.nightmareUUID
                if (nightmareUuid.isPresent) {
                    val nightmare = level.getEntity(nightmareUuid.get())
                    if (nightmare is NightmareEntity) {
                        if (nightmare.touchingUnloadedChunk()) {
                            nightmare.discard()
                            setData(player, Data(false, Optional.empty()))
                        }
                    } else {
                        nightmare?.discard()
                        setData(player, Data(false, Optional.empty()))
                    }
                }

            }
        }
    }
}