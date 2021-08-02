package top.fanua.doctor.plugin.astralsorcery.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.astralsorcery.definations.PktLoginAcknowledgePacket
import top.fanua.doctor.plugin.astralsorcery.definations.PktLoginSyncDataHolderPacket
import top.fanua.doctor.plugin.astralsorcery.definations.PktLoginSyncGatewayPacket
import top.fanua.doctor.plugin.astralsorcery.definations.PktLoginSyncPerkInformationPacket

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
