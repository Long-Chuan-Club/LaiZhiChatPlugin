package org.example.mirai.plugin.Command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.util.ImageUtils

/**
 * ”来只“or”来点“ 模块
 */
object SomeChat : SimpleCommand(PluginMain,"来只", description = "获取某个人的图库"){

        @Handler
        suspend fun CommandSender.GetSome (arg: String) {
//            if(!CheckString(pi)) return;
            PluginMain.logger.info("存储路径${PluginMain.dataFolderPath}+/$arg/")
            if (arg != null) {
                var res = ImageUtils.GetImage(arg)
                //如果不为空，就上传

                if (res != null) {
                    PluginMain.logger.info("图片路径存在")
                    this.subject?.let {
                        var img = res.uploadAsImage(it)
                        sendMessage(buildMessageChain { "请查收你的${arg}" + img });
                    }
                    sendMessage("图片已发送成功")
                } else {
                    sendMessage("目录下找不到图片噢")
                }

            }

        }

}

