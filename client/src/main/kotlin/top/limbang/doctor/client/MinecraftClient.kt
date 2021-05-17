package top.limbang.doctor.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.netty.util.concurrent.Promise
import io.reactivex.rxjava3.core.Observable
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.LoginSuccessEvent
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.listener.PlayListener
import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.client.utils.newPromise
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.play.client.CKeepAlivePacket
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.definition.status.client.RequestPacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket
import top.limbang.doctor.protocol.entity.ServiceResponse
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer
import top.limbang.doctor.protocol.version.autoversion.PingProtocol
import java.util.concurrent.TimeUnit

/**
 * ### Minecraft 客户端
 */
class MinecraftClient() : EventEmitter by DefaultEventEmitter() {
    val logger = LoggerFactory.getLogger(this.javaClass)
    fun start(host: String, port: Int) {
        val pluginManager = PluginManager(this)
        val version = ping(host, port).get()

        val suffix = autoForge(version, pluginManager)

        val sessionService = YggdrasilMinecraftSessionService(
            authServer = "https://skin.blackyin.xyz/api/yggdrasil/authserver",
            sessionServer = "https://skin.blackyin.xyz/api/yggdrasil/sessionserver"
        )
        val session = sessionService.loginYggdrasilWithPassword("tfgv852@qq.com", "12345678")

        val net = NetworkManager.Builder()
            .host(host + suffix)
            .port(port)
            .pluginManager(pluginManager)
            .protocolVersion(autoVersion(version))
            .eventEmitter(this)
            .build()


        net.addListener(LoginListener(session, protocolVersion(version), sessionService))
        net.addListener(PlayListener())

        net.on(ConnectionEvent.Disconnect) {
            Thread.sleep(2000)
            net.connect()
        }.onPacket<ChatPacket> {
            val chat = ChatGsonSerializer.jsonToChat(packet.json)
            logger.info(chat.getFormattedText())
        }.on(LoginSuccessEvent) {
            Observable.timer(5, TimeUnit.SECONDS).subscribe {
                net.sendPacket(CKeepAlivePacket(System.currentTimeMillis()))
            }
        }


        net.connect()
    }

    companion object {
        private fun toJsonObj(jsonStr: String): JsonObject {
            val json = JsonParser.parseString(jsonStr)
            if (!json.isJsonObject) {
                throw JsonParseException("服务器的返回值不是一个json对象")
            }
            return json.asJsonObject
        }

        fun protocolVersion(jsonStr: String): Int {
            val obj = toJsonObj(jsonStr)
            return obj.getAsJsonObject("version").getAsJsonPrimitive("protocol").asInt
        }

        fun autoVersion(jsonStr: String): String {
            val obj = toJsonObj(jsonStr)
            //原版
            return obj.getAsJsonObject("version").getAsJsonPrimitive("name").asString!!

        }

        fun autoForge(jsonStr: String, pluginManager: PluginManager): String {
            val obj = toJsonObj(jsonStr)
            val fml1 = obj.getAsJsonObject("modinfo")
            val fml2 = obj.getAsJsonObject("forgeData")
            return if (fml1 != null) {
                val modList = Gson().fromJson(fml1, ServiceResponse.Modinfo::class.java).modList
                pluginManager.registerPlugin(FML1Plugin(modList))
                "\u0000FML\u0000"
            } else if (fml2 != null) {
                val modList = Gson().fromJson(fml2, ServiceResponse.ForgeData::class.java).mods
                pluginManager.registerPlugin(FML2Plugin(modList))
                "\u0000FML2\u0000"
            } else ""
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