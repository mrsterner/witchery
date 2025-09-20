package dev.sterner.witchery.api.block

import net.minecraft.core.RegistryAccess
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket


interface CustomDataPacketHandlingBlockEntity {

    fun onDataPacket(
        connection: Connection?,
        packet: ClientboundBlockEntityDataPacket?,
        registryAccess: RegistryAccess.Frozen
    )
}