package top.limbang.doctor.client.factory

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.protocol.version.autoversion.PingProtocol

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
            version: String,
            eventEmitter: EventEmitter
        ): NetworkManager {
            return NetworkManager.Builder()
                .host(host)
                .port(port)
                .pluginManager(pluginManager)
                .protocolVersion(version)
                .eventEmitter(eventEmitter)
                .build()
        }
}