package top.limbang.doctor.client

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.netty.util.concurrent.Promise
import kotlinx.coroutines.yield
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.old.listener.LoginServiceListener
import top.limbang.doctor.client.utils.newPromise
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.login.server.DisconnectPacket
import top.limbang.doctor.protocol.definition.status.client.RequestPacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket
import top.limbang.doctor.protocol.entity.ServiceResponse
import top.limbang.doctor.protocol.version.autoversion.PingProtocol

/**
 * ### Minecraft 客户端
 */
class MinecraftClient() : EventEmitter by DefaultEventEmitter() {

    fun start(host: String, port: Int) {
        val loginServiceListener = LoginServiceListener("tfgv852@qq.com", "12345678")
            .authServer("https://skin.blackyin.xyz/api/yggdrasil/authserver")
            .sessionServer("https://skin.blackyin.xyz/api/yggdrasil/sessionserver")

        val pluginManager = PluginManager(this)
        val version = ping(host, port).get()
        val mcversion = autoVersion(version)
//        val modlist = autoForge(version)
//        pluginManager.registerPlugin(FMLPlugin(modlist))

        val net = NetworkManager.Builder()
            .host(host)
            .port(port)
            .pluginManager(pluginManager)
            .protocolVersion(mcversion)
            .build()

        net.addListener(LoginListener("tfgv852@qq.com", "12345678").also {
            it.authServer = "https://skin.blackyin.xyz/api/yggdrasil/authserver"
            it.sessionServer = "https://skin.blackyin.xyz/api/yggdrasil/sessionserver"
            it.loginAuthlib()
        })

        net.on(PacketEvent(DisconnectPacket::class)) {
            print(it.reason)
        }

//        net.addListener(HandshakeListener())
//            .addListener(loginServiceListener)
//            .addListener(PingServiceListListener())
        net.connect()
    }

    companion object {

        fun autoVersion(jsonStr: String): String {
            val json = JsonParser.parseString(jsonStr)
            if (!json.isJsonObject) {
                throw JsonParseException("服务器的返回值不是一个json对象")
            }
            val obj = json.asJsonObject
            //原版
            return obj.getAsJsonObject("version").getAsJsonPrimitive("name").asString!!

        }

        fun autoForge(jsonStr: String): List<ServiceResponse.Mod> {
            //TODO：小符号加油
            return Gson().fromJson(
                JsonParser.parseString(jsonStr).asJsonObject
                    .getAsJsonObject("modinfo"),
                ServiceResponse.Modinfo::class.java
            ).modList
        }

        fun ping(host: String, port: Int): Promise<String> {
            return newPromise { result ->
                val net = NetworkManager.Builder()
                    .host(host)
                    .port(port)
                    .protocol(PingProtocol())
                    .build()

                net.once(ConnectionEvent.Connected, this::startPing)
                    .once(PacketEvent(ResponsePacket::class)) {
                        net.shutdown()
                        result.setSuccess(it.json)
                    }
                net.connect()
            }
        }

        private fun startPing(arg: ConnectionEventArgs) {
            val connection = arg.context!!.channel().attr(Attributes.ATTR_CONNECTION).get()
            connection.sendPacket(
                HandshakePacket(0, connection.host, connection.port, ProtocolState.STATUS)
            ).await()
            arg.context!!.setProtocolState(ProtocolState.STATUS)
            connection.sendPacket(RequestPacket())
        }

    }
}