package dev.sterner.witchery.util

import com.mojang.serialization.Codec

object CodecUtils {
    val Char.Companion.CODEC: Codec<Char>
        get() = Codec.stringResolver({ char -> char.toString() }, { str -> str.toCharArray()[0] })
}