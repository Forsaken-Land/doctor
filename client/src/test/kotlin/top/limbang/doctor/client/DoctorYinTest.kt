package top.limbang.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.JoinGameEvent
import top.limbang.doctor.client.running.mod.*
import java.io.FileInputStream
import java.util.*

fun main() {
    val logger: Logger = LoggerFactory.getLogger("main")
    val pros = Properties()
    val file = FileInputStream("local.properties")
    pros.load(file)

    // 离线登陆测试
//    val host = "localhost"
//    val port = 25565
//    val name = pros["name"] as String

    // 外置正版登陆测试
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
        .enablePlayerList()

    if (!client.start(host, port)) return

    client.enableLag()

    client.once(JoinGameEvent) {
        GlobalScope.launch {
            val lags = client.getLagSuspend()
            var outMsg = "[123]高于1000us/t 的方块/实体如下:\n"
            var size = 0
            lags.forEach {
//                if (size > 50) return@forEach
                if (it.nanos > 1000 * 1000) { //可能是这样换算
                    size++
                    outMsg += when (it.data) {
                        is Block -> {
                            val name = (it.data as Block).name
                            val x = (it.data as Block).x
                            val y = (it.data as Block).y
                            val z = (it.data as Block).z
                            val worldId = (it.data as Block).worldId
                            "方块：$name,位置：$worldId($x,$y,$z),耗时：${it.nanos / 1000}us/t\n" //可能是这样换算
                        }
                        is Entity -> {
                            val name = (it.data as Entity).name
                            val uuid = (it.data as Entity).uuid
                            val worldId = (it.data as Entity).worldId
                            "实体：$name,uuid：$uuid,时间：$worldId,耗时：${it.nanos / 1000}us/t\n" //可能是这样换算
                        }
                        is Event -> {
                            ""
                        }
                    }
                }
            }
            logger.info(outMsg)
            client.stop()
        }
    }
}