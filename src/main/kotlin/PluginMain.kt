package org.longchuanclub.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info
import org.longchuanclub.mirai.plugin.Command.getImgList
import org.longchuanclub.mirai.plugin.Service.myEvent
import org.longchuanclub.mirai.plugin.util.SendTask
import xyz.cssxsh.mirai.hibernate.MiraiHibernateRecorder


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.long_chuan_club.LaiZhi",
        name = "来只XX",
        version = "0.2.1"
    ) {
        author("Huvz")
        info(
            """
            个人自用
            来只&来点 功能 将群友话语做成可以出发的图
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
        //globalEventChannel().registeredCommands
        CommandManager.registerCommand(getImgList)
        globalEventChannel().registerListenerHost(myEvent)
        globalEventChannel().subscribeAlways<MemberLeaveEvent> {
            SendTask.sendMessage(group,"(˚ ˃̣̣̥᷄⌓˂̣̣̥᷅ ) 泪目，有人离开了群。")
        }
        globalEventChannel().subscribeAlways<MemberJoinEvent> {
            SendTask.sendMessage(group,At(member)+"欢迎进入本群哇！ ૮(˶ᵔ ᵕ ᵔ˶)ა")

        }
    }



}


