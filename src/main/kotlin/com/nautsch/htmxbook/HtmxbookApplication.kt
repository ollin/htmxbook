package com.nautsch.htmxbook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class HtmxbookApplication

fun main(args: Array<String>) {
	runApplication<HtmxbookApplication>(*args)
}
