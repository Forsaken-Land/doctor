package top.fanua.doctor.client.listener

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.event.LoginSuccessEvent
import top.fanua.doctor.client.session.GameProfile
import top.fanua.doctor.client.session.Session
import top.fanua.doctor.client.session.YggdrasilMinecraftSessionService
import top.fanua.doctor.client.utils.SecurityUtils
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.api.Connection
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.event.NetLifeCycleEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.network.handler.oncePacket
import top.fanua.doctor.network.utils.connection
import top.fanua.doctor.network.utils.setProtocolState
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.definition.client.HandshakePacket
import top.fanua.doctor.protocol.definition.login.client.EncryptionResponsePacket
import top.fanua.doctor.protocol.definition.login.client.LoginStartPacket
import top.fanua.doctor.protocol.definition.login.server.DisconnectPacket
import top.fanua.doctor.protocol.definition.login.server.EncryptionRequestPacket
import top.fanua.doctor.protocol.definition.login.server.LoginSuccessPacket
import top.fanua.doctor.protocol.definition.login.server.SetCompressionPacket

/**
 * ### 登陆监听器
 *
 * - [name] 离线登陆名称
 * - [session] 会话
 * - [protocolVersion] 协议版本
 * - [sessionService] 会话服务
 * - [suffix] host 后缀
 */
class LoginListener(
    private val name: String = "",
    var session: Session? = null,
    private val protocolVersion: Int = 340,
    private val sessionService: YggdrasilMinecraftSessionService = YggdrasilMinecraftSessionService.Default,
    var suffix: String = ""
) : EventListener {
    private val logger: Logger = LoggerFactory.getLogger(LoginListener::class.java)
    private var connected = false

    override fun initListen(emitter: EventEmitter) {
        //注册事件

        //启动前：验证用户
        emitter.on(NetLifeCycleEvent.BeforeConnect) {
            if (session != null)
                session = sessionService.validateYggdrasilSession(session!!)
        }

        //连接开始
        emitter.on(ConnectionEvent.Connected) { (ctx) ->
            ctx!!
            val connection = ctx.connection()
            connection.sendPacket(
                HandshakePacket(
                    protocolVersion,
                    connection.host + suffix,
                    connection.port,
                    ProtocolState.LOGIN
                )
            )
            ctx.setProtocolState(ProtocolState.LOGIN)

            connection.sendPacket(
                if (session == null) {
                    LoginStartPacket(name)
                } else {
                    LoginStartPacket(session!!.profile.name)
                }
            )

            //监听登录事件
            emitter.oncePacket<LoginSuccessPacket> {
                ctx.setProtocolState(ProtocolState.PLAY)
                emitter.emit(LoginSuccessEvent, GameProfile(packet.uUID, packet.userName))
            }
        }
        emitter.onPacket<EncryptionRequestPacket> {
            encryption(packet, connection)
        }
        emitter.onPacket<SetCompressionPacket> {
            connection.setCompressionEnabled(packet.threshold)
        }
        emitter.onPacket<DisconnectPacket> {
            logger.warn("连接断开：${packet.reason}")
        }

    }

    //处理加密
    private fun encryption(packet: EncryptionRequestPacket, connection: Connection) {
        val sharedSecret = SecurityUtils.generateSharedKey()
        val publicKey = SecurityUtils.decodePublicKey(packet.publicKey)
        val secret = SecurityUtils.encryptRSA(publicKey, sharedSecret.encoded)
        val verify = SecurityUtils.encryptRSA(publicKey, packet.verifyToken)
        val serverHash = SecurityUtils.generateAuthHash(packet.serverID, sharedSecret, publicKey)

        if (session != null)
            sessionService.joinServer(session!!, serverHash)

        connection.sendPacket(EncryptionResponsePacket(secret, verify))
        connection.setEncryptionEnabled(sharedSecret)
    }

}



