package top.limbang.doctor.plugin.astralsorcery.handler

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.plugin.astralsorcery.definations.PktLoginAcknowledgePacket
import top.limbang.doctor.plugin.astralsorcery.definations.PktLoginSyncDataHolderPacket
import top.limbang.doctor.plugin.astralsorcery.definations.PktLoginSyncGatewayPacket
import top.limbang.doctor.plugin.astralsorcery.definations.PktLoginSyncPerkInformationPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:2:13
 */
class AstralSorceryListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<PktLoginSyncPerkInformationPacket> {
            PktLoginAcknowledgePacket(it.messageId)
        }.replyPacket<PktLoginSyncDataHolderPacket> {
            PktLoginAcknowledgePacket(it.messageId)
        }.replyPacket<PktLoginSyncGatewayPacket> {
            PktLoginAcknowledgePacket(it.messageId)
        }.replyPacket<PktLoginAcknowledgePacket> {
            PktLoginAcknowledgePacket(it.messageId)
        }
    }
}
