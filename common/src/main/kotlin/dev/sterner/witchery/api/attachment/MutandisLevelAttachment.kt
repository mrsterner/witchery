package dev.sterner.witchery.api.attachment

import net.minecraft.core.BlockPos

data class MutandisLevelAttachment(val mutandisCacheMap: MutableMap<BlockPos, MutandisData> = mutableMapOf())