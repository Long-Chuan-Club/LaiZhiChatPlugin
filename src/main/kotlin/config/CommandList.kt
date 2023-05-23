package org.example.mirai.plugin.config

/**
 * 指令枚举类
 * 主要用于判断目标是否是本插件的指令
 */


enum class CommandList(name: String) {
    GET1("来只"),
    GET2("来点"),
    GET3("整点"),
    ADD("ADD"),
    Clear("Clear")
}