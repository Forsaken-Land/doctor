package top.fanua.doctor.plugin.environmental.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.environmental.definations.AcknowledgeEnvironmentalMessagePacket
import top.fanua.doctor.plugin.environmental.definations.SyncBackpackTypeMessagePacket
import top.fanua.doctor.plugin.environmental.definations.SyncSlabfishTypeMessagePacket
import top.fanua.doctor.plugin.environmental.definations.SyncSweaterTypeMessagePacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/24:0:24
 */
class EnvironmentalListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<SyncSweaterTypeMessagePacket> {
            AcknowledgeEnvironmentalMessagePacket(it.messageId)
        }.replyPacket<SyncBackpackTypeMessagePacket> {
            AcknowledgeEnvironmentalMessagePacket(it.messageId)
        }.replyPacket<SyncSlabfishTypeMessagePacket> {
            AcknowledgeEnvironmentalMessagePacket(it.messageId)
        }
    }
}
