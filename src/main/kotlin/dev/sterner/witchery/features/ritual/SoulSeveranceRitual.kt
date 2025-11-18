package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import dev.sterner.witchery.core.registry.WitcheryTags
import dev.sterner.witchery.features.affliction.AfflictionTypes
import dev.sterner.witchery.features.affliction.ability.AfflictionAbilityHandler
import dev.sterner.witchery.features.affliction.lich.LichdomAbility
import dev.sterner.witchery.features.affliction.lich.LichdomSpecificEventHandler
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

class SoulSeveranceRitual : Ritual("soul_severance") {


    override fun onStartRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ): Boolean {
        if (level.isClientSide) return true

        val target: LivingEntity? = when {
            goldenChalkBlockEntity.targetPlayer != null -> {
                level.server?.playerList?.getPlayer(goldenChalkBlockEntity.targetPlayer!!)
            }
            goldenChalkBlockEntity.targetEntity != null -> {
                level.getEntity(goldenChalkBlockEntity.targetEntity!!) as? LivingEntity
            }
            else -> null
        }

        if (target == null) {
            return false
        }

        if (target.type.`is`(WitcheryTags.NECROMANCER_SUMMONABLE)) {
            val tagCopy = target.saveWithoutId(CompoundTag())
            tagCopy.remove("UUID")
            val deadTargetCopy = target.type.create(level)
            if (deadTargetCopy is LivingEntity) {
                deadTargetCopy.load(tagCopy)
                deadTargetCopy.moveTo(target.x, target.y, target.z, target.yRot, target.xRot)
                deadTargetCopy.copyPosition(target)
                EtherealEntityAttachment.setData(deadTargetCopy, EtherealEntityAttachment.Data())

                EtherealEntityAttachment.setData(
                    deadTargetCopy,
                    EtherealEntityAttachment.Data(
                        null,
                        canDropLoot = false,
                        isEthereal = true,
                        summonTime = level.gameTime,
                        maxLifeTime = 6000L
                    )
                )

                target.kill()
                level.addFreshEntity(deadTargetCopy)
            }
            return true
        }

        if (target is ServerPlayer) {
            AfflictionAbilityHandler.addAbilityOnLevelUp(
                target,
                LichdomAbility.SOUL_FORM.requiredLevel,
                AfflictionTypes.LICHDOM,
                force = true
            )

            LichdomSpecificEventHandler.activateSoulForm(target)

            return true
        }

        return true
    }
}