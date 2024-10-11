package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry

@EmiEntrypoint
class WitcheryEmiPlugin : EmiPlugin {

    override fun register(registry: EmiRegistry) {

    }

    companion object
}