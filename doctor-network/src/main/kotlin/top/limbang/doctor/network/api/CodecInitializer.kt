package top.limbang.doctor.network.api

import io.netty.channel.socket.SocketChannel
import top.limbang.doctor.network.core.NetworkManager

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
interface CodecInitializer {
    fun initChannel(ch: SocketChannel, manager: NetworkManager)
}

object DummyCodecInitializer : CodecInitializer {
    override fun initChannel(ch: SocketChannel, manager: NetworkManager) {
    }

}