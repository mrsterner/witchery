package dev.sterner.witchery.integration.wthit

import dev.sterner.witchery.block.blood_poppy.BloodPoppyBlock
import mcp.mobius.waila.api.IClientRegistrar
import mcp.mobius.waila.api.IWailaClientPlugin


class WitcheryWailaPlugin : IWailaClientPlugin {

    override fun register(registrar: IClientRegistrar) {
        registrar.override(SneakyBlockProvider.INSTANCE, BloodPoppyBlock::class.java)
    }
}