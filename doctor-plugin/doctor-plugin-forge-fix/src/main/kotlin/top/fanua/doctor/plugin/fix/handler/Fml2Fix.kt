package top.fanua.doctor.plugin.fix.handler

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.forge.definations.fml2.AcknowledgementPacket
import top.fanua.doctor.protocol.definition.login.server.LoginPluginRequestPacket

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/26:16:46
 */
class Fml2Fix(client: MinecraftClient) {
    init {
        client.onPacket<LoginPluginRequestPacket> {
            sendPacket(AcknowledgementPacket(packet.messageId))
        }
    }
}
