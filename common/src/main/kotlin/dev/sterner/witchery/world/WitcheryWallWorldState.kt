package dev.sterner.witchery.world

import dev.sterner.witchery.handler.VillageWallHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.saveddata.SavedData

class WitcheryWallWorldState : SavedData() {

    val cachedSegments = mutableMapOf<ChunkPos, List<VillageWallHandler.WallSegment>>()

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val listTag = ListTag()
        for ((chunkPos, segments) in cachedSegments) {
            val chunkTag = CompoundTag()
            chunkTag.putInt("x", chunkPos.x)
            chunkTag.putInt("z", chunkPos.z)

            val segmentList = ListTag()
            for (segment in segments) {
                val segTag = CompoundTag()
                segTag.putString("structure_id", segment.structureId.toString())
                segTag.putLong("pos", segment.pos.asLong())
                segTag.putString("rotation", segment.rotation.name)
                segmentList.add(segTag)
            }

            chunkTag.put("segments", segmentList)
            listTag.add(chunkTag)
        }

        tag.put("WallSegments", listTag)
        return tag
    }

    companion object {
        private fun factory(): Factory<WitcheryWallWorldState> {
            return Factory(
                { WitcheryWallWorldState() },
                { tag, _ -> load(tag) },
                DataFixTypes.SAVED_DATA_RAIDS
            )
        }

        fun get(level: ServerLevel): WitcheryWallWorldState {
            return level.dataStorage.computeIfAbsent(factory(), "witchery_wall_world_state")
        }

        fun load(tag: CompoundTag): WitcheryWallWorldState {
            val state = WitcheryWallWorldState()
            val listTag = tag.getList("WallSegments", Tag.TAG_COMPOUND.toInt())

            for (chunkTag in listTag) {
                if (chunkTag is CompoundTag) {
                    val x = chunkTag.getInt("x")
                    val z = chunkTag.getInt("z")
                    val chunkPos = ChunkPos(x, z)

                    val segments = mutableListOf<VillageWallHandler.WallSegment>()
                    val segmentList = chunkTag.getList("segments", Tag.TAG_COMPOUND.toInt())

                    for (segTag in segmentList) {
                        if (segTag is CompoundTag) {
                            val structureId = ResourceLocation.parse(segTag.getString("structure_id"))
                            val pos = BlockPos.of(segTag.getLong("pos"))
                            val rotation = Rotation.valueOf(segTag.getString("rotation"))
                            segments.add(VillageWallHandler.WallSegment(structureId, pos, rotation))
                        }
                    }

                    state.cachedSegments[chunkPos] = segments
                }
            }

            return state
        }
    }
}
