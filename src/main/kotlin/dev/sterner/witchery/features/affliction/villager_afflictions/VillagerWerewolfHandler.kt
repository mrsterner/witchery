package dev.sterner.witchery.features.affliction.villager_afflictions

import dev.sterner.witchery.content.entity.WerewolfEntity
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player

object VillagerWerewolfHandler {

    @JvmStatic
    fun infectVillager(villager: Villager, attacker: Player?) {
        val data = VillagerDataAttachment.getData(villager)
        if (!data.isWerewolf && data.infectedTicks == 0) {
            VillagerDataAttachment.setData(
                villager,
                data.copy(infectedTicks = 1)
            )
        }
    }

    @JvmStatic
    fun tickInfection(villager: Villager) {
        if (villager.level().isClientSide) return

        val data = VillagerDataAttachment.getData(villager)

        if (data.infectedTicks > 0 && data.infectedTicks < 400) {
            VillagerDataAttachment.setData(
                villager,
                data.copy(infectedTicks = data.infectedTicks + 1)
            )

            if (data.infectedTicks % 80 == 0 && villager.level() is ServerLevel) {
                (villager.level() as ServerLevel).sendParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    villager.x,
                    villager.y + 1.5,
                    villager.z,
                    2,
                    0.3,
                    0.3,
                    0.3,
                    0.0
                )
            }
        } else if (data.infectedTicks >= 400 && !data.isWerewolf) {
            VillagerDataAttachment.setData(
                villager,
                data.copy(isWerewolf = true)
            )

            villager.level().playSound(
                null,
                villager.blockPosition(),
                SoundEvents.WOLF_GROWL,
                SoundSource.HOSTILE,
                0.7f,
                0.5f
            )
        }
    }

    @JvmStatic
    fun shouldTransformToWerewolf(villager: Villager): Boolean {
        val data = VillagerDataAttachment.getData(villager)
        if (!data.isWerewolf) return false

        val level = villager.level()
        return !level.isDay && level.moonPhase == 0
    }

    @JvmStatic
    fun transformToWerewolf(villager: Villager): WerewolfEntity? {
        if (villager.level().isClientSide) return null

        val serverLevel = villager.level() as ServerLevel
        val werewolf = WerewolfEntity(serverLevel)

        werewolf.moveTo(villager.x, villager.y, villager.z, villager.yRot, villager.xRot)

        val villagerNBT = CompoundTag()
        villager.saveWithoutId(villagerNBT)
        werewolf.setVillagerData(villagerNBT)
        werewolf.setFromVillager(true)

        val healthPercent = villager.health / villager.maxHealth
        werewolf.health = werewolf.maxHealth * healthPercent

        serverLevel.sendParticles(
            ParticleTypes.LARGE_SMOKE,
            villager.x,
            villager.y + 1,
            villager.z,
            30,
            0.5,
            0.5,
            0.5,
            0.1
        )

        serverLevel.playSound(
            null,
            villager.blockPosition(),
            SoundEvents.WOLF_HOWL,
            SoundSource.HOSTILE,
            2.0f,
            0.6f
        )

        villager.discard()
        serverLevel.addFreshEntity(werewolf)

        return werewolf
    }

    @JvmStatic
    fun transformToVillager(werewolf: WerewolfEntity): Villager? {
        if (!werewolf.isFromVillager() || werewolf.level().isClientSide) return null

        val serverLevel = werewolf.level() as ServerLevel
        val villagerNBT = werewolf.getVillagerData() ?: return null

        val villager = EntityType.VILLAGER.create(serverLevel) ?: return null
        villager.load(villagerNBT)

        villager.moveTo(werewolf.x, werewolf.y, werewolf.z, werewolf.yRot, werewolf.xRot)

        val healthPercent = werewolf.health / werewolf.maxHealth
        villager.health = villager.maxHealth * healthPercent

        val data = VillagerDataAttachment.getData(villager)
        VillagerDataAttachment.setData(villager, data.copy(infectedTicks = 400))

        serverLevel.sendParticles(
            ParticleTypes.LARGE_SMOKE,
            werewolf.x,
            werewolf.y + 1,
            werewolf.z,
            30,
            0.5,
            0.5,
            0.5,
            0.1
        )

        werewolf.discard()
        serverLevel.addFreshEntity(villager)

        return villager
    }

    @JvmStatic
    fun shouldTransformToVillager(werewolf: WerewolfEntity): Boolean {
        if (!werewolf.isFromVillager()) return false

        val level = werewolf.level()
        return level.isDay || level.moonPhase != 0
    }

    @JvmStatic
    fun tickVillager(villager: Villager){

        tickInfection(villager)

        if (shouldTransformToWerewolf(villager)) {
            transformToWerewolf(villager)
        }
    }

    @JvmStatic
    fun dieVillager(villager: Villager){

    }
}