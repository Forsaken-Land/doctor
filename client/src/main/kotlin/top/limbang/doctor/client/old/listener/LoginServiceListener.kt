package top.limbang.minecraft.core.listener


import io.netty.channel.ChannelHandlerContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.utils.SecurityUtils
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.core.HttpClient
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.protocolState
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.login.client.EncryptionResponsePacket
import top.limbang.doctor.protocol.definition.login.client.LoginStartPacket
import top.limbang.doctor.protocol.definition.login.server.DisconnectPacket
import top.limbang.doctor.protocol.definition.login.server.EncryptionRequestPacket
import top.limbang.doctor.protocol.definition.login.server.LoginSuccessPacket
import top.limbang.doctor.protocol.definition.login.server.SetCompressionPacket
import top.limbang.minecraft.entity.yggdrasil.AuthenticateRequest
import top.limbang.minecraft.entity.yggdrasil.AuthenticateResponse
import top.limbang.minecraft.entity.yggdrasil.JoinRequest
import top.limbang.minecraft.entity.yggdrasil.YggdrasilError
import java.util.concurrent.TimeUnit

/**
 * ### 登录服务器监听器
 *
 * - [username] 用户名
 * - [password] 密码
 */
open class LoginServiceListener(private val username: String, private val password: String) : EventListener {
    private val logger: Logger = LoggerFactory.getLogger(LoginServiceListener::class.java)

    private var authServer = "https://authserver.mojang.com/authenticate"
    private var sessionServer = "https://sessionserver.mojang.com"

    private val connectionKey = Attributes.ATTR_CONNECTION
    private val httpClient = HttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var authenticateResponse: AuthenticateResponse


    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Read) {
            loginPacket(it)
        }
        emitter.on(ConnectionEvent.Connected) {
            loginStart(it.context!!)
        }
    }

    /**
     * 设置 authServer 地址,默认为: https://authserver.mojang.com/authenticate
     *
     * @param authServer url
     */
    fun authServer(authServer: String): LoginServiceListener {
        this.authServer = authServer
        return this
    }

    /**
     * 设置 sessionServer 地址,默认为: https://sessionserver.mojang.com
     *
     * @param sessionServer url
     */
    fun sessionServer(sessionServer: String): LoginServiceListener {
        this.sessionServer = sessionServer
        return this
    }

    /**
     * ### 登录开始
     *
     * 提交一个300毫秒后的任务,判断连接成功后状态是否处于 [ProtocolState.LOGIN],如果处于就开始登录
     */
    private fun loginStart(ctx: ChannelHandlerContext) {
        val connection = ctx.channel().attr(connectionKey).get()
        ctx.executor().schedule({
            if (ctx.protocolState() == ProtocolState.LOGIN) {
                // 判断 authenticateResponse 是否未初始化
                if (!this::authenticateResponse.isInitialized) {
                    val authServer = "$authServer/authenticate"
                    val body = json.encodeToString(AuthenticateRequest(username, password))
                    val response = httpClient.postJson(authServer, body)
                    if (response.code != 200) {
                        val errorResponse = json.decodeFromString<YggdrasilError>(response.content)
                        logger.error("登录验证出错:${errorResponse.errorMessage}")
                        httpClient.close()
                        connection.close()
                        return@schedule
                    }
                    authenticateResponse = json.decodeFromString(response.content)
                }
                connection.sendPacket(LoginStartPacket(authenticateResponse.selectedProfile.name))

            }
        }, 300, TimeUnit.MICROSECONDS)
    }

    /**
     * ### 登录包处理
     *
     *
     */
    private fun loginPacket(event: ConnectionEventArgs) {
        val connection = event.context!!.channel().attr(connectionKey).get()
        // 登录事件
        when (val packet = event.message) {
            // 断开连接包
            is DisconnectPacket -> logger.info(packet.reason)
            // 加密请求
            is EncryptionRequestPacket -> encryptionRequest(packet, connection)
            // 登录成功
            is LoginSuccessPacket -> event.context!!.setProtocolState(ProtocolState.PLAY)
            // 设置压缩
            is SetCompressionPacket -> connection.setCompressionEnabled(packet.threshold)
        }
    }

    /**
     * ### 加密请求处理
     *
     *
     */
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