package dev.sterner.witchery.api.attachment

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

data class MutandisData(val tag: TagKey<Block>, val time: Int)