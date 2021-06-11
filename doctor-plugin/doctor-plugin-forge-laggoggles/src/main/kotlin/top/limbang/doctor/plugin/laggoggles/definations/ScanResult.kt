package top.limbang.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import top.limbang.doctor.plugin.laggoggles.api.LagPacket
import top.limbang.doctor.plugin.laggoggles.entity.Entry
import top.limbang.doctor.protocol.api.PacketDecoder
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:13:07
 */
object AnySerializer : KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        TODO("多余的") //可能以后需要
    }

    override val descriptor = PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING) //不得不写

    override fun serialize(encoder: Encoder, value: Any) {
        when (value) {
            is String -> {
                encoder.encodeString(value)
            }
            is Long -> {
                encoder.encodeLong(value)
            }
            is Int -> {
                encoder.encodeInt(value)
            }
            is UUID -> {
                encoder.encodeString(value.toString())
            }
            is Boolean -> {
                encoder.encodeBoolean(value)
            }
        }
    }

}

@Serializable
data class ScanResultPacket(
    val tickCount: Long,
    val hasMore: Boolean,
    val endTime: Long,
    val startTime: Long,
    val totalTime: Long,
    val totalFrames: Long,
    val side: Side,
    val scanType: ScanType,
    val data: List<ObjectData>
) : LagPacket {

    @Serializable
    data class ObjectData(
        val type: Type,
        val data: Map<Entry, @Serializable(AnySerializer::class) Any>
    )

    enum class Type {
        ENTITY,
        TILE_ENTITY,
        BLOCK,
        EVENT_BUS_LISTENER,
        GUI_ENTITY,
        GUI_BLOCK;

        companion object {
            fun getValue(id: Int): Type {
                return values().getOrNull(id) ?: ENTITY
            }
        }
    }
}

class ScanResultDecoder : PacketDecoder<ScanResultPacket> {
    override fun decoder(buf: ByteBuf): ScanResultPacket {
        val tickCount = buf.readLong()
        val hasMore = buf.readBoolean()
        val endTime = buf.readLong()
        val startTime = buf.readLong()
        val totalTime = buf.readLong()
        val totalFrames = buf.readLong()
        val side = Side.getValue(buf.readInt())
        val scanType = ScanType.getValue(buf.readInt())
        val size = buf.readInt()
        val data = mutableListOf<ScanResultPacket.ObjectData>()
        for (i in 0 until size) {
            val type = ScanResultPacket.Type.getValue(buf.readInt())
            val length = buf.readInt()
            val entryMap = TreeMap<Entry, Any>()
            for (j in 0 until length) {
                val entry = Entry.getValue(buf.readInt())
                entryMap[entry] = entry.read(buf)
            }
            data.add(ScanResultPacket.ObjectData(type, entryMap))
        }
        return ScanResultPacket(tickCount, hasMore, endTime, startTime, totalTime, totalFrames, side, scanType, data)
    }
}


enum class Side(val id: Int) {
    DEDICATED_SERVER(0),
    CLIENT_WITHOUT_SERVER(1),
    CLIENT_WITH_SERVER(2),
    UNKNOWN(3);

    companion object {
        fun getValue(id: Int): Side {
            return values().getOrNull(id) ?: DEDICATED_SERVER
        }
    }
}

enum class ScanType(val id: Int, private val text: String) {
    WORLD(0, "LagGoggles: World scan results"),
    FPS(1, "LagGoggles: FPS scan results"),
    EMPTY(2, "Empty profile results.");

    companion object {
        fun getValue(id: Int): ScanType {
            return values().getOrNull(id) ?: WORLD
        }
    }
}



