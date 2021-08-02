package top.fanua.doctor.client.factory

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.plugin.PluginManager
import top.fanua.doctor.network.core.NetworkManager
import top.fanua.doctor.protocol.version.ProtocolVersion
import top.fanua.doctor.protocol.version.autoversion.PingProtocol

/**
 * ### 网络管理工厂
 */
object NetworkManagerFactory {
        /**
         * ### 创建 Ping [NetworkManager]
         */
        fun createNetworkManager(host: String, port: Int): NetworkManager {
            return NetworkManager.Builder()
                .host(host)
                .port(port)
                .protocol(PingProtocol())
                .build()
        }

        /**
         * ### 创建 Login [NetworkManager]
         */
        fun createNetworkManager(
            host: String,
            port: Int,
            pluginManager: PluginManager,
            version: Int,
            eventEmitter: EventEmitter
        ): NetworkManager {
            return NetworkManager.Builder()
                .host(host)
                .port(port)
                .pluginManager(pluginManager)
                .protocolVersion(ProtocolVersion.fromNumber(version))
                .eventEmitter(eventEmitter)
                .build()
        }
}
