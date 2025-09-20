package dev.sterner.witchery.entity.sleeping_player

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.mixin.PlayerInvoker
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*


/**
 * Represents data for a sleeping player entity, including their inventory, profile, and other attributes.
 *
 * This class is used to store and manage the state of a sleeping player, including their UUID, inventories,
 * and model data. It provides methods for serializing and deserializing the data to and from NBT.
 */
class SleepingPlayerData(
    /**
     * The unique identifier for the sleeping player. Defaults to a UUID with all zeroes.
     */
    var id: UUID? = UUID(0, 0),

    /**
     * The resolvable profile of the player, used for identifying and resolving the player’s game profile.
     */
    var resolvableProfile: ResolvableProfile? = null,

    /**
     * The main inventory of the player, consisting of 36 item slots.
     */
    var mainInventory: NonNullList<ItemStack> = NonNullList.withSize(36, ItemStack.EMPTY),

    /**
     * The armor inventory of the player, consisting of 4 armor slots.
     */
    var armorInventory: NonNullList<ItemStack> = NonNullList.withSize(4, ItemStack.EMPTY),

    /**
     * The off-hand inventory of the player, consisting of 1 item slot.
     */
    var offHandInventory: NonNullList<ItemStack> = NonNullList.withSize(1, ItemStack.EMPTY),

    /**
     * The equipment inventory of the player, corresponding to all equipment slots.
     */
    var equipment: NonNullList<ItemStack> = NonNullList.withSize(EquipmentSlot.entries.size, ItemStack.EMPTY),

    /**
     * An additional inventory for the player, containing 36 main slots and 9 extra slots.
     */
    var extraInventory: NonNullList<ItemStack> = NonNullList.withSize(36 + 9, ItemStack.EMPTY),

    /**
     * The player's model customization byte, representing visual adjustments or states.
     */
    var model: Byte = 0,
) {

    /**
     * Writes the sleeping player data to an NBT compound.
     *
     * @param lookup A provider for item and entity references used for serialization.
     * @return A [CompoundTag] containing the serialized data of the sleeping player.
     */
    fun writeNbt(lookup: HolderLookup.Provider): CompoundTag {
        val nbt = CompoundTag()
        this.id?.let { nbt.putUUID("Id", it) }

        nbt.put(
            "profile", ResolvableProfile.CODEC.encodeStart(
                NbtOps.INSTANCE,
                resolvableProfile
            ).getOrThrow()
        )

        nbt.putByte("Model", this.model)

        writeInventory(nbt, "Main", this.mainInventory, lookup)
        writeInventory(nbt, "Armor", this.armorInventory, lookup)
        writeInventory(nbt, "Offhand", this.offHandInventory, lookup)
        writeInventory(nbt, "Extra", this.extraInventory, lookup)
        writeInventory(nbt, "Equipment", this.equipment, lookup)

        return nbt
    }

    companion object {
        /**
         * Creates a [SleepingPlayerData] instance from a given [Player].
         *
         * @param player The player to extract data from.
         * @return A [SleepingPlayerData] instance populated with the player's data.
         */
        fun fromPlayer(player: Player): SleepingPlayerData {
            val builder = SleepingPlayerData()
            builder.id = UUID.randomUUID()
            builder.resolvableProfile = ResolvableProfile(player.gameProfile)

            for (i in 0 until builder.mainInventory.size) {
                builder.mainInventory[i] = player.inventory.items[i]
            }

            for (i in 0 until builder.armorInventory.size) {
                builder.armorInventory[i] = player.inventory.armor[i]
            }

            for (i in 0 until builder.offHandInventory.size) {
                builder.offHandInventory[i] = player.inventory.offhand[i]
            }

            for (i in 0 until EquipmentSlot.entries.size) {
                builder.equipment[i] = player.getItemBySlot(EquipmentSlot.entries[i]).copy()
            }

            val playerModeCustomisation: EntityDataAccessor<Byte> = PlayerInvoker.getPlayerModeCustomisationAccessor()
            builder.model = player.entityData.get(playerModeCustomisation)
            return builder
        }

        /**
         * Reads a [SleepingPlayerData] instance from an NBT compound.
         *
         * @param nbt The NBT compound to read from.
         * @param lookup A provider for item and entity references used for deserialization.
         * @return A [SleepingPlayerData] instance populated with data from the NBT.
         */
        fun readNbt(nbt: CompoundTag, lookup: HolderLookup.Provider): SleepingPlayerData {
            val builder = SleepingPlayerData()
            if (nbt.contains("Id")) {
                builder.id = nbt.getUUID("Id")
            } else {
                builder.id = UUID.randomUUID()
            }

            if (nbt.contains("profile")) {
                ResolvableProfile.CODEC
                    .parse(NbtOps.INSTANCE, nbt.get("profile"))
                    .resultOrPartial { string: String? ->
                        Witchery.LOGGER.error(
                            "Failed to load profile from sleeping player: {}",
                            string
                        )
                    }
                    .ifPresent { owner: ResolvableProfile? ->
                        builder.resolvableProfile = owner
                    }
            }

            readInventory(nbt, "Main", builder.mainInventory, lookup)
            readInventory(nbt, "Armor", builder.armorInventory, lookup)
            readInventory(nbt, "Offhand", builder.offHandInventory, lookup)
            readInventory(nbt, "Equipment", builder.equipment, lookup)
            readInventory(nbt, "Extra", builder.extraInventory, lookup)

            builder.model = nbt.getByte("Model")
            return builder
        }

        /**
         * Reads inventory data from an NBT compound and populates a given inventory.
         *
         * @param compound The NBT compound containing the inventory data.
         * @param name The key under which the inventory data is stored.
         * @param inv The inventory to populate.
         * @param lookup A provider for item and entity references used for deserialization.
         */
        private fun readInventory(
            compound: CompoundTag,
            name: String,
            inv: NonNullList<ItemStack>,
            lookup: HolderLookup.Provider
        ) {
            if (compound.contains(name, 9)) {
                val listTag = compound.getList(name, 10)

                for (i in inv.indices) {
                    val compoundTag = listTag.getCompound(i)
                    inv[i] = ItemStack.parseOptional(lookup, compoundTag)
                }
            }
        }

        /**
         * Writes inventory data to an NBT compound.
         *
         * @param nbt The NBT compound to write to.
         * @param key The key under which to store the inventory data.
         * @param inventory The inventory to serialize.
         * @param lookup A provider for item and entity references used for serialization.
         */
        private fun writeInventory(
            nbt: CompoundTag,
            key: String,
            inventory: NonNullList<ItemStack>,
            lookup: HolderLookup.Provider
        ) {
            val listTag = ListTag()

            for (itemStack in inventory) {
                if (!itemStack.isEmpty) {
                    listTag.add(itemStack.save(lookup))
                } else {
                    listTag.add(CompoundTag())
                }
            }

            nbt.put(key, listTag)
        }
    }
}