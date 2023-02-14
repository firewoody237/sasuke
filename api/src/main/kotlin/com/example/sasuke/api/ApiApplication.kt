package com.example.sasuke.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan


@SpringBootApplication(scanBasePackages = ["com.example.sasuke"])
@ServletComponentScan(basePackages = ["com.example.sasuke"])
class ApiApplication {

}

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}