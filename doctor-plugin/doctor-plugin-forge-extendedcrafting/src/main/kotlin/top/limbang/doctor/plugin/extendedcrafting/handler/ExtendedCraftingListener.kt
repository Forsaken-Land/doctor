package top.limbang.doctor.plugin.extendedcrafting.handler

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.plugin.extendedcrafting.definations.AcknowledgeMessagePacket
import top.limbang.doctor.plugin.extendedcrafting.definations.SyncSingularitiesMessagePacket

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
