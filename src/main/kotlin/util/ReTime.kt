package org.example.mirai.plugin.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class ReTime {
    val updateTime = LocalTime.of(1, 0) // 每天凌晨1点更新
    val timer = Timer()
    fun LocalTime.toMillis(): Long {
        val now = LocalDateTime.now()
        val target = this.atDate(now.toLocalDate())
        val delay = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return if (delay < 0) delay + 86400000 else delay // 如果时间已经过去，就在明天的同一时间更新
    }
    //hi功能
}