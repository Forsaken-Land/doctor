package top.limbang.doctor.client

import org.junit.Test

class MinecraftClientTest {

    @Test
    fun ping() {
        MinecraftClient().ping("127.0.0.1", 25565)

    }
}



