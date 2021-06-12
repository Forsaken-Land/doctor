package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.util.*

val logger: Logger = LoggerFactory.getLogger("main")
val pros = Properties()
val file = FileInputStream("local.properties")
val load = pros.load(file)

val host = pros["host"] as String
val port = (pros["port"] as String).toInt()
val username = pros["username"] as String
val password = pros["password"] as String
val authServerUrl = pros["authServerUrl"] as String
val sessionServerUrl = pros["sessionServerUrl"] as String