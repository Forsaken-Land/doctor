package top.limbang.doctor.plugin.exNihiloSequentia.handler

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.plugin.exNihiloSequentia.definations.HandshakeMessages

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/2:21:38
 */
class ExNihiloSequentiaListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<HandshakeMessages.LoginIndexedMessagePacket> {
            HandshakeMessages.C2SAcknowledgePacket(it.messageId)
        }
    }

}
