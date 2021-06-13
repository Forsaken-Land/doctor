package top.limbang.doctor.client.plugin

import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.entity.ServerInfo
import top.limbang.doctor.core.api.plugin.Plugin

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