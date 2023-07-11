package org.example.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.Command.AddChat
import org.example.mirai.plugin.Service.myEvent
import org.example.mirai.plugin.config.LzConfig
import org.example.mirai.plugin.util.ImageUtils
import org.example.mirai.plugin.util.SendTask
import xyz.cssxsh.mirai.hibernate.MiraiHibernateRecorder
import java.io.File
import java.time.Instant


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.come_only.mirai-come",
        name = "来只XX",
        version = "0.1.7"
    ) {
        author("huvz")
        info(
            """
            个人自用
            来只&来点 功能 将群友话语做成梗图
        """.trimIndent()
        )
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
        // author 和 info 可以删除.
    }
) {

    val QuoteReply.originalMessageFromLocal: MessageChain
        get() = MiraiHibernateRecorder[source].firstOrNull()?.toMessageChain() ?: source.originalMessage

    override fun onEnable() {
        logger.info { "Plugin loaded" }
         //CommandManager.registerCommand(AddChat) // 注册指令
        //配置文件目录 "${dataFolder.absolutePath}/"
        globalEventChannel().registerListenerHost(myEvent)
        globalEventChannel().subscribeAlways<MemberLeaveEvent> {
            SendTask.sendMessage(group,"泪目，有人离开了群。")
        }
    }



}


