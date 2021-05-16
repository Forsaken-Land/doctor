package top.limbang.doctor.client.listener

import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.utils.SecurityUtils
import top.limbang.doctor.client.yggdrasil.AuthenticateRequest
import top.limbang.doctor.client.yggdrasil.AuthenticateResponse
import top.limbang.doctor.client.yggdrasil.JoinRequest
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.core.api.promise.Promise
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.core.HttpClient
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.connection
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.login.client.EncryptionResponsePacket
import top.limbang.doctor.protocol.definition.login.client.LoginStartPacket
import top.limbang.doctor.protocol.definition.login.server.EncryptionRequestPacket
import top.limbang.doctor.protocol.definition.login.server.LoginSuccessPacket
import top.limbang.doctor.protocol.definition.login.server.SetCompressionPacket
import top.limbang.minecraft.entity.yggdrasil.YggdrasilError

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */


class LoginListener(
    val username: String,
    val password: String
) : EventListener {
    lateinit var emitter: EventEmitter
    private val logger: Logger = LoggerFactory.getLogger(LoginListener::class.java)
    private var connected = false

    override fun initListen(emitter: EventEmitter) {
        this.emitter = emitter

        login()


    }


    fun waitFor(e: Event<*>) = Promise { resolve, reject ->
        this.emitter.once(e) {
            resolve(it!!)
        }
        this.emitter.once(ConnectionEvent.Error) {
            reject(it.error!!)
        }
    }


    fun loginAuthlib() {
        val authServer = "$authServer/authenticate"
        val body = json.encodeToString(AuthenticateRequest(username, password))
        val response = httpClient.postJson(authServer, body)
        if (response.code != 200) {
            val errorResponse = json.decodeFromString<YggdrasilError>(response.content)
            logger.error("登录验证出错:${errorResponse.errorMessage}")
            httpClient.close()
            return
        }
        authenticateResponse = json.decodeFromString(response.content)
    }


    lateinit var context: ChannelHandlerContext
    lateinit var connection: Connection
    fun login() {

        waitFor(ConnectionEvent.Connected)
            .then {
                it as ConnectionEventArgs
                context = it.context!!
                connection = context.connection()

                connection.sendPacket(
                    HandshakePacket(340, connection.host, connection.port, ProtocolState.LOGIN)
                )
                context.setProtocolState(ProtocolState.LOGIN)
                connection.sendPacket(LoginStartPacket(authenticateResponse.selectedProfile.name))

                Promise { _, reject ->
                    waitFor(PacketEvent(EncryptionRequestPacket::class))
                        .then {
                            it as EncryptionRequestPacket
                            encryptionRequest(it, connection)
                        }.catch(reject)
                    waitFor(PacketEvent(SetCompressionPacket::class)).then {
                        it as SetCompressionPacket
                        connection.setCompressionEnabled(it.threshold)
                    }.catch(reject)

                    waitFor(PacketEvent(LoginSuccessPacket::class))
                        .then {
                            context.setProtocolState(ProtocolState.PLAY)
                        }
                }
            }
            .catch {
                it as Throwable
                logger.error("登录失败", it)
            }

    }

    var authServer = "https://authserver.mojang.com/authenticate"
    var sessionServer = "https://sessionserver.mojang.com"

    private val connectionKey = Attributes.ATTR_CONNECTION
    private val httpClient = HttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var authenticateResponse: AuthenticateResponse

    private fun encryptionRequest(packet: EncryptionRequestPacket, connection: Connection) {
        val sharedSecret = SecurityUtils.generateSharedKey()
        val publicKey = SecurityUtils.decodePublicKey(packet.publicKey)
        val secret = SecurityUtils.encryptRSA(publicKey, sharedSecret.encoded)
        val verify = SecurityUtils.encryptRSA(publicKey, packet.verifyToken)
        val serverHash = SecurityUtils.generateAuthHash(packet.serverID, sharedSecret, publicKey)

        // post 验证
        val joinRequest =
            JoinRequest(authenticateResponse.accessToken, authenticateResponse.selectedProfile.id, serverHash)
        val sessionServer = "$sessionServer/session/minecraft/join"
        val response = httpClient.postJson(sessionServer, json.encodeToString(joinRequest))
        if (response.code != 204) {
            logger.error("进入验证出错...")
            httpClient.close()
            connection.close()
            return
        }
        httpClient.close()

        connection.sendPacket(EncryptionResponsePacket(secret, verify))
        connection.setEncryptionEnabled(sharedSecret)
    }
}



