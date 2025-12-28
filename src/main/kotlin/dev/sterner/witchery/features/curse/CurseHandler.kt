package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryPowerHelper
import dev.sterner.witchery.core.api.event.CurseEvent
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import dev.sterner.witchery.core.registry.WitcheryPoppetRegistry
import dev.sterner.witchery.features.hunter.HunterArmorDefenseHandler
import dev.sterner.witchery.features.hunter.HunterArmorParticleEffects
import dev.sterner.witchery.features.poppet.PoppetHandler
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.NeoForge

object CurseHandler {

    fun addCurse(
        player: Player,
        sourcePlayer: ServerPlayer?,
        curse: ResourceLocation,
        catBoosted: Boolean,
        duration: Int = 24000,
        witchPower: Int? = null
    ): Boolean {

        val result = CurseEvent.Added(player, sourcePlayer, curse, catBoosted)
        NeoForge.EVENT_BUS.post(result)
        if (result.isCanceled) {
            return false
        }

        val voodooProtectionPoppet = WitcheryPoppetRegistry.VOODOO_PROTECTION.get()
        val (foundPoppet, _) = PoppetHandler.findPoppet(player, voodooProtectionPoppet)

        if (foundPoppet != null) {
            val protected = PoppetHandler.activatePoppet(player, voodooProtectionPoppet, null)

            if (protected) {
                if (sourcePlayer != null && player.level() is ServerLevel) {
                    val serverLevel = player.level() as ServerLevel

                    val lightning = LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel)
                    lightning.moveTo(sourcePlayer.x, sourcePlayer.y, sourcePlayer.z)
                    lightning.setCause(player as? ServerPlayer)
                    serverLevel.addFreshEntity(lightning)

                    serverLevel.playSound(
                        null,
                        sourcePlayer.x, sourcePlayer.y, sourcePlayer.z,
                        SoundEvents.LIGHTNING_BOLT_THUNDER,
                        SoundSource.WEATHER,
                        1.0f,
                        1.0f
                    )
                }

                if (player.level() is ServerLevel) {
                    val serverLevel = player.level() as ServerLevel
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        player.x,
                        player.y + player.bbHeight * 0.5,
                        player.z,
                        20,
                        0.5,
                        0.5,
                        0.5,
                        0.1
                    )
                }

                return false
            }
        }

        val hunterPieceCount = HunterArmorDefenseHandler.getHunterArmorPieceCount(player)
        if (hunterPieceCount == 4) {
            val random = player.level().random

            if (random.nextFloat() < 0.5f) {
                if (player.level() is ServerLevel) {
                    val serverLevel = player.level() as ServerLevel
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        player.x,
                        player.y + player.bbHeight * 0.5,
                        player.z,
                        15,
                        0.4,
                        0.4,
                        0.4,
                        0.1
                    )
                }

                HunterArmorParticleEffects.spawnProtectionParticles(
                    player,
                    HunterArmorParticleEffects.ProtectionType.CURSE_REDUCTION
                )

                if (random.nextFloat() < 0.25f && sourcePlayer != null) {
                    val sourceData = CursePlayerAttachment.getData(sourcePlayer).playerCurseList.toMutableList()
                    val existingSourceCurse = sourceData.find { it.curseId == curse }

                    if (existingSourceCurse != null) {
                        sourceData.remove(existingSourceCurse)
                    }

                    val reflectedCurseData = CursePlayerAttachment.PlayerCurseData(
                        curse,
                        duration = duration,
                        catBoosted = catBoosted
                    )
                    sourceData.add(reflectedCurseData)

                    CursePlayerAttachment.setData(sourcePlayer, CursePlayerAttachment.Data(sourceData))

                    WitcheryCurseRegistry.CURSES_REGISTRY[reflectedCurseData.curseId]?.onAdded(
                        sourcePlayer.level(),
                        sourcePlayer,
                        reflectedCurseData.catBoosted
                    )

                    if (player is ServerPlayer) {
                        HunterArmorParticleEffects.spawnCurseReflectionEffect(player, sourcePlayer)
                        HunterArmorParticleEffects.spawnProtectionParticles(
                            player,
                            HunterArmorParticleEffects.ProtectionType.CURSE_REFLECTION
                        )

                        NeoForge.EVENT_BUS.post(CurseEvent.Added(sourcePlayer, player, curse, catBoosted))
                    }

                }

                return false
            }
        }

        val actualWitchPower = witchPower ?: if (sourcePlayer != null) {
            WitcheryPowerHelper.calculateWitchPower(sourcePlayer)
        } else {
            0
        }

        val data = CursePlayerAttachment.getData(player).playerCurseList.toMutableList()
        val existingCurse = data.find { it.curseId == curse }

        if (existingCurse != null) {
            val existingTotalPower = existingCurse.witchPower + existingCurse.failedRemovalAttempts
            if (actualWitchPower <= existingTotalPower) {
                return false
            }
            data.remove(existingCurse)
        }

        val scaledDuration = calculateCurseDuration(duration, actualWitchPower)
        val adjustedDuration = HunterArmorDefenseHandler.reduceCurseDuration(player, scaledDuration)

        val newCurseData = CursePlayerAttachment.PlayerCurseData(
            curse,
            duration = adjustedDuration,
            catBoosted = catBoosted,
            witchPower = actualWitchPower,
            failedRemovalAttempts = 0
        )
        data.add(newCurseData)

        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(data))

        WitcheryCurseRegistry.CURSES_REGISTRY[newCurseData.curseId]?.onAdded(
            player.level(),
            player,
            newCurseData.catBoosted
        )

        return true
    }

    private fun calculateCurseDuration(baseDuration: Int, witchPower: Int): Int {
        val multiplier = 1.0 + (witchPower / 13.0) * 2.0
        return (baseDuration * multiplier).toInt()
    }

    fun removeCurse(player: Player, curse: Curse, removerPlayer: Player? = null, force: Boolean): Boolean {
        val data = CursePlayerAttachment.getData(player)
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
        val curseIndex = data.playerCurseList.indexOfFirst { it.curseId == curseId }

        if (curseIndex == -1) return false

        val curseData = data.playerCurseList[curseIndex]

        val removerWitchPower = if (removerPlayer != null) {
            WitcheryPowerHelper.calculateWitchPower(removerPlayer)
        } else {
            0
        }

        //DnD me
        val criticalSuccessThreshold = (20 - curseData.failedRemovalAttempts).coerceAtLeast(1)
        val roll = player.level().random.nextInt(1, 21)
        val isCriticalSuccess = roll >= criticalSuccessThreshold

        if (!WitcheryPowerHelper.canRemoveCurse(
                removerWitchPower,
                curseData.witchPower,
                curseData.failedRemovalAttempts
            ) && !isCriticalSuccess
        ) {

            val updatedCurseData = curseData.copy(
                failedRemovalAttempts = curseData.failedRemovalAttempts + 1
            )

            val updatedList = data.playerCurseList.toMutableList()
            updatedList[curseIndex] = updatedCurseData

            CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(updatedList))

            if (player.level() is ServerLevel) {
                val level = player.level() as ServerLevel
                level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.PLAYERS,
                    1.0f,
                    0.5f
                )

                level.sendParticles(
                    ParticleTypes.SMOKE,
                    player.x,
                    player.y + 1.0,
                    player.z,
                    20,
                    0.5,
                    0.5,
                    0.5,
                    0.05
                )
            }

            if (!force) {
                return false
            }
        }

        curse.onRemoved(player.level(), player, curseData.catBoosted)

        val updatedList = data.playerCurseList.toMutableList()
        updatedList.removeAt(curseIndex)
        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(updatedList))

        return true
    }

    fun removeAllCurses(player: Player): Int {
        val data = CursePlayerAttachment.getData(player).playerCurseList
        if (data.isEmpty()) return 0

        val count = data.size
        val level = player.level()

        data.forEach { curseData ->
            WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onRemoved(
                level,
                player,
                curseData.catBoosted
            )
        }

        CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(mutableListOf()))

        return count
    }

    fun hasCurse(player: Player, curse: Curse): Boolean {
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.any { it.curseId == curseId }
    }

    fun getCurseDuration(player: Player, curse: Curse): Int {
        val curseId = WitcheryCurseRegistry.CURSES_REGISTRY.getKey(curse)
        return CursePlayerAttachment.getData(player).playerCurseList.find { it.curseId == curseId }?.duration ?: -1
    }

    fun tickCurse(player: Player?) {
        if (player == null) {
            return
        }

        val data = CursePlayerAttachment.getData(player)
        if (data.playerCurseList.isEmpty()) {
            return
        }

        var dataModified = false
        val curses = data.playerCurseList.toMutableList()
        val iterator = curses.iterator()

        while (iterator.hasNext()) {
            val curseData = iterator.next()

            if (curseData.duration > 0) {

                WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onTickCurse(
                    player.level(),
                    player,
                    curseData.catBoosted
                )

                curseData.duration -= 1
                dataModified = true
            }

            if (curseData.duration <= 0) {

                WitcheryCurseRegistry.CURSES_REGISTRY[curseData.curseId]?.onRemoved(
                    player.level(),
                    player,
                    curseData.catBoosted
                )

                iterator.remove()
                dataModified = true
            }
        }

        if (dataModified) {
            CursePlayerAttachment.setData(player, CursePlayerAttachment.Data(curses))
        }
    }

    fun onHurt(
        livingEntity: LivingEntity?,
        damageSource: DamageSource?,
        amount: Float
    ) {
        if (livingEntity !is Player || damageSource == null) {
            return
        }

        val data = CursePlayerAttachment.getData(livingEntity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.onHurt(
                livingEntity.level(),
                livingEntity,
                damageSource,
                amount,
                curse.catBoosted
            )
        }
    }

    fun breakBlock(
        level: Level?,
        blockState: BlockState,
        serverPlayer: Player?,
    ) {
        if (serverPlayer == null || level == null) {
            return
        }

        val data = CursePlayerAttachment.getData(serverPlayer)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.breakBlock(
                level,
                serverPlayer,
                blockState,
                curse.catBoosted
            )
        }

        return
    }

    fun placeBlock(
        level: Level?,
        blockState: BlockState?,
        entity: Entity?
    ) {
        if (level == null || blockState == null || entity !is Player) {
            return
        }

        val data = CursePlayerAttachment.getData(entity)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.placeBlock(
                level,
                entity,
                blockState,
                curse.catBoosted
            )
        }
    }

    fun attackEntity(
        player: Player?,
        level: Level?,
        target: Entity?
    ) {
        if (player == null || level == null || target == null) {
            return
        }

        val data = CursePlayerAttachment.getData(player)
        for (curse in data.playerCurseList) {
            WitcheryCurseRegistry.CURSES_REGISTRY[curse.curseId]?.attackEntity(
                level,
                player,
                target,
                curse.catBoosted
            )
        }
    }

    fun getActiveCurses(player: Player): List<ResourceLocation> {
        return CursePlayerAttachment.getData(player).playerCurseList.map { it.curseId }
    }
}