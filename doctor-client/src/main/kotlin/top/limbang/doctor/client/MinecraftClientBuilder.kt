package top.limbang.doctor.client

import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.core.api.plugin.Plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-06-14
 */
class MinecraftClientBuilder {

    private var email: String = ""
    private var password: String = ""
    private var name: String = ""
    private var authServerUrl: String? = null
    private var sessionServerUrl: String? = null

    private val plugins = mutableSetOf<Plugin>()

    /**
     * ### 设置在线登录
     */
    fun user(email: String, password: String): MinecraftClientBuilder {
        this.email = email
        this.password = password
        return this
    }

    /**
     * ### 设置离线登录名称
     */
    fun name(name: String): MinecraftClientBuilder {
        this.name = name
        return this
    }

    /**
     * ### 设置外置登录 验证地址
     */
    fun authServerUrl(url: String): MinecraftClientBuilder {
        this.authServerUrl = url
        return this
    }

    /**
     * ### 设置外置登录 session地址
     */
    fun sessionServerUrl(url: String): MinecraftClientBuilder {
        this.sessionServerUrl = url
        return this
    }

    /**
     * ### 添加插件
     */
    fun plugin(plugin: Plugin): MinecraftClientBuilder {
        plugins.add(plugin)
        return this
    }

    fun build(): MinecraftClient {
        val result: MinecraftClient = if (authServerUrl != null && sessionServerUrl != null) {
            val sessionService = YggdrasilMinecraftSessionService(authServerUrl!!, sessionServerUrl!!)
            MinecraftClient(email, password, name, sessionService)
        } else {
            MinecraftClient(email, password, name)
        }
        plugins.forEach {
            result.pluginManager.registerPlugin(it)
        }

        return result
    }
}