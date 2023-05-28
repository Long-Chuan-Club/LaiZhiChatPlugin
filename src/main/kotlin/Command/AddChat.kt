package org.example.mirai.plugin.Command


import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.PluginMain.logger
import org.example.mirai.plugin.PluginMain.resolveDataFile
import org.example.mirai.plugin.util.ImageUtils
import java.io.File
import java.util.stream.Collectors

object AddChat :SimpleCommand(PluginMain,"listImage",description = "获取某个人的图库") {

    @Handler
    suspend fun handlerlist(sender: CommandSender){
        val files = File(PluginMain.dataFolder.absolutePath).listFiles()
        var cnt = 0;
        sender.sendMessage(

            buildMessageChain {
                sender.user?.let { At(it.id) }
                +"检索到的图库如下:\n"
                for(s in files!!)
                {
                    cnt++;
                    +PlainText("图库${cnt}:${s.name}==文件数量:${(s.listFiles()?.size ?: 0)}\n")
                }

            }

        )
    }

}