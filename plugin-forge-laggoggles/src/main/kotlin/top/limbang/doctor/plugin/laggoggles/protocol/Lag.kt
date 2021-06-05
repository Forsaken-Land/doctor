package top.limbang.doctor.plugin.laggoggles.protocol

import top.limbang.doctor.plugin.forge.registry.IModPacketRegistry
import top.limbang.doctor.plugin.laggoggles.definations.RequestScanEncoder
import top.limbang.doctor.plugin.laggoggles.definations.RequestServerDataEncoder
import top.limbang.doctor.plugin.laggoggles.definations.ServerDataDecoder
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.ICommonPacketGroup

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
                .register(2, ServerDataDecoder())
//                .register(0, RequestScanDecoder())


        }

    }
}
