package top.fanua.doctor.plugin.silentgems.handler

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.silentgems.definations.LoginPacket
import top.fanua.doctor.plugin.silentgems.definations.SyncChaosBuffsPacket
import top.fanua.doctor.plugin.silentgems.definations.SyncSoulsPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 21:03
 */
class SilentGemsListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<SyncSoulsPacket> {
            LoginPacket(it.messageId)
        }.replyPacket<SyncChaosBuffsPacket> {
            LoginPacket(it.messageId)
        }
    }
}
