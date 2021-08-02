package top.fanua.doctor.plugin.extendedcrafting.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.extendedcrafting.definations.AcknowledgeMessagePacket
import top.fanua.doctor.plugin.extendedcrafting.definations.SyncSingularitiesMessagePacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:51
 */
class ExtendedCraftingListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<SyncSingularitiesMessagePacket> {
            AcknowledgeMessagePacket(it.messageId)
        }.replyPacket<AcknowledgeMessagePacket> {
            AcknowledgeMessagePacket(it.messageId)
        }
    }
}
