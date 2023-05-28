package org.example.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.Command.AddChat
import org.example.mirai.plugin.PluginMain.dataFolderPath
import org.example.mirai.plugin.config.LzConfig
import org.example.mirai.plugin.util.ImageUtils
import java.io.File
import java.time.Instant


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.come_only.mirai-come",
        name = "来只XX",
        version = "0.1.4"
    ) {
        author("huvz")
        info(
            """
            群聊出现三只重复后 会自动复读
            来只&来点 功能 将群友话语做成梗图
        """.trimIndent()
        )
        // author 和 info 可以删除.
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
         CommandManager.registerCommand(AddChat) // 注册指令
        //配置文件目录 "${dataFolder.absolutePath}/"
        globalEventChannel().subscribeAlways<GroupMessageEvent> { it ->
            //获取指令
            var msg = it.message.content;
            var strname : String?

            if(msg.startsWith("/clear"))
            {
                var filename = msg.drop(6).trim()
                if(  filename  in LzConfig.pdImageList)
                    this.group.sendMessage(At(sender)+"这是受保护的图库，你无法删除噢")
                else {
                    var file = File(PluginMain.dataFolderPath.toString()+"/$filename")
                    try {
                        file.deleteRecursively()
                        this.group.sendMessage("清空${filename}文件夹成功")
                    } catch (e: Exception) {
                        this.group.sendMessage("泪目,未知错误")
                        e.printStackTrace()
                    }
                }

            }
            for(eqstr  in LzConfig.GetcommandList){
                if(msg.startsWith(eqstr)) {
                        strname = msg.drop(eqstr.length).trim()
                        it.Lzget(strname)
                    break;
                }
            }
            for(eqstr2 in LzConfig.AddcommandList){
                if(msg.startsWith(eqstr2)){
                        strname = msg.drop(eqstr2.length).trim()
                        it.Lzsave(strname,it.sender)
                    break;
                }
            }


        }

    }
    private suspend fun GroupMessageEvent.Lzget(arg: String?) {
        if (arg != null) {
            var res = ImageUtils.GetImage(arg)
            //如果不为空，就上传

            if (res != null) {
                this.subject.let {
                    var img = res.uploadAsImage(it)
                    this.group.sendMessage(img);
                }
            } else {
                this.group.sendMessage("目录下找不到图片噢")
            }

        }
    }
    private suspend fun GroupMessageEvent.Lzsave(arg: String?,sender1: Member)
    {
        this.group.sendMessage(At(sender1)+"请在1000ms内发送一张图片")
        var tim1 = Instant.now().epochSecond
        globalEventChannel().subscribe<GroupMessageEvent>{
            if(this.sender.id == sender1.id){
                var chain = this.message;
                val image: Image? = chain.findIsInstance<Image>()
                if(Instant.now().epochSecond-tim1>=10){
                    val quote: QuoteReply? = chain[QuoteReply]
                    this.group.sendMessage(buildMessageChain{
                        if (quote != null) {+quote}
                        +"已超时，请重新发送图片"
                    } )
                    return@subscribe ListeningStatus.STOPPED
                }
                if(image!=null) {
                    logger.info("提取到图片${image.imageId}")
                    arg?.let { it1 -> ImageUtils.saveImage(it1,image) }
                    this.group.sendMessage(chain+ PlainText("保存成功噢"));
                    return@subscribe ListeningStatus.STOPPED
                }
                else {
                    this.group.sendMessage(PlainText("没有找到图片噢"));
                    return@subscribe ListeningStatus.STOPPED
                }


            }
            return@subscribe ListeningStatus.LISTENING

        }
    }



}


