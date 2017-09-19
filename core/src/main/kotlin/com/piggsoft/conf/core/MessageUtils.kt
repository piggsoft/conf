package com.piggsoft.conf.core

import com.dyuproject.protostuff.Schema
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
    val defaultEncode = CharsetUtil.UTF_8.toString().toByte()

    val defaultEncrypt: Byte = 0x1

    val headerLength = 1 + 1 + 4 + 4 + 4
}

object SerializationUtil {
    val cachedSchema = ConcurrentHashMap<Class<Any>, Schema<Any>>()
}