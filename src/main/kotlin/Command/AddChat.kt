package org.example.mirai.plugin.Command


import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.PluginMain.logger
import org.example.mirai.plugin.util.ImageUtils
import java.util.stream.Collectors

object AddChat :SimpleCommand(PluginMain,"添加",description = "获取某个人的图库") {

    @Handler
    suspend fun CommandSenderOnMessage<*>.add(name:String){
        sendMessage("请发送一张图片")
        globalEventChannel().subscribeOnce<GroupMessageEvent>{
            var chain = this.message;
            val image: Image? = chain.findIsInstance<Image>()
            if(image!=null) {
                logger.info("提取到图片${image.imageId}")
                ImageUtils.saveImage(name,image)
                PluginMain.logger.info("保存成功")
                sendMessage(chain+ PlainText("保存成功"));
            }
            else {
                sendMessage(PlainText("没有找到图片   噢"));
            }
        }


    }

}