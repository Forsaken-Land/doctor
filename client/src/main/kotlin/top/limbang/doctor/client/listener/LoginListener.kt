package top.limbang.doctor.client.listener

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.LoginSuccessEvent
import top.limbang.doctor.client.session.GameProfile
import top.limbang.doctor.client.session.Session
import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.client.utils.SecurityUtils
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.NetLifeCycleEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.handler.onPacketWrapped
import top.limbang.doctor.network.handler.oncePacket
import top.limbang.doctor.network.handler.oncePacketWrapped
import top.limbang.doctor.network.utils.connection
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.login.client.EncryptionResponsePacket
import top.limbang.doctor.protocol.definition.login.client.LoginStartPacket
import top.limbang.doctor.protocol.definition.login.server.DisconnectPacket
import top.limbang.doctor.protocol.definition.login.server.EncryptionRequestPacket
import top.limbang.doctor.protocol.definition.login.server.LoginSuccessPacket
import top.limbang.doctor.protocol.definition.login.server.SetCompressionPacket

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */


class LoginListener(
    var session: Session,
    val protocolVersion: Int = 340,
    val sessionService: YggdrasilMinecraftSessionService = YggdrasilMinecraftSessionService.Default
) : EventListener {
    lateinit var emitter: EventEmitter
    private val logger: Logger = LoggerFactory.getLogger(LoginListener::class.java)
    private var connected = false

    override fun initListen(emitter: EventEmitter) {
        //注册事件

        //启动前：验证用户
        emitter.on(NetLifeCycleEvent.BeforeConnect) {
            session = sessionService.validateYggdrasilSession(session)
        }

        //连接开始
        emitter.on(ConnectionEvent.Connected) { (ctx) ->
            ctx!!
            val connection = ctx.connection()
            connection.sendPacket(HandshakePacket(protocolVersion, connection.host, connection.port, ProtocolState.LOGIN))
            ctx.setProtocolState(ProtocolState.LOGIN)
            connection.sendPacket(LoginStartPacket(session.profile.name))
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

        sessionService.joinServer(session, serverHash)

        connection.sendPacket(EncryptionResponsePacket(secret, verify))
        connection.setEncryptionEnabled(sharedSecret)
    }

}



