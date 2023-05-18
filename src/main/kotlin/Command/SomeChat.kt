package org.example.mirai.plugin.Command

import net.mamoe.mirai.console.command.*
import org.example.mirai.plugin.PluginMain

/**
 * ”来只“or”来点“ 模块
 */
object SomeChat : SimpleCommand(PluginMain,"来只", description = "获取某个人的图库"){

        @Handler
        suspend fun Comeonly(sender: CommandSender, arg: String){
//            if(!CheckString(pi)) return;
            PluginMain.dataFolderPath
            sender.sendMessage("测试一下返回$arg");
            println("测试print$arg")
        }

}