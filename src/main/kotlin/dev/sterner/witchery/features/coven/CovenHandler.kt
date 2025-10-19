package dev.sterner.witchery.features.coven

import dev.sterner.witchery.entity.CovenWitchEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID
import kotlin.collections.get
import kotlin.math.cos
import kotlin.math.sin

object CovenHandler {
    private const val MAX_COVEN_SIZE = 13 // Max total coven size (witches + players)
    private const val MAX_PLAYER_MEMBERS = 8 // Maximum player members in a coven

    /**
     * Adds a Coven-Witch to the player's coven. Discards the Witch
     */
    fun addWitchToCoven(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)
        val witches = data.covenWitches.toMutableList()

        if (getTotalCovenSize(player) >= MAX_COVEN_SIZE) {
            player.displayClientMessage(Component.translatable("witchery.coven.too_large"), true)
            return
        }

        val tag = CompoundTag()
        if (witch.saveAsPassenger(tag)) {
            val witchData = CovenPlayerAttachment.Data.WitchData(
                entityData = tag,
                health = witch.health,
                name = witch.customName ?: Component.literal("Coven Witch #${witches.size + 1}")
            )

            witches.add(witchData)
            CovenPlayerAttachment.setData(player, data.copy(covenWitches = witches))
            CovenPlayerAttachment.sync(player, data.copy(covenWitches = witches))

            witch.discard()

            player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
        }
    }

    /**
     * Add a player to another player's coven
     */
    fun addPlayerToCoven(leader: ServerPlayer, member: ServerPlayer) {
        val data = CovenPlayerAttachment.getData(leader)

        if (getTotalCovenSize(leader) >= MAX_COVEN_SIZE ||
            data.playerMembers.size >= MAX_PLAYER_MEMBERS
        ) {
            leader.displayClientMessage(Component.translatable("witchery.coven.too_large"), true)
            return
        }

        if (data.playerMembers.contains(member.uuid)) {
            leader.displayClientMessage(Component.translatable("witchery.coven.already_member"), true)
            return
        }

        val updatedMembers = data.playerMembers + member.uuid
        val updatedData = data.copy(playerMembers = updatedMembers)

        CovenPlayerAttachment.setData(leader, updatedData)
        CovenPlayerAttachment.sync(leader, updatedData)

        leader.displayClientMessage(
            Component.translatable("witchery.coven.added_player", member.displayName),
            false
        )

        member.displayClientMessage(
            Component.translatable("witchery.coven.joined", leader.displayName),
            false
        )

        leader.level().playSound(
            null,
            leader.blockPosition(),
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }

    /**
     * Remove a player from a coven
     */
    fun removePlayerFromCoven(leader: ServerPlayer, memberUUID: UUID) {
        val data = CovenPlayerAttachment.getData(leader)

        if (!data.playerMembers.contains(memberUUID)) {
            leader.displayClientMessage(Component.translatable("witchery.coven.not_member"), true)
            return
        }

        val updatedMembers = data.playerMembers.filter { it != memberUUID }
        val updatedData = data.copy(playerMembers = updatedMembers)

        CovenPlayerAttachment.setData(leader, updatedData)
        CovenPlayerAttachment.sync(leader, updatedData)
    }

    /**
     * Get all the player's Coven-Witches
     */
    fun getWitchesFromCoven(player: Player): List<CovenWitchEntity> {
        val data = CovenPlayerAttachment.getData(player)
        val level = player.level()

        return data.covenWitches
            .filter { it.isActive }
            .mapNotNull { witchData ->
                EntityType.loadEntityRecursive(witchData.entityData, level) { it } as? CovenWitchEntity
            }
    }

    /**
     * Get all active coven members (players) within range of a position
     */
    fun getActiveCovenPlayers(leader: Player, pos: BlockPos, radius: Double): List<Player> {
        val data = CovenPlayerAttachment.getData(leader)
        val level = leader.level()

        return data.playerMembers.mapNotNull { uuid ->
            level.getPlayerByUUID(uuid)
        }.filter { member ->
            member.isAlive &&
                    member.level() == level &&
                    member.distanceToSqr(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) <= radius * radius
        }
    }

    /**
     * Get total coven size (witches + players)
     */
    fun getTotalCovenSize(player: Player): Int {
        val data = CovenPlayerAttachment.getData(player)
        return data.covenWitches.size + data.playerMembers.size
    }

    /**
     * Get active coven size for ritual requirements (witches + players in range)
     */
    fun getActiveCovenSize(player: Player, ritualPos: BlockPos): Int {
        val activeWitches = getWitchesFromCoven(player).size
        val activePlayers = getActiveCovenPlayers(player, ritualPos, 16.0).size
        return activeWitches + activePlayers
    }

    /**
     * Takes a Coven-Witch from a specific index in the players list of coven witches
     */
    fun summonWitchFromCoven(player: Player, index: Int, summonTo: Vec3): CovenWitchEntity? {
        val data = CovenPlayerAttachment.getData(player)
        val witchData = data.covenWitches.getOrNull(index) ?: return null
        if (!witchData.isActive) return null

        val level = player.level()

        val mob = EntityType.loadEntityRecursive(witchData.entityData.copy(), level) { it } as? CovenWitchEntity
            ?: return null

        mob.health = witchData.health
        mob.moveTo(summonTo.x, summonTo.y, summonTo.z)
        mob.setIsCoven(true)

        level.addFreshEntity(mob)

        return mob
    }

    /**
     * Summon all witches around a ritual circle with better positioning
     */
    fun summonCovenAroundRitual(player: Player, level: Level, ritualPos: BlockPos): Int {
        val covenSize = getWitchesFromCoven(player).size
        if (covenSize == 0) return 0

        val positions = calculateCirclePositions(
            ritualPos.x + 0.5,
            ritualPos.y + 1.0,
            ritualPos.z + 0.5,
            covenSize,
            4.5
        )

        var summonedCount = 0
        for (i in 0 until covenSize) {
            val (x, z) = positions.getOrNull(i) ?: continue
            val targetPos = BlockPos.containing(x, ritualPos.y + 1.0, z)

            val spawnPos = findValidSpawnPosition(level, targetPos) ?: continue

            val witch = summonWitchFromCoven(
                player,
                i,
                Vec3(spawnPos.x + 0.5, spawnPos.y.toDouble(), spawnPos.z + 0.5)
            )

            witch?.let {
                it.setLastRitualPos(Optional.of(ritualPos))
                it.setIsCoven(true)
                summonedCount++
            }
        }

        if (summonedCount > 0 && player is ServerPlayer) {
            val data = CovenPlayerAttachment.getData(player)
            CovenPlayerAttachment.setData(player, data.copy(lastRitualTime = level.gameTime))
            CovenPlayerAttachment.sync(player, data.copy(lastRitualTime = level.gameTime))
        }

        return summonedCount
    }

    /**
     * Update witch health when it changes
     */
    fun updateWitchHealth(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)

        val witchIndex = findWitchIndex(witch, data.covenWitches)
        if (witchIndex == -1) return

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches[witchIndex] = updatedWitches[witchIndex].copy(health = witch.health)

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
    }

    /**
     * Handle witch death - mark as inactive
     */
    fun handleWitchDeath(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)

        val witchIndex = findWitchIndex(witch, data.covenWitches)
        if (witchIndex == -1) return

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches[witchIndex] = updatedWitches[witchIndex].copy(isActive = false)

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
        CovenPlayerAttachment.sync(player, data.copy(covenWitches = updatedWitches))
    }

    /**
     * Resurrect a fallen witch (e.g., via ritual)
     */
    fun resurrectWitch(player: ServerPlayer, index: Int): Boolean {
        val data = CovenPlayerAttachment.getData(player)
        if (index !in data.covenWitches.indices) return false

        val witch = data.covenWitches[index]
        if (witch.isActive) return false

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches[index] = witch.copy(
            isActive = true,
            health = 20f
        )

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
        CovenPlayerAttachment.sync(player, data.copy(covenWitches = updatedWitches))

        return true
    }

    /**
     * Removes a coven-witch from its index
     */
    fun removeWitchFromCoven(player: ServerPlayer, index: Int): Boolean {
        val data = CovenPlayerAttachment.getData(player)
        val witches = data.covenWitches.toMutableList()

        if (index in witches.indices) {
            witches.removeAt(index)
            val updatedData = data.copy(covenWitches = witches)

            CovenPlayerAttachment.setData(player, updatedData)
            CovenPlayerAttachment.sync(player, updatedData)
            return true
        }
        return false
    }

    /**
     * Get number of witches that can be summoned
     */
    fun getSummonableWitchCount(player: Player): Int {
        return CovenPlayerAttachment.getData(player).covenWitches
            .count { it.isActive }
    }

    /**
     * Calculate positions in a circle
     */
    private fun calculateCirclePositions(
        centerX: Double,
        centerY: Double,
        centerZ: Double,
        count: Int,
        radius: Double
    ): List<Pair<Double, Double>> {
        if (count <= 0) return emptyList()

        val positions = mutableListOf<Pair<Double, Double>>()
        val angleIncrement = (2 * Math.PI) / count

        for (i in 0 until count) {
            val angle = i * angleIncrement
            val x = centerX + radius * cos(angle)
            val z = centerZ + radius * sin(angle)
            positions.add(Pair(x, z))
        }

        return positions
    }

    /**
     * Find valid position to spawn a witch
     */
    private fun findValidSpawnPosition(level: Level, origin: BlockPos, radius: Int = 2): BlockPos? {
        if (isSpawnPositionValid(level, origin)) {
            return origin
        }

        for (r in 1..radius) {
            for (dy in -1..1) {
                for (angle in 0 until 8) {
                    val radians = angle * Math.PI / 4
                    val dx = (r * cos(radians)).toInt()
                    val dz = (r * sin(radians)).toInt()
                    val checkPos = origin.offset(dx, dy, dz)

                    if (isSpawnPositionValid(level, checkPos)) {
                        return checkPos
                    }
                }
            }
        }

        return null
    }

    /**
     * Check if position is valid for spawning
     */
    private fun isSpawnPositionValid(level: Level, pos: BlockPos): Boolean {
        return level.getBlockState(pos).canBeReplaced() &&
                level.getBlockState(pos.above()).canBeReplaced() &&
                !level.getBlockState(pos.below()).isAir
    }

    /**
     * Find index of witch in coven data
     */
    private fun findWitchIndex(witch: CovenWitchEntity, witches: List<CovenPlayerAttachment.Data.WitchData>): Int {
        for (i in witches.indices) {
            val tag = witches[i].entityData
            if (tag.hasUUID("UUID") && tag.getUUID("UUID") == witch.uuid) {
                return i
            }
        }

        return -1
    }
}