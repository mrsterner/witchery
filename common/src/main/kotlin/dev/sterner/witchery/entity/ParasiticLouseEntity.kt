package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class ParasiticLouseEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.PARASITIC_LOUSE.get(), level) {

    var effect: MobEffectInstance? = null

    override fun interactAt(player: Player, vec: Vec3, hand: InteractionHand): InteractionResult {

        if (player.mainHandItem.`is`(Items.POTION)) {
            val content = player.mainHandItem.get(DataComponents.POTION_CONTENTS)
            val all = content?.allEffects?.iterator()
            if (all != null && all.hasNext()) {
                effect = all.next()
            }
            player.mainHandItem.shrink(1)
            return InteractionResult.SUCCESS
        }

        if (player.mainHandItem.isEmpty) {
            val leech = WitcheryItems.PARASITIC_LOUSE.get().defaultInstance
            leech.set(WitcheryDataComponents.LEECH_EFFECT.get(), effect)

            player.setItemInHand(InteractionHand.MAIN_HAND, leech)
            discard()
        }

        return super.interactAt(player, vec, hand)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("effect")) {
            effect = MobEffectInstance.load(compound.getCompound("effect"))
        }
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        if (effect != null) {
            compound.put("effect", effect!!.save())
        }

        super.addAdditionalSaveData(compound)
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is LivingEntity && effect != null) {
            target.addEffect(effect!!)
            if (level().random.nextFloat() > 0.85) {
                effect = null
            }
        }
        return super.doHurtTarget(target)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(1, ClimbOnTopOfPowderSnowGoal(this, this.level()))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(
            7, LookAtPlayerGoal(
                this,
                Player::class.java, 8.0f
            )
        )
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        targetSelector.addGoal(
            1,
            HurtByTargetGoal(this, *arrayOfNulls(0))
                .setAlertOthers(*arrayOfNulls(0))
        )
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(
                Attributes.MOVEMENT_SPEED, 0.25
            ).add(Attributes.ATTACK_DAMAGE, 2.0)
        }
    }
}