package dev.sterner.witchery.world

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.*
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.saveddata.SavedData

class WitcheryWorldState(private val level: ServerLevel) : SavedData() {

    val pendingRestores: MutableMap<BlockPos, Pair<Int, Map<BlockPos, BlockState>>> = mutableMapOf()

    private val cacheMap: MutableMap<BlockPos, MutableMap<BlockPos, BlockState>> = mutableMapOf()

    fun getCache(origin: BlockPos): Map<BlockPos, BlockState>? = cacheMap[origin]

    fun get(origin: BlockPos, pos: BlockPos): BlockState? = cacheMap[origin]?.get(pos)

    fun put(origin: BlockPos, pos: BlockPos, state: BlockState) {
        val cache = cacheMap.getOrPut(origin) { mutableMapOf() }
        cache[pos] = state
        setDirty()
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val allCaches = ListTag()

        for ((origin, stateMap) in cacheMap) {
            val cacheTag = CompoundTag()
            cacheTag.put("origin", NbtUtils.writeBlockPos(origin))

            val stateList = ListTag()
            for ((pos, state) in stateMap) {
                val entry = CompoundTag()
                entry.put("pos", NbtUtils.writeBlockPos(pos))
                entry.put("state", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state)
                    .result()
                    .orElseThrow { IllegalStateException("Failed to encode BlockState at $pos") })
                stateList.add(entry)
            }

            cacheTag.put("states", stateList)
            allCaches.add(cacheTag)
        }

        tag.put("StateCaches", allCaches)
        return tag
    }

    companion object {
        private fun factory(level: ServerLevel): Factory<WitcheryWorldState> {
            return Factory(
                { WitcheryWorldState(level) },
                { tag, lookup -> load(level, tag, lookup) },
                DataFixTypes.SAVED_DATA_RAIDS
            )
        }

        private fun load(level: ServerLevel, tag: CompoundTag, registries: HolderLookup.Provider?): WitcheryWorldState {
            val data = WitcheryWorldState(level)
            val allCaches = tag.getList("StateCaches", Tag.TAG_COMPOUND.toInt())

            for (cacheTag in allCaches) {
                if (cacheTag !is CompoundTag) continue

                val stateList = cacheTag.getList("states", Tag.TAG_COMPOUND.toInt())
                val stateMap = mutableMapOf<BlockPos, BlockState>()

                for (entry in stateList) {
                    if (entry !is CompoundTag) continue
                    val pos = entry.get("pos")?.let { NbtUtils.readBlockPos(it as CompoundTag, "pos").orElse(null) }
                    val state = entry.get("state")?.let { BlockState.CODEC.parse(NbtOps.INSTANCE, it).result().orElse(null) }

                    if (pos != null && state != null) {
                        stateMap[pos] = state
                    }

                }

                NbtUtils.readBlockPos(cacheTag, ("origin")).ifPresent { origin ->
                    data.cacheMap[origin] = stateMap
                }

            }

            return data
        }

        fun get(level: ServerLevel): WitcheryWorldState {
            return level.dataStorage.computeIfAbsent(factory(level), "witchery_world_state")
        }
    }
}


