package dev.sterner.witchery.block

import com.mojang.logging.LogUtils
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BrushableBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BrushableBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import org.slf4j.Logger
import java.util.*
import kotlin.math.max

class SuspiciousGraveyardDirtBlockEntity(pos: BlockPos?, blockState: BlockState?) :
    BlockEntity(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), pos, blockState) {

    private var brushCount = 0
    private var brushCountResetsAtTick: Long = 0
    private var coolDownEndsAtTick: Long = 0
    var item: ItemStack = ItemStack.EMPTY
        private set
    var hitDirection: Direction? = null
        private set
    private var lootTable: ResourceKey<LootTable>? = null
    private var lootTableSeed: Long = 0

    fun brush(startTick: Long, player: Player, hitDirection: Direction?): Boolean {
        if (this.hitDirection == null) {
            this.hitDirection = hitDirection
        }

        this.brushCountResetsAtTick = startTick + 40L
        if (startTick >= this.coolDownEndsAtTick && level is ServerLevel) {
            this.coolDownEndsAtTick = startTick + 10L
            this.unpackLootTable(player)
            val i = this.completionState
            if (++this.brushCount >= 10) {
                this.brushingCompleted(player)
                return true
            } else {
                level!!.scheduleTick(this.blockPos, blockState.block, 2)
                val j = this.completionState
                if (i != j) {
                    val blockState = this.blockState
                    val blockState2 = blockState.setValue(BlockStateProperties.DUSTED, j)
                    level!!.setBlock(this.blockPos, blockState2, 3)
                }

                return false
            }
        } else {
            return false
        }
    }

    fun unpackLootTable(player: Player) {
        if (this.lootTable != null && (this.level != null) && !level!!.isClientSide() && (level!!.server != null)) {
            val lootTable =
                level!!.server!!.reloadableRegistries().getLootTable(this.lootTable!!)
            if (player is ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger(player, this.lootTable!!)
            }

            val lootParams = LootParams.Builder(level as ServerLevel)
                .withParameter(
                    LootContextParams.ORIGIN, Vec3.atCenterOf(
                        this.worldPosition
                    )
                )
                .withLuck(player.luck)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.CHEST)
            val objectArrayList = lootTable.getRandomItems(lootParams, this.lootTableSeed)

            this.item = when (objectArrayList.size) {
                0 -> ItemStack.EMPTY
                1 -> (objectArrayList[0] as ItemStack)
                else -> {
                    objectArrayList[0]
                }
            }
            this.lootTable = null
            this.setChanged()
        }
    }

    private fun brushingCompleted(player: Player) {
        if (this.level != null && level!!.server != null) {
            this.dropContent(player)
            val blockState = this.blockState
            level!!.levelEvent(3008, this.blockPos, Block.getId(blockState))
            val block2 = if (this.blockState.block is BrushableBlock) {
                val brushableBlock = this.blockState.block as BrushableBlock
                brushableBlock.turnsInto
            } else {
                Blocks.AIR
            }

            level!!.setBlock(this.worldPosition, block2.defaultBlockState(), 3)
        }
    }

    private fun dropContent(player: Player) {
        if (this.level != null && level!!.server != null) {
            this.unpackLootTable(player)
            if (!item.isEmpty) {
                val d = EntityType.ITEM.width.toDouble()
                val e = 1.0 - d
                val f = d / 2.0
                val direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP) as Direction
                val blockPos = worldPosition.relative(direction, 1)
                val g = blockPos.x.toDouble() + 0.5 * e + f
                val h = blockPos.y.toDouble() + 0.5 + (EntityType.ITEM.height / 2.0f).toDouble()
                val i = blockPos.z.toDouble() + 0.5 * e + f
                val itemEntity = ItemEntity(
                    this.level, g, h, i,
                    item.split(
                        level!!.random.nextInt(21) + 10
                    )
                )
                itemEntity.deltaMovement = Vec3.ZERO
                level!!.addFreshEntity(itemEntity)
                this.item = ItemStack.EMPTY
            }
        }
    }

    fun checkReset() {
        if (this.level != null) {
            if (this.brushCount != 0 && level!!.gameTime >= this.brushCountResetsAtTick) {
                val i = this.completionState
                this.brushCount = max(0.0, (this.brushCount - 2).toDouble()).toInt()
                val j = this.completionState
                if (i != j) {
                    level!!.setBlock(
                        this.blockPos,
                        blockState.setValue(BlockStateProperties.DUSTED, j), 3
                    )
                }

                val k = 4
                this.brushCountResetsAtTick = level!!.gameTime + 4L
            }

            if (this.brushCount == 0) {
                this.hitDirection = null
                this.brushCountResetsAtTick = 0L
                this.coolDownEndsAtTick = 0L
            } else {
                level!!.scheduleTick(this.blockPos, blockState.block, 2)
            }
        }
    }

    private fun tryLoadLootTable(tag: CompoundTag): Boolean {
        if (tag.contains("LootTable", 8)) {
            this.lootTable =
                ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(tag.getString("LootTable")))
            this.lootTableSeed = tag.getLong("LootTableSeed")
            return true
        } else {
            return false
        }
    }

    private fun trySaveLootTable(tag: CompoundTag): Boolean {
        if (this.lootTable == null) {
            return false
        } else {
            tag.putString("LootTable", lootTable!!.location().toString())
            if (this.lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", this.lootTableSeed)
            }

            return true
        }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val compoundTag = super.getUpdateTag(registries)
        if (this.hitDirection != null) {
            compoundTag.putInt("hit_direction", hitDirection!!.ordinal)
        }

        if (!item.isEmpty) {
            compoundTag.put("item", item.save(registries))
        }

        return compoundTag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (!this.tryLoadLootTable(tag) && tag.contains("item")) {
            this.item = ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY) as ItemStack
        } else {
            this.item = ItemStack.EMPTY
        }

        if (tag.contains("hit_direction")) {
            this.hitDirection = Direction.entries[tag.getInt("hit_direction")]
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        if (!this.trySaveLootTable(tag) && !item.isEmpty) {
            tag.put("item", item.save(registries))
        }
    }

    fun setLootTable(lootTable: ResourceKey<LootTable>?, seed: Long) {
        this.lootTable = lootTable
        this.lootTableSeed = seed
    }

    private val completionState: Int
        get() = if (this.brushCount == 0) {
            0
        } else if (this.brushCount < 3) {
            1
        } else {
            if (this.brushCount < 6) 2 else 3
        }

    companion object {
        private val LOGGER: Logger = LogUtils.getLogger()
        private const val LOOT_TABLE_TAG = "LootTable"
        private const val LOOT_TABLE_SEED_TAG = "LootTableSeed"
        private const val HIT_DIRECTION_TAG = "hit_direction"
        private const val ITEM_TAG = "item"
        private const val BRUSH_COOLDOWN_TICKS = 10
        private const val BRUSH_RESET_TICKS = 40
        private const val REQUIRED_BRUSHES_TO_BREAK = 10
    }
}
