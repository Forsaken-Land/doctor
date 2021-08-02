package top.fanua.doctor.plugin.silentgear.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.silentgear.definations.LoginPacket
import top.fanua.doctor.plugin.silentgear.definations.SyncGearPartsPacket
import top.fanua.doctor.plugin.silentgear.definations.SyncMaterialsPacket
import top.fanua.doctor.plugin.silentgear.definations.SyncTraitsPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:28
 */
class SilentGearListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<SyncTraitsPacket> {
            LoginPacket(it.messageId)
        }.replyPacket<SyncGearPartsPacket> {
            LoginPacket(it.messageId)
        }.replyPacket<SyncMaterialsPacket> {
            LoginPacket(it.messageId)
        }
    }
}
