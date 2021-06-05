package top.limbang.doctor.plugin.laggoggles.entity

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.readString
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:2:06
 */
enum class Entry(private val id: Int) {
    WORLD_ID(0) {
        override fun read(buf: ByteBuf): Int {
            return buf.readInt()
        }
    },

    ENTITY_NAME(1) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },
    ENTITY_UUID(2) {
        override fun read(buf: ByteBuf): UUID {
            return UUID(buf.readLong(), buf.readLong())
        }
    },
    ENTITY_CLASS_NAME(3) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },

    BLOCK_NAME(4) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },
    BLOCK_POS_X(5) {
        override fun read(buf: ByteBuf): Int {
            return buf.readInt()
        }
    },
    BLOCK_POS_Y(6) {
        override fun read(buf: ByteBuf): Int {
            return buf.readInt()
        }
    },
    BLOCK_POS_Z(7) {
        override fun read(buf: ByteBuf): Int {
            return buf.readInt()
        }
    },
    BLOCK_CLASS_NAME(8) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },

    EVENT_BUS_LISTENER(9) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },
    EVENT_BUS_EVENT_CLASS_NAME(10) {
        override fun read(buf: ByteBuf): String {
            return buf.readString()
        }
    },
    EVENT_BUS_THREAD_TYPE(11) {
        override fun read(buf: ByteBuf): Int {
            return buf.readInt()
        }
    },

    NANOS(12) {
        override fun read(buf: ByteBuf): Long {
            return buf.readLong()
        }
    },
    NONE(-1) {
        override fun read(buf: ByteBuf): Any {
            return ""
        }
    };

    abstract fun read(buf: ByteBuf): Any

    companion object {
        fun getValue(id: Int): Entry {
            return values().getOrNull(id) ?: NONE
        }
    }
}
