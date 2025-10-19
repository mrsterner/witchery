package dev.sterner.witchery.core.data_attachment.affliction

import com.mojang.serialization.Codec

data class FieldDefinition<T>(
    val path: String,
    val codec: Codec<T>,
    val getter: (AfflictionPlayerAttachment.Data) -> T,
    val setter: (AfflictionPlayerAttachment.Data, T) -> AfflictionPlayerAttachment.Data
)