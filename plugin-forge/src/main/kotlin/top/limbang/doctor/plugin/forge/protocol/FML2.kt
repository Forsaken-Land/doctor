package top.limbang.doctor.plugin.forge.protocol

import top.limbang.doctor.plugin.forge.definations.fml2.*
import top.limbang.doctor.plugin.forge.registry.FML2PacketRegistryImpl
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/16 下午9:51
 */
class FML2 :
    IFML2PacketRegistry by
    FML2PacketRegistryImpl() {
    init {
        fml2PacketMap(PacketDirection.S2C)
            .register(
                1, ModListDecoder()
            ).register(
                3, ServerRegisterDecoder()
            ).register(
                4, ConfigurationDataDecoder()
            )
        fml2PacketMap(PacketDirection.C2S)
            .register(
                2, ModListEncoder()
            ).register(
                99, AcknowledgementEncoder()
            )
    }
}
