plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.15.0-M1"

}

group = "huvz.chat.repeater"
version = "0.1.5"
repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
}
dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.7.1")
    implementation("io.ktor:ktor-client-okhttp:2.2.4")
}
mirai {
    jvmTarget = JavaVersion.VERSION_11
}