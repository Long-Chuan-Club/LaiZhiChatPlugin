plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.15.0-M1"
}

group = "huvz.chat.repeater"
version = "0.1.1"

repositories {

    mavenCentral()
    //    if (System.getenv("CI")?.toBoolean() != true) {
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
//    }
}
