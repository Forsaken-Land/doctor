package top.fanua.doctor.client.plugin

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.entity.ServerInfo
import top.fanua.doctor.core.api.plugin.Plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
interface ClientPlugin : Plugin {
    fun beforeEnable(serverInfo: ServerInfo) {

    }

    var client: MinecraftClient
}
