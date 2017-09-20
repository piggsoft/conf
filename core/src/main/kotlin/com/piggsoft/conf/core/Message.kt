package com.piggsoft.conf.core

/**
 *
 * @author yaochen4
 * @since 1.0
 * @version 1.0
 * @create 2017/9/19
 * 通用报文格式：无论是请求还是响应，报文都由一个通用报文头和实际数据组成。报文头在前，数据在后
 * （1）报文头:由数据解析类型，数据解析方法，编码，扩展字节，包长度组成,共16个字节：
 * 编码方式（1byte）、加密（1byte）、会话ID（4byte）、命令或者结果码（4byte）、包长（4byte）
 * （2）数据:由包长指定。请求或回复数据。类型对应为JAVA的Map<String,String>
 * 数据格式定义：
 * 字段1键名长度    字段1键名 字段1值长度    字段1值
 * 字段2键名长度    字段2键名 字段2值长度    字段2值
 * 字段3键名长度    字段3键名 字段3值长度    字段3值
 * …    …    …    …
 * 长度为整型，占4个字节
 */
class Message<T> {

    var encode: Byte = MessageUtils.defaultEncode
    var encrypt: Byte = MessageUtils.defaultEncrypt
    var sessionId: Int = 1
    var command: Int = 0
    var bodyLength: Int = 0
    var body: T? = null

}

val pingMessage = Message<Any>()