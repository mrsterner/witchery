package dev.sterner.witchery.item

import dev.sterner.witchery.api.interfaces.VillagerTransfix
import dev.sterner.witchery.block.blood_crucible.BloodCrucibleBlockEntity
import dev.sterner.witchery.block.sacrificial_circle.SacrificialBlockEntity
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment

import dev.sterner.witchery.data_attachment.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.entity.LilithEntity
import dev.sterner.witchery.features.misc.BloodPoolHandler
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling.canPerformQuest
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.core.WitcheryConstants
import net.minecraft.ChatFormatting
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import java.awt.Color

class WineGlassItem(properties: Properties) : Item(properties.stacksTo(1)) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)
        if (livingEntity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(livingEntity, stack)
            livingEntity.awardStat(Stats.ITEM_USED[this])

            if (stack.has(WitcheryDataComponents.VAMPIRE_BLOOD.get()) && stack.get(WitcheryDataComponents.VAMPIRE_BLOOD.get()) == true) {
                val data = AfflictionPlayerAttachment.getData(livingEntity)
                if (data.getVampireLevel() == 0) {
                    VampireLeveling.increaseVampireLevel(player = livingEntity)
                    BloodPoolHandler.increaseBlood(
                        livingEntity = livingEntity,
                        WitcheryConstants.BLOOD_DROP
                    )
                }
            }
        }

        return ItemStack(WitcheryItems.WINE_GLASS.get())
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun getDrinkingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun getEatingSound(): SoundEvent {
        return SoundEvents.HONEY_DRINK
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (player.mainHandItem.get(WitcheryDataComponents.BLOOD.get()) != null) {
            return ItemUtils.startUsingInstantly(level, player, usedHand)
        }

        val data = player.mainHandItem.get(WitcheryDataComponents.BLOOD.get())
        if (data == null && player.isShiftKeyDown && player.offhandItem.`is`(WitcheryItems.BONE_NEEDLE.get())) {
            player.mainHandItem.set(WitcheryDataComponents.BLOOD.get(), player.uuid)
            if (AfflictionPlayerAttachment.getData(player).getVampireLevel() == 10) {
                player.mainHandItem.set(WitcheryDataComponents.VAMPIRE_BLOOD.get(), true)
            }
            player.hurt(level.damageSources().playerAttack(player), 4f)
        }

        return InteractionResultHolder.fail(player.mainHandItem)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player
        val pos = context.clickedPos
        val item = context.itemInHand

        if (level.getBlockEntity(pos) is BloodCrucibleBlockEntity && player != null) {
            val bloodCrucible = level.getBlockEntity(pos) as BloodCrucibleBlockEntity
            bloodCrucible.handleWineGlass(player, item)
            return InteractionResult.SUCCESS
        }

        if (level.getBlockEntity(pos) is SacrificialBlockEntity && level.isNight) {
            val wine = player?.mainHandItem

            val be = level.getBlockEntity(pos) as SacrificialBlockEntity
            if (be.candles.size >= 8 && be.hasSkull) {
                if (wine?.`is`(WitcheryItems.WINE_GLASS.get()) == true) {
                    val hasChickenBlood = wine.has(WitcheryDataComponents.CHICKEN_BLOOD.get())
                    if (hasChickenBlood && wine.get(WitcheryDataComponents.CHICKEN_BLOOD.get()) == true) {
                        wine.set(WitcheryDataComponents.CHICKEN_BLOOD.get(), false)
                        wine.remove(WitcheryDataComponents.BLOOD.get())
                        be.hasSkull = false
                        be.setChanged()
                        summonElle(level, pos, player)
                        return InteractionResult.SUCCESS
                    }
                }
            }
        }

        return super.useOn(context)
    }

    private fun summonElle(level: Level, pos: BlockPos, player: Player) {
        val lightning = EntityType.LIGHTNING_BOLT.create(level)
        lightning!!.moveTo(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
        level.addFreshEntity(lightning)
        val elle = WitcheryEntityTypes.ELLE.get().create(level)
        elle!!.moveTo(pos.x + 0.5, pos.y + 1.4, pos.z + 0.5)
        elle.setOwnerUUID(player.uuid)
        level.addFreshEntity(elle)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val bl = stack.has(WitcheryDataComponents.BLOOD.get())
        val bl2 = stack.has(WitcheryDataComponents.VAMPIRE_BLOOD.get())
        if (bl2 && stack.get(WitcheryDataComponents.VAMPIRE_BLOOD.get()) == true) {
            tooltipComponents.add(
                Component.translatable("witchery.vampire_blood")
                    .setStyle(Style.EMPTY.withColor(Color(255, 50, 100).rgb)).withStyle(ChatFormatting.ITALIC)
            )
        } else if (bl && stack.get(WitcheryDataComponents.BLOOD.get()) != null) {
            tooltipComponents.add(
                Component.translatable("witchery.blood").setStyle(Style.EMPTY.withColor(Color(255, 50, 80).rgb))
            )
        } else {
            tooltipComponents.add(Component.translatable("witchery.use_with_needle"))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (!player.level().isClientSide && interactionTarget is LilithEntity && interactionTarget.entityData.get(
                LilithEntity.IS_DEFEATED
            ) && !interactionTarget.hasUsedLilith
        ) {
            stack.set(WitcheryDataComponents.VAMPIRE_BLOOD.get(), true)
            stack.set(WitcheryDataComponents.BLOOD.get(), interactionTarget.uuid)
            player.setItemInHand(InteractionHand.MAIN_HAND, stack)
            interactionTarget.hasUsedLilith = true
            return InteractionResult.SUCCESS
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand)
    }

    companion object {

        fun applyWineOnVillager(
            event: PlayerInteractEvent.EntityInteract,
            player: Player?,
            entity: Entity?
        ) {
            if (entity is Villager && player != null) {
                val item = player.mainHandItem
                val bl = item.get(WitcheryDataComponents.VAMPIRE_BLOOD.get()) == true
                if (bl && AfflictionPlayerAttachment.getData(player).getVampireLevel() >= 9) {
                    val transfix = entity as VillagerTransfix
                    val blood = BloodPoolLivingEntityAttachment.getData(entity)
                    if (blood.bloodPool <= blood.maxBlood / 2 && transfix.`witchery$isMesmerized`()) {
                        val vampire = WitcheryEntityTypes.VAMPIRE.get().create(player.level())
                        vampire!!.moveTo(entity.position(), entity.xRot, entity.yRot)
                        vampire.setOwnerUUID(player.uuid)
                        vampire.setPersistenceRequired()
                        vampire.creationPos = entity.blockPosition()
                        player.level().addFreshEntity(vampire)
                        entity.discard()
                        if (player is ServerPlayer) {
                            if (canPerformQuest(player, 9)) {
                                VampireLeveling.increaseVampireLevel(player)
                            }
                        }
                        event.isCanceled = true
                    }
                }
            }

        }
    }
}