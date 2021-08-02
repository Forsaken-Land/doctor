package top.fanua.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.session.YggdrasilMinecraftSessionService
import top.fanua.doctor.core.api.plugin.Plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-06-14
 */
class MinecraftClientBuilder {
    companion object {

        private val logger: Logger = LoggerFactory.getLogger(MinecraftClientBuilder::class.java)
    }

    private var email: String? = null
    private var password: String? = null
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
        val client: MinecraftClient = if (email != null && password != null) {
            val sessionService = if (authServerUrl != null && sessionServerUrl != null) {
                YggdrasilMinecraftSessionService(authServerUrl!!, sessionServerUrl!!)
            } else {
                YggdrasilMinecraftSessionService()
            }

            val session = sessionService.loginYggdrasilWithPassword(email!!, password!!)

            MinecraftClient(session = session, sessionService = sessionService)
        } else {
            MinecraftClient(name = name)
        }
        //加载插件
        plugins.forEach {
            client.pluginManager.registerPlugin(it)
        }
        return client
    }
}
