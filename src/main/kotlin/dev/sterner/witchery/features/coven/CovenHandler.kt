package dev.sterner.witchery.features.coven

import dev.sterner.witchery.content.entity.CovenWitchEntity
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.Optional
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

object CovenHandler {
    private const val MAX_COVEN_WITCHES = 13
    private const val MAX_PLAYER_MEMBERS = 8

    /**
     * Adds a Coven-Witch to the player's coven. Requires witch to have demon heart.
     */
    fun addWitchToCoven(player: ServerPlayer, witch: CovenWitchEntity): Boolean {
        val witchName = witch.customName ?: CovenDialogue.generateName(witch.random)

        if (!witch.getHasDemonHeart()) {
            player.sendSystemMessage(
                CovenDialogue.getNeedsHeartResponse(witchName, witch.random)
                    .withStyle(ChatFormatting.DARK_RED)
            )
            return false
        }

        val data = CovenPlayerAttachment.getData(player)

        if (data.covenWitches.size >= MAX_COVEN_WITCHES) {
            player.sendSystemMessage(
                CovenDialogue.getCovenFullResponse(witchName, witch.random)
                    .withStyle(ChatFormatting.RED)
            )
            return false
        }

        val tag = CompoundTag()
        if (witch.saveAsPassenger(tag)) {
            val witchData = CovenPlayerAttachment.Data.WitchData(
                entityData = tag,
                health = witch.health,
                name = witchName
            )

            val updatedWitches = data.covenWitches + witchData
            val updatedData = data.copy(covenWitches = updatedWitches)

            CovenPlayerAttachment.setData(player, updatedData)
            CovenPlayerAttachment.sync(player, updatedData)

            val level = player.serverLevel()
            level.sendParticles(
                ParticleTypes.CLOUD,
                witch.x,
                witch.y + witch.bbHeight / 2.0,
                witch.z,
                30,
                0.3,
                0.5,
                0.3,
                0.05
            )
            level.sendParticles(
                ParticleTypes.PORTAL,
                witch.x,
                witch.y + witch.bbHeight / 2.0,
                witch.z,
                20,
                0.3,
                0.5,
                0.3,
                0.1
            )

            witch.discard()

            player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )

            player.sendSystemMessage(
                CovenDialogue.getBindingResponse(witchName, witch.random)
                    .withStyle(ChatFormatting.LIGHT_PURPLE)
            )

            return true
        }

        return false
    }


    /**
     * Add a player to another player's coven
     */
    fun addPlayerToCoven(leader: ServerPlayer, member: ServerPlayer): Boolean {
        val data = CovenPlayerAttachment.getData(leader)

        if (data.playerMembers.size >= MAX_PLAYER_MEMBERS) {
            leader.displayClientMessage(
                Component.translatable("witchery.coven.player_limit"),
                true
            )
            return false
        }

        if (data.playerMembers.contains(member.uuid)) {
            leader.displayClientMessage(
                Component.translatable("witchery.coven.already_member"),
                true
            )
            return false
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

        return true
    }

    /**
     * Remove a player from a coven
     */
    fun removePlayerFromCoven(leader: ServerPlayer, memberUUID: UUID): Boolean {
        val data = CovenPlayerAttachment.getData(leader)

        if (!data.playerMembers.contains(memberUUID)) {
            leader.displayClientMessage(
                Component.translatable("witchery.coven.not_member"),
                true
            )
            return false
        }

        val updatedMembers = data.playerMembers.filter { it != memberUUID }
        val updatedData = data.copy(playerMembers = updatedMembers)

        CovenPlayerAttachment.setData(leader, updatedData)
        CovenPlayerAttachment.sync(leader, updatedData)

        return true
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
     * Get active coven size for ritual requirements (witches + players in range)
     */
    fun getActiveCovenSize(player: Player, ritualPos: BlockPos): Int {
        val data = CovenPlayerAttachment.getData(player)
        val activeWitches = data.covenWitches.count { it.isActive }
        val activePlayers = getActiveCovenPlayers(player, ritualPos, 16.0).size
        return activeWitches + activePlayers + 1
    }

    /**
     * Summon witch from coven at specific index
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
        mob.setOwner(player.uuid)
        mob.resetDespawnTimer()

        level.addFreshEntity(mob)

        return mob
    }

    /**
     * Summon all witches around a ritual circle
     */
    fun summonCovenAroundRitual(player: Player, level: Level, ritualPos: BlockPos): Int {
        val data = CovenPlayerAttachment.getData(player)
        val activeWitches = data.covenWitches.filter { it.isActive }

        if (activeWitches.isEmpty()) return 0

        val positions = calculateCirclePositions(
            ritualPos.x + 0.5,
            ritualPos.y + 1.0,
            ritualPos.z + 0.5,
            activeWitches.size,
            4.5
        )

        var summonedCount = 0
        activeWitches.forEachIndexed { actualIndex, _ ->
            val (x, z) = positions.getOrNull(actualIndex) ?: return@forEachIndexed
            val targetPos = BlockPos.containing(x, ritualPos.y + 1.0, z)

            val spawnPos = findValidSpawnPosition(level, targetPos) ?: return@forEachIndexed

            val witch = summonWitchFromCoven(
                player,
                data.covenWitches.indexOfFirst { it === activeWitches[actualIndex] },
                Vec3(spawnPos.x + 0.5, spawnPos.y.toDouble(), spawnPos.z + 0.5)
            )

            witch?.let {
                it.setLastRitualPos(Optional.of(ritualPos))
                summonedCount++
            }
        }

        if (summonedCount > 0 && player is ServerPlayer) {
            CovenPlayerAttachment.setData(
                player,
                data.copy(lastRitualTime = level.gameTime)
            )
        }

        return summonedCount
    }

    /**
     * Return witch to coven when it despawns naturally
     */
    fun returnWitchToCoven(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)
        val witchIndex = findWitchIndex(witch, data.covenWitches)

        if (witchIndex == -1) return

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches[witchIndex] = updatedWitches[witchIndex].copy(
            health = witch.health.coerceAtLeast(1f)
        )

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
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

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches), sync = false)
    }

    /**
     * Handle witch death - mark as inactive
     */
    fun handleWitchDeath(player: ServerPlayer, witch: CovenWitchEntity) {
        val data = CovenPlayerAttachment.getData(player)
        val witchIndex = findWitchIndex(witch, data.covenWitches)

        if (witchIndex == -1) return

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches[witchIndex] = updatedWitches[witchIndex].copy(
            isActive = false,
            health = 0f
        )

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
        CovenPlayerAttachment.sync(player, data.copy(covenWitches = updatedWitches))

        player.displayClientMessage(
            Component.translatable("witchery.coven.witch_died"),
            false
        )
    }

    /**
     * Resurrect a fallen witch
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
     * Remove witch from coven
     */
    fun removeWitchFromCoven(player: ServerPlayer, index: Int): Boolean {
        val data = CovenPlayerAttachment.getData(player)

        if (index !in data.covenWitches.indices) return false

        val updatedWitches = data.covenWitches.toMutableList()
        updatedWitches.removeAt(index)

        CovenPlayerAttachment.setData(player, data.copy(covenWitches = updatedWitches))
        CovenPlayerAttachment.sync(player, data.copy(covenWitches = updatedWitches))

        return true
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

    fun disbandCovenFromContract(level: ServerLevel, stack: ItemStack) {
        val componentType = WitcheryDataComponents.PLAYER_UUID_ORDERED_LIST.get()
        val contractList = stack.get(componentType)

        if (!contractList.isNullOrEmpty()) {
            val leaderUuid = contractList.first().first
            val leader = level.server.playerList.getPlayer(leaderUuid)

            if (leader is ServerPlayer) {
                val data = CovenPlayerAttachment.getData(leader)

                data.playerMembers
                    .filter { memberUuid -> contractList.any { it.first == memberUuid } }
                    .forEach { memberUuid ->
                        removePlayerFromCoven(leader, memberUuid)
                    }

                val contractWitchNames = contractList.map { it.second }
                val witchesToRemove = data.covenWitches.mapIndexedNotNull { index, witchData ->
                    val witchName = witchData.name.string
                    if (contractWitchNames.any { it.trim() == witchName.trim() }) {
                        index
                    } else null
                }

                for (index in witchesToRemove.sortedDescending()) {
                    removeWitchFromCoven(leader, index)
                }

                leader.displayClientMessage(
                    Component.translatable("witchery.coven.contract_destroyed"),
                    false
                )

                level.playSound(
                    null,
                    leader.blockPosition(),
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.5f
                )

                level.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    leader.x,
                    leader.y + 1.0,
                    leader.z,
                    20,
                    0.5,
                    0.5,
                    0.5,
                    0.05
                )
            }
        }
    }

}