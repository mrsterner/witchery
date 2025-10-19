package dev.sterner.witchery.integration.wthit

import dev.sterner.witchery.content.block.blood_poppy.BloodPoppyBlock
import mcp.mobius.waila.api.IClientRegistrar
import mcp.mobius.waila.api.ICommonRegistrar
import mcp.mobius.waila.api.IWailaClientPlugin
import mcp.mobius.waila.api.IWailaCommonPlugin


class WitcheryWailaPlugin : IWailaClientPlugin, IWailaCommonPlugin {

    override fun register(registrar: IClientRegistrar) {
        registrar.override(SneakyBlockProvider.INSTANCE, BloodPoppyBlock::class.java)
    }

    override fun register(registrar: ICommonRegistrar) {

    }
}