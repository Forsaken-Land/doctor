package top.fanua.doctor.protocol.extension

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import net.querz.nbt.io.NBTDeserializer
import net.querz.nbt.tag.CompoundTag
import top.fanua.doctor.protocol.entity.math.BlockPos
import top.fanua.doctor.protocol.utils.ResourceLocation
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

/**
 * ### 从缓冲区读取 [ByteArray]
 *
 * 先从缓冲区读取 VarInt 值确定数组长度,在读取数组
 */
fun ByteBuf.readByteArray(): ByteArray {
    return ByteArray(readVarInt()).also {
        readBytes(it)
    }
}

/**
 * ### 写 [ByteArray] 到缓冲区
 *
 * 先写 [length]VarInt类型,在写[byteArray]
 */
fun ByteBuf.writeByteArray(length: Int, byteArray: ByteArray) {
    writeVarInt(length)
    writeBytes(byteArray)
}

fun ByteBuf.writeByteArray(byteArray: ByteArray) {
    writeByteArray(byteArray.size, byteArray)
}

/**
 * ### 从缓冲区读取压缩的 Int
 *
 * 可变长度格式，使较小的数字使用较少的字节。它们非常类似于协议缓冲区变量：用7个最低有效位对值进行编码，最高有效位表示数字的下一部分是否有另一个字节。首先写入最低有效组，然后写入每个更高的组；因此，变量实际上是小端（但是，组是7位，而不是8位）。
 *
 * VarInts变量永远不会超过5个字节
 * @return [Int]
 */
fun ByteBuf.readVarInt(): Int {

    var readableBytes = readableBytes()
    var varInt = 0
    var byteShift = 0
    var byte: Byte
    do {
        if (--readableBytes < 0)
            throw IOException("无法读取 VarInt.")
        byte = readByte()
        varInt = varInt or (byte.toInt() and 0x7F shl byteShift++ * 7)
        if (byteShift > 5) throw IOException("尝试读取过长的 VarInt.")
    } while (byte.toInt() and 0x80 == 0x80)
    return varInt
}

/**
 * ### 写入压缩的 Int 到缓冲区
 *
 * 可变长度格式，使较小的数字使用较少的字节。它们非常类似于协议缓冲区变量：用7个最低有效位对值进行编码，最高有效位表示数字的下一部分是否有另一个字节。首先写入最低有效组，然后写入每个更高的组；因此，变量实际上是小端（但是，组是7位，而不是8位）。
 *
 * VarInts变量永远不会超过5个字节
 * @param input 未压缩的 Int
 */
fun ByteBuf.writeVarInt(input: Int) {

    var value = input
    var part: Int
    do {
        part = value and 0x7F
        value = value ushr 7
        if (value != 0)
            part = part or 0x80
        writeByte(part)
    } while (value != 0)
}

/**
 * ### 读取缓冲区字符串
 * 从缓冲区读取指定最大长度字符串 [maxLength] 字符串,以 [StandardCharsets.UTF_8] 编码
 * @param maxLength 要读取的最大长度
 */
fun ByteBuf.readString(maxLength: Int): String {
    val len = readVarInt()
    if (len > maxLength) throw IOException("字符串长度[$len]超出最大长度[$maxLength].")
    val bytes = ByteArray(len)
    readBytes(bytes)
    return String(bytes, StandardCharsets.UTF_8)
}

/**
 * ### 读取缓冲区字符串
 * 从缓冲区读取最大 [Short.MAX_VALUE] 字符串,以 [StandardCharsets.UTF_8] 编码
 */
fun ByteBuf.readString(): String {
    return readString(Short.MAX_VALUE.toInt())
}

/**
 *  ### 写入字符串
 *
 * 以 [StandardCharsets.UTF_8] 编码
 */
fun ByteBuf.writeString(value: String) {

    val bytes = value.toByteArray(StandardCharsets.UTF_8)
    if (bytes.size > Short.MAX_VALUE) throw IOException("尝试写入长度大于${Short.MAX_VALUE}的数据.")
    writeVarInt(bytes.size)
    writeBytes(bytes)
}

fun ByteBuf.readCompoundTag(): CompoundTag {
    val i = this.readerIndex()
    val length = this.readByte()

    return if (length == 0.toByte()) {
        throw IOException("无法读取Nbt： 数据长度为0")
    } else {
        this.readerIndex(i)
        val stream = ByteBufInputStream(this)
        NBTDeserializer(false).fromStream(stream).tag as CompoundTag
    }
}


fun ByteBuf.readUUID(): UUID {
    return UUID(readLong(), readLong())
}

fun ByteBuf.readBlockPos(): BlockPos {
    return BlockPos.fromLong(this.readLong())
}

fun ByteBuf.writeBlockPos(pos: BlockPos): ByteBuf {
    this.writeLong(pos.toLong())
    return this
}

fun ByteBuf.readResourceLocation(): ResourceLocation {
    return ResourceLocation(readString())
}

fun ByteBuf.writeResourceLocation(resourceLocation: ResourceLocation): ByteBuf {
    writeString("${resourceLocation.namespace}:${resourceLocation.path}")
    return this
}

fun ByteBuf.readVarShort(): Int {
    var low = readUnsignedShort()
    var high = 0
    if (low and 0x8000 != 0) {
        low = low and 0x7FFF
        high = readUnsignedByte().toInt()
    }
    return high and 0xFF shl 15 or low
}

fun ByteBuf.writeVarShort(toWrite: Int) {
    var low = toWrite and 0x7FFF
    val high = toWrite and 0x7F8000 shr 15
    if (high != 0) {
        low = low or 0x8000
    }
    writeShort(low)
    if (high != 0) {
        writeByte(high)
    }
}
