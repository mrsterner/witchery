package dev.sterner.witchery.content.block

import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.max

class SuspiciousGraveyardDirtBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(WitcheryBlockEntityTypes.BRUSHABLE_BLOCK.get(), pos, blockState) {

    private var brushProgress = 0
    private var brushResetTime: Long = 0
    private var coolDownEndsAtTick: Long = 0
    var storedItem: ItemStack = ItemStack.EMPTY
        private set
    var hitDirection: Direction? = null
        private set
    private var lootTable: ResourceKey<LootTable>? = null
    private var lootTableSeed: Long = 0

    fun brush(startTick: Long, player: Player, hitDirection: Direction?): Boolean {
        if (this.hitDirection == null) {
            this.hitDirection = hitDirection
        }

        this.brushResetTime = startTick + 40L
        if (startTick >= this.coolDownEndsAtTick && level is ServerLevel) {
            this.coolDownEndsAtTick = startTick + 10L
            this.unpackLootTable(player)
            val i = this.completionState
            if (++this.brushProgress >= 10) {
                this.onBrushingCompleted(player)
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

    private fun unpackLootTable(player: Player) {
        if (this.level != null && !level!!.isClientSide() && (level!!.server != null) && this.storedItem == ItemStack.EMPTY) {
            val list = listOf(
                Items.BONE.defaultInstance,
                Items.ROTTEN_FLESH.defaultInstance,
                WitcheryItems.TORN_PAGE.get().defaultInstance,
                Items.SKELETON_SKULL.defaultInstance
            )

            this.storedItem = list[level!!.random.nextInt(list.size - 1)]
            this.lootTable = null
            this.setChanged()
        }
    }

    private fun onBrushingCompleted(player: Player) {
        if (this.level != null && level!!.server != null) {
            this.dropLoot(player)
            val currentBlockState = this.blockState
            level!!.levelEvent(3008, this.blockPos, Block.getId(currentBlockState))
            val replacementBlock = if (this.blockState.block is SuspiciousGraveyardDirtBlock) {
                val graveyardDirtBlock = this.blockState.block as SuspiciousGraveyardDirtBlock
                graveyardDirtBlock.turnsInto
            } else {
                Blocks.AIR
            }

            level!!.setBlock(this.worldPosition, replacementBlock.defaultBlockState(), 3)
        }
    }

    private fun dropLoot(player: Player) {
        if (this.level != null && level!!.server != null) {
            this.unpackLootTable(player)
            if (!storedItem.isEmpty) {
                val entityWidth = EntityType.ITEM.width.toDouble()
                val positionOffset = 1.0 - entityWidth
                val halfWidth = entityWidth / 2.0
                val dropDirection = Objects.requireNonNullElse(this.hitDirection, Direction.UP) as Direction
                val dropPosition = worldPosition.relative(dropDirection, 1)
                val dropX = dropPosition.x.toDouble() + 0.5 * positionOffset + halfWidth
                val dropY = dropPosition.y.toDouble() + 0.5 + (EntityType.ITEM.height / 2.0f).toDouble()
                val dropZ = dropPosition.z.toDouble() + 0.5 * positionOffset + halfWidth

                val itemEntity = ItemEntity(
                    this.level!!, dropX, dropY, dropZ,
                    storedItem.split(
                        level!!.random.nextInt(21) + 10
                    )
                )
                itemEntity.deltaMovement = Vec3.ZERO
                level!!.addFreshEntity(itemEntity)
                this.storedItem = ItemStack.EMPTY
            }
        }
    }

    fun resetBrushingState() {
        if (this.level != null) {
            if (this.brushProgress != 0 && level!!.gameTime >= this.brushResetTime) {
                val oldCompletionState = this.completionState
                this.brushProgress = max(0.0, (this.brushProgress - 2).toDouble()).toInt()
                val newCompletionState = this.completionState
                if (oldCompletionState != newCompletionState) {
                    level!!.setBlock(
                        this.blockPos,
                        blockState.setValue(BlockStateProperties.DUSTED, newCompletionState), 3
                    )
                }
                this.brushResetTime = level!!.gameTime + 4L
            }

            if (this.brushProgress == 0) {
                this.hitDirection = null
                this.brushResetTime = 0L
                this.brushResetTime = 0L
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

        if (!storedItem.isEmpty) {
            compoundTag.put("item", storedItem.save(registries))
        }

        return compoundTag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (!this.tryLoadLootTable(tag) && tag.contains("item")) {
            this.storedItem = ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY) as ItemStack
        } else {
            this.storedItem = ItemStack.EMPTY
        }

        if (tag.contains("hit_direction")) {
            this.hitDirection = Direction.entries[tag.getInt("hit_direction")]
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        if (!this.trySaveLootTable(tag) && !storedItem.isEmpty) {
            tag.put("item", storedItem.save(registries))
        }
    }

    fun setLootTable(lootTable: ResourceKey<LootTable>?, seed: Long) {
        this.lootTable = lootTable
        this.lootTableSeed = seed
    }

    private val completionState: Int
        get() = if (this.brushProgress == 0) {
            0
        } else if (this.brushProgress < 3) {
            1
        } else {
            if (this.brushProgress < 6) 2 else 3
        }
}
