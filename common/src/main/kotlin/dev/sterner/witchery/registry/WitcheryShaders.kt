package dev.sterner.witchery.registry

import net.minecraft.client.renderer.ShaderInstance

object WitcheryShaders {
    @JvmField
    var spiritPortal: ShaderInstance? = null

    @JvmField
    var soul_chain: ShaderInstance? = null

    @JvmField
    var spirit_chain: ShaderInstance? = null

    @JvmField
    var soulLantern: ShaderInstance? = null

    @JvmField
    var ghost: ShaderInstance? = null

    @JvmField
    var ether: ShaderInstance? = null
}