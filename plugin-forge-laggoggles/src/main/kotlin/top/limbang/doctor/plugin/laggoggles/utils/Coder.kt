package top.limbang.doctor.plugin.laggoggles.utils

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeString
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:1:42
 */
abstract class Coder<T> {
    companion object {
        val int: Coder<Int> = object : Coder<Int>() {
            override fun read(buf: ByteBuf): Int {
                return buf.readInt()
            }

            override fun write(`var`: Int, buf: ByteBuf) {
                buf.writeInt(`var`)
            }
        }
        val string: Coder<String> = object : Coder<String>() {
            override fun read(buf: ByteBuf): String {
                return buf.readString()
            }

            override fun write(`var`: String, buf: ByteBuf) {
                buf.writeString(`var`)
            }

        }
        val uuid: Coder<UUID> = object : Coder<UUID>() {
            override fun read(buf: ByteBuf): UUID {
                return UUID(buf.readLong(), buf.readLong())
            }

            override fun write(`var`: UUID, buf: ByteBuf) {
                buf.writeLong(`var`.mostSignificantBits)
                buf.writeLong(`var`.leastSignificantBits)
            }

        }
        val long: Coder<Long> = object : Coder<Long>() {
            override fun read(buf: ByteBuf): Long {
                return buf.readLong()
            }

            override fun write(`var`: Long, buf: ByteBuf) {
                buf.writeLong(`var`)
            }

        }
        val boolean: Coder<Boolean> = object : Coder<Boolean>() {
            override fun read(buf: ByteBuf): Boolean {
                return buf.readBoolean()
            }

            override fun write(`var`: Boolean, buf: ByteBuf) {
                buf.writeBoolean(`var`)
            }

        }
    }

    abstract fun read(buf: ByteBuf): T
    abstract fun write(`var`: T, buf: ByteBuf)
}
