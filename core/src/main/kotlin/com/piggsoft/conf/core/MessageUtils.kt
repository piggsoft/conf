package com.piggsoft.conf.core

import com.dyuproject.protostuff.LinkedBuffer
import com.dyuproject.protostuff.ProtostuffIOUtil
import com.dyuproject.protostuff.Schema
import com.dyuproject.protostuff.runtime.RuntimeSchema
import io.netty.util.CharsetUtil
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author yaochen4
 * @since 1.0
 * @version 1.0
 * @create 2017/9/19
 */
object MessageUtils {
    val defaultEncode: Byte = 0x1

    val defaultEncrypt: Byte = 0x1

    val headerLength = 1 + 1 + 4 + 4 + 4
}

val cachedSchema = ConcurrentHashMap<Class<*>, Schema<*>>()


inline fun <reified T> getSchema(): Schema<T> {
    return cachedSchema.getOrPut(T::class.java, {
        RuntimeSchema.getSchema(T::class.java)
    }) as Schema<T>
}

inline fun <reified T> T.serialize(): ByteArray {
    val buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)
    return ProtostuffIOUtil.toByteArray(this, getSchema<T>(), buffer)
}

inline fun <reified T> deserialize(data: ByteArray?): T? {
    return data?.let {
        var obj = T::class.java.newInstance()
        ProtostuffIOUtil.mergeFrom(data, obj, getSchema<T>())
        obj
    }
}

enum class CommandType(val command: Int) {
    PING(0),
    FILECHANGE(1)
}
