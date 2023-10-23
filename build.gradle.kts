plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.15.0"

}
val kotlin_Version = "1.8.20"
val kotlinx_coroutines = "1.7.3"
group = "LaiZhi.Chat.Repeater"
version = "0.2.2"
repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
}
dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.7.1")
    implementation("io.ktor:ktor-client-okhttp:2.2.4")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-swing
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${kotlinx_coroutines}")

    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.80")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinx_coroutines}")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlin_Version}")
// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlin_Version}")
}
mirai {
    jvmTarget = JavaVersion.VERSION_17
}