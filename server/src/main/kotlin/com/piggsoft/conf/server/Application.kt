package com.piggsoft.conf.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 *
 * @author piggsoft@163.com
 * @since 1.0
 * @version 1.0
 * @create 2017/9/16
 * Description:
 */
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}