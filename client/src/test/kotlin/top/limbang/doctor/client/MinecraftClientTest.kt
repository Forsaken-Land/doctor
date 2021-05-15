package top.limbang.doctor.client

import kotlinx.coroutines.runBlocking
import org.junit.Test

class MinecraftClientTest {

    @Test
    fun ping() {
        runBlocking{
            MinecraftClient().ping("127.0.0.1",25565)
        }
    }
}



