package org.example.mirai.plugin.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object LzConfig {
    /**
     * 指令列表
     * 默认为"[来点]","[来只]"
     */
    //val GetcommandList:List<String> = listOf("来点","来只")
    val AddcommandList:List<String> = listOf("add","添加")
    var archiveDirectory: String = ""
    var adminQQid:Long = 1686448912
    var pdImageList:List<String?> = listOf("群友","蔡姐姐")
    var ApiList:List<String?> = listOf("")
    var MsgmessageIntervalTime = 100L;

}