package top.limbang.doctor.plugin.silentgear.handler

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.plugin.silentgear.definations.LoginPacket
import top.limbang.doctor.plugin.silentgear.definations.SyncGearPartsPacket
import top.limbang.doctor.plugin.silentgear.definations.SyncMaterialsPacket
import top.limbang.doctor.plugin.silentgear.definations.SyncTraitsPacket

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
