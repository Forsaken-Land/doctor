package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.ChatEvent
import top.limbang.doctor.client.event.JoinGameEvent
import top.limbang.doctor.client.running.TpsEntity
import top.limbang.doctor.client.running.TpsUtils
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import java.io.FileInputStream
import java.util.*

fun main() {

    val logger: Logger = LoggerFactory.getLogger("main")
    val pros = Properties()
    val file = FileInputStream("local.properties")
    pros.load(file)
    val host = pros["host"] as String
    val port = (pros["port"] as String).toInt()
    val username = pros["username"] as String
    val password = pros["password"] as String
    val authServerUrl = pros["authServerUrl"] as String
    val sessionServerUrl = pros["sessionServerUrl"] as String

    val client = MinecraftClient()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .start(host, port)



}