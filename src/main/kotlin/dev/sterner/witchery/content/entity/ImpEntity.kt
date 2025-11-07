package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.block.soul_cage.SoulCageBlockEntity
import dev.sterner.witchery.content.entity.goal.LookAtTradingPlayerGoal
import dev.sterner.witchery.content.entity.goal.TradeWithPlayerGoal
import dev.sterner.witchery.content.menu.SoulTradingMenu
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.necromancy.EtherealEntityAttachment
import dev.sterner.witchery.network.SyncSoulTradeDataS2CPayload
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

class ImpEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.IMP.get(), level) {

    var tradingPlayer: Player? = null

    init {
        this.moveControl = FlyingMoveControl(this, 20, true)
        this.setPersistenceRequired()
        this.setPathfindingMalus(PathType.WATER, -1.0f)
        this.setPathfindingMalus(PathType.LAVA, 8.0f)
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0f)
    }

    override fun isOnFire(): Boolean {
        return false
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun baseTick() {
        super.baseTick()
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(1, TradeWithPlayerGoal(this))
        goalSelector.addGoal(3, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(4, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(2, LookAtTradingPlayerGoal(this))
        goalSelector.addGoal(6, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(9, RandomLookAroundGoal(this))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        targetSelector.addGoal(3, HurtByTargetGoal(this))
        targetSelector.addGoal(
            4, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
        targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Villager::class.java, true))

        super.registerGoals()
    }

    override fun mobInteract(
        player: Player,
        hand: InteractionHand
    ): InteractionResult {
        if (!level().isClientSide) {
            tradingPlayer = player
            openTradingMenu(player as ServerPlayer)
        }
        return super.mobInteract(player, hand)
    }

    fun ImpEntity.openTradingMenu(player: ServerPlayer) {
        val imp = this@ImpEntity
        player.openMenu(object : MenuProvider {
            override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeInt(id)
                val menu = SoulTradingMenu(containerId, inventory, buf, imp)

                val trades = getAvailableTrades()
                val souls = findNearbySouls(player as ServerPlayer)

                menu.setTrades(trades)
                menu.setSouls(souls)
                imp.tradingPlayer = player

                PacketDistributor.sendToPlayer(
                    player,
                    SyncSoulTradeDataS2CPayload(
                        trades,
                        souls,
                        emptyList(),
                        -1
                    )
                )

                return menu
            }

            override fun getDisplayName(): Component {
                return Component.translatable("container.witchery.soul_trade_menu")
            }
        }) { buf -> buf.writeInt(id) }
    }

    fun ImpEntity.findNearbySouls(player: ServerPlayer): List<SoulTradingMenu.SoulData> {
        val level = player.serverLevel()
        val souls = mutableListOf<SoulTradingMenu.SoulData>()
        val searchRadius = 16.0
        val playerPos = player.blockPosition()
        val searchBox = AABB(playerPos).inflate(searchRadius)

        val entities = level.getEntitiesOfClass(LivingEntity::class.java, searchBox) { entity ->
            entity != null && EtherealEntityAttachment.getData(entity).isEthereal
        }

        for (entity in entities) {
            val entityType = entity.type.toString()

            souls.add(SoulTradingMenu.SoulData(
                entityId = entity.id,
                weight = calculateSoulWeight(entityType),
                entityType = entityType,
                isBlockEntity = false
            ))
        }

        val minPos = BlockPos(
            playerPos.x - searchRadius.toInt(),
            playerPos.y - searchRadius.toInt(),
            playerPos.z - searchRadius.toInt()
        )
        val maxPos = BlockPos(
            playerPos.x + searchRadius.toInt(),
            playerPos.y + searchRadius.toInt(),
            playerPos.z + searchRadius.toInt()
        )

        for (pos in BlockPos.betweenClosed(minPos, maxPos)) {
            val blockEntity = level.getBlockEntity(pos) as? SoulCageBlockEntity
            if (blockEntity != null && blockEntity.hasSoul) {
                souls.add(SoulTradingMenu.SoulData(
                    entityId = pos.asLong().toInt(),
                    weight = 20,
                    entityType = "minecraft:villager",
                    isBlockEntity = true
                ))
            }
        }

        return souls
    }

    fun calculateSoulWeight(entityType: String): Int {
        return when {
            entityType.contains("villager", ignoreCase = true) -> 20
            entityType.contains("pillager", ignoreCase = true) -> 10
            entityType.contains("vindicator", ignoreCase = true) -> 10
            else -> 5
        }
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.10000000149011612)
                .add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        fun getAvailableTrades(): List<SoulTradingMenu.SoulTrade> {
            return listOf(
                SoulTradingMenu.SoulTrade(
                    ItemStack(WitcheryItems.DEMON_HEART.get()), 20
                ),
                SoulTradingMenu.SoulTrade(
                    WitcheryItems.TOE_OF_FROG.get().defaultInstance, 5
                ),
                SoulTradingMenu.SoulTrade(
                    WitcheryItems.WOOL_OF_BAT.get().defaultInstance, 5
                ),
                SoulTradingMenu.SoulTrade(
                    WitcheryItems.TONGUE_OF_DOG.get().defaultInstance,5
                ),
                SoulTradingMenu.SoulTrade(
                    WitcheryItems.OWLETS_WING.get().defaultInstance, 5
                )
            )
        }
    }

    override fun createNavigation(level: Level): PathNavigation {
        val flyingPathNavigation = FlyingPathNavigation(this, level)
        flyingPathNavigation.setCanOpenDoors(false)
        flyingPathNavigation.setCanFloat(true)
        flyingPathNavigation.setCanPassDoors(true)
        return flyingPathNavigation
    }

    override fun travel(travelVector: Vec3) {
        if (this.isControlledByLocalInstance) {
            if (this.isInWater) {
                this.moveRelative(0.02f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.800000011920929)
            } else if (this.isInLava) {
                this.moveRelative(0.02f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.5)
            } else {
                this.moveRelative(this.speed, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = deltaMovement.scale(0.9100000262260437)
            }
        }

        this.calculateEntityAnimation(false)
    }

    override fun checkFallDamage(y: Double, onGround: Boolean, state: BlockState, pos: BlockPos) {
    }

    override fun getAmbientSound(): SoundEvent {
        return if (this.hasItemInSlot(EquipmentSlot.MAINHAND)) SoundEvents.ALLAY_AMBIENT_WITH_ITEM else SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.ALLAY_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ALLAY_DEATH
    }

    override fun getSoundVolume(): Float {
        return 0.4f
    }

    override fun isFlapping(): Boolean {
        return !this.onGround()
    }
}