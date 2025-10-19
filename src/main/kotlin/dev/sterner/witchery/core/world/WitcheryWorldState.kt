package dev.sterner.witchery.core.world

import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.*
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.saveddata.SavedData
import kotlin.collections.iterator

class WitcheryWorldState(private val level: ServerLevel) : SavedData() {

    val pendingRestores: MutableMap<GlobalPos, Pair<Int, Map<BlockPos, BlockState>>> = mutableMapOf()
    private val cacheMap: MutableMap<GlobalPos, MutableMap<BlockPos, BlockState>> = mutableMapOf()

    fun getCache(origin: GlobalPos): Map<BlockPos, BlockState>? = cacheMap[origin]

    fun get(origin: GlobalPos, pos: BlockPos): BlockState? = cacheMap[origin]?.get(pos)

    fun put(origin: GlobalPos, pos: BlockPos, state: BlockState) {
        val cache = cacheMap.getOrPut(origin) { mutableMapOf() }
        cache[pos] = state
        setDirty()
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val allCaches = ListTag()

        for ((globalOrigin, stateMap) in cacheMap) {
            val cacheTag = CompoundTag()
            cacheTag.put(
                "origin", GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalOrigin)
                    .result()
                    .orElseThrow { IllegalStateException("Failed to encode GlobalPos $globalOrigin") })

            val stateList = ListTag()
            for ((pos, state) in stateMap) {
                val entry = CompoundTag()
                entry.put("pos", NbtUtils.writeBlockPos(pos))
                entry.put(
                    "state", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state)
                        .result()
                        .orElseThrow { IllegalStateException("Failed to encode BlockState at $pos") })
                stateList.add(entry)
            }

            cacheTag.put("states", stateList)
            allCaches.add(cacheTag)
        }

        tag.put("StateCaches", allCaches)

        val restoreList = ListTag()
        for ((globalPos, pair) in pendingRestores) {
            val (ticks, stateMap) = pair
            val entry = CompoundTag()
            entry.put("pos", GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).result().orElseThrow())
            entry.putInt("ticks", ticks)

            val stateList = ListTag()
            for ((pos, state) in stateMap) {
                val stateTag = CompoundTag()
                stateTag.put("pos", NbtUtils.writeBlockPos(pos))
                stateTag.put(
                    "state", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state)
                        .result()
                        .orElseThrow()
                )
                stateList.add(stateTag)
            }

            entry.put("states", stateList)
            restoreList.add(entry)
        }

        tag.put("PendingRestores", restoreList)
        return tag
    }


    companion object {
        private fun factory(level: ServerLevel): Factory<WitcheryWorldState> {
            return Factory(
                { WitcheryWorldState(level) },
                { tag, lookup -> load(level, tag, lookup) },
                DataFixTypes.LEVEL
            )
        }

        private fun load(level: ServerLevel, tag: CompoundTag, registries: HolderLookup.Provider?): WitcheryWorldState {
            val data = WitcheryWorldState(level)

            val allCaches = tag.getList("StateCaches", Tag.TAG_COMPOUND.toInt())
            for (cacheTag in allCaches) {
                if (cacheTag !is CompoundTag) continue

                val globalPos =
                    GlobalPos.CODEC.parse(NbtOps.INSTANCE, cacheTag.get("origin")).result().orElse(null) ?: continue
                val stateList = cacheTag.getList("states", Tag.TAG_COMPOUND.toInt())
                val stateMap = mutableMapOf<BlockPos, BlockState>()

                for (entry in stateList) {
                    if (entry !is CompoundTag) continue
                    val pos = entry.get("pos")?.let { NbtUtils.readBlockPos(it as CompoundTag, "pos").orElse(null) }
                    val state =
                        entry.get("state")?.let { BlockState.CODEC.parse(NbtOps.INSTANCE, it).result().orElse(null) }

                    if (pos != null && state != null) {
                        stateMap[pos] = state
                    }
                }

                data.cacheMap[globalPos] = stateMap
            }

            val restoreList = tag.getList("PendingRestores", Tag.TAG_COMPOUND.toInt())
            for (entry in restoreList) {
                if (entry !is CompoundTag) continue

                val globalPos =
                    GlobalPos.CODEC.parse(NbtOps.INSTANCE, entry.get("pos")).result().orElse(null) ?: continue
                val ticks = entry.getInt("ticks")
                val stateList = entry.getList("states", Tag.TAG_COMPOUND.toInt())
                val stateMap = mutableMapOf<BlockPos, BlockState>()

                for (stateEntry in stateList) {
                    if (stateEntry !is CompoundTag) continue
                    val pos = NbtUtils.readBlockPos(stateEntry, "pos").orElse(null)
                    val state = BlockState.CODEC.parse(NbtOps.INSTANCE, stateEntry.get("state")).result().orElse(null)
                    if (pos != null && state != null) {
                        stateMap[pos] = state
                    }
                }

                data.pendingRestores[globalPos] = ticks to stateMap
            }

            return data
        }


        fun get(level: ServerLevel): WitcheryWorldState {
            return level.dataStorage.computeIfAbsent(factory(level), "witchery_world_state")
        }
    }
}


