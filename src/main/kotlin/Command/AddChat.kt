package org.example.mirai.plugin.Command


import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.GroupAwareCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
import org.example.mirai.plugin.Command.TestCommand.add
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.util.ImageUtils
import java.util.stream.Collectors

object AddChat :SimpleCommand(PluginMain,"新增[来只]图库") {

    @Handler
    suspend fun GroupAwareCommandSender.add(){
//        var messageChain :MessageChain
//            .filter(::isImage)
//            .map(::getImage)
//            .collect(Collectors.toList())
//        //val image = event.message[Image.Key]
//        if (image != null) {
//            PluginMain.logger.info("提取到图片${image.imageId}")
//        }
//        image?.let { ImageUtils.saveImage(it,name) };
//        PluginMain.logger.info("保存成功")
    }

}