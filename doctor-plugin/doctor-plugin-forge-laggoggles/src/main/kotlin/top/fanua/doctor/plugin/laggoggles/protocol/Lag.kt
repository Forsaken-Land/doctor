package top.fanua.doctor.plugin.laggoggles.protocol

import top.fanua.doctor.plugin.forge.registry.IModPacketRegistry
import top.fanua.doctor.plugin.laggoggles.definations.*
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:12:40
 */
object Lag : ICommonPacketGroup<IModPacketRegistry> {

    override fun registerPackets(registry: IModPacketRegistry) {
        registry.run {
            modPacketMap("LagGoggles", PacketDirection.C2S)
                .register(5, RequestScanEncoder())
                .register(3, RequestServerDataEncoder())

            modPacketMap("LagGoggles", PacketDirection.S2C)
                .register(4, MessageDecoder())
                .register(2, ServerDataDecoder())
                .register(1, ProfileStatusDecoder())
                .register(0, ScanResultDecoder())

        }

    }
}
