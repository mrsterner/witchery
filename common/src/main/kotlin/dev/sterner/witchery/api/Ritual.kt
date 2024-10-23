package dev.sterner.witchery.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

open class Ritual(val id: ResourceLocation) {

    open fun onTickRitual(level: Level, pos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }

    open fun onStartRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }

    open fun onEndRitual(level: Level, blockPos: BlockPos, goldenChalkBlockEntity: GoldenChalkBlockEntity) {

    }

    companion object {
        val CODEC: Codec<Ritual> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Ritual> ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter { ritual -> ritual.id }
            ).apply(instance) { resourceLocation ->
                Ritual(resourceLocation)
            }
        }
    }
}