package top.fanua.doctor.plugin.forge.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.forge.FML2Plugin
import top.fanua.doctor.plugin.forge.definations.fml2.AcknowledgementPacket
import top.fanua.doctor.plugin.forge.definations.fml2.ConfigurationDataPacket
import top.fanua.doctor.plugin.forge.definations.fml2.ModListPacket
import top.fanua.doctor.plugin.forge.definations.fml2.ServerRegisterPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午8:59
 */
class Forge2HandshakeListener(
    val fmlPlugin: FML2Plugin
) : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.onPacket<ModListPacket> {
            connection.sendPacket(packet)
        }
        emitter.onPacket<ServerRegisterPacket> {
            connection.sendPacket(AcknowledgementPacket(packet.messageId))
        }
        emitter.onPacket<ConfigurationDataPacket> {
            connection.sendPacket(AcknowledgementPacket(packet.messageId))
        }
    }
}
