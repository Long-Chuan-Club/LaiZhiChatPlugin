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
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.Command.AddChat
import org.example.mirai.plugin.config.LzConfig
import org.example.mirai.plugin.util.ImageUtils
import xyz.cssxsh.mirai.hibernate.MiraiHibernateRecorder
import java.io.File
import java.time.Instant


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.come_only.mirai-come",
        name = "来只XX",
        version = "0.1.5"
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
        var isKey:Boolean = true;
        globalEventChannel().subscribe<GroupMessageEvent> { it ->
            //获取指令
            var msg = it.message.content;
            var strname : String?
            message[QuoteReply.Key]?.run {
                val msg1 = msg.trim();
                if(msg1.startsWith("@${bot.id} #删除")){
                    val original = originalMessageFromLocal
                    var inmage = original.get(Image)!!;
                    if(original.get(Image) is Image){
                        var filename = msg.drop("@${bot.id} #删除".length)
                        logger.info("文件名${filename}");
                        if(ImageUtils.delImages(filename,inmage)) {
                            group.sendMessage(At(sender)+"删除成功");
                        }
                        else{
                            group.sendMessage(At(sender)+"删除失败")
                        }
                    }
                    else{
                        group.sendMessage(At(sender)+"没找到图片")
                    }
                    return@subscribe ListeningStatus.LISTENING
                }


            }
            if(msg.equals("#获取图库")){
                val files = File(PluginMain.dataFolder.absolutePath).listFiles()
                var cnt = 0;
                this.group.sendMessage(

                    buildMessageChain {At(sender.id)
                        +"检索到的图库如下:\n"
                        for(s in files!!)
                        {
                            cnt++;
                            +PlainText("File>${cnt}:${s.name}.size():${(s.listFiles()?.size ?: 0)}\n")
                        }

                    }

                )
            }
            else if(msg.startsWith("#clear") && sender.id==LzConfig.adminQQid)
            {
                var filename = msg.drop(6).trim()
                it.clear(filename)
                return@subscribe ListeningStatus.LISTENING
            }
            else if(msg.equals("开关关键字") && sender.id==LzConfig.adminQQid){
                if(isKey){
                    isKey=false;
                    this.group.sendMessage("成功关闭关键字检索")
                }else {
                    isKey=true;
                    this.group.sendMessage("成功开启关键字检索")
                }
            }
            /**
             *  匹配添加指令
             */

            else if(msg.equals("#help")){
                this.group.sendMessage(At(sender.id)+
                    buildMessageChain {
                        +"\n本群包括但不限于以下指令列表\n"
                        +
                        "1.获取xx图库"
                            +"\n检索关键字：${isKey}"
                        +"\n2.创建图库"
                        for(eqstr  in LzConfig.AddcommandList){
                            +"\n        *[${eqstr}]"
                        }
                        +"\n3.清空图库*[#clear]"
                        +"\n4.获取列表*[#获取图库]"
                        +"\nbot版本:${PluginMain.version}"
                    }

                )

            }

            /**
             * 根据关键字匹配
             */
            else if(msg.startsWith("添加")){
                strname = msg.drop("添加".length).trim()
                it.Lzsave(strname,it.sender)

                return@subscribe ListeningStatus.LISTENING
            }
            else{
                if(isKey){
                    var filenamelist = countFile()
                    for(eqstr  in filenamelist){
                        if(msg.contains(eqstr)) {
                            it.Lzget(eqstr)
                            return@subscribe ListeningStatus.LISTENING
                        }
                    }
                }
                else{
                    if(msg.startsWith("来只")){
                        strname = msg.drop("来只".length).trim()
                        it.Lzget(strname)
                    }

                }

            }

            return@subscribe ListeningStatus.LISTENING
        }


    }

    /**
     * 更新文件list
     * 用来匹配关键字
     * 从长到短进行匹配,防止最小的匹配到
     */
    private  fun countFile(): List<String> {
        var filenamelist = mutableListOf<String>();
        var filelist= File(PluginMain.dataFolder.absolutePath).listFiles();
        for(f in filelist!!){
            filenamelist.add(f.name)
        }
        return filenamelist.sortedBy{ it.length }.reversed();
    }

    /**
     * 获取图片
     */
    private suspend fun GroupMessageEvent.Lzget(arg: String?) {
        if (arg != null) {
            var res = ImageUtils.GetImage(arg)
            //如果不为空，就上传

            if (res != null) {
                this.subject.let {
                    var img = res.uploadAsImage(it)
                    this.group.sendMessage(img);
                }
                res.closed
            } else {
                this.group.sendMessage("目录下找不到图片噢")
            }
        }
    }

    /**
     * 清理图库
     */
    private suspend fun GroupMessageEvent.clear(filename: String){
        if(  filename  in LzConfig.pdImageList)
            this.group.sendMessage(At(sender)+"这是受保护的图库，你无法删除噢(请联系管理员)")
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

    /**
     * 删除图库
     */
    private suspend fun GroupMessageEvent.Lzsave(arg: String?,sender1: Member)
    {
        this.group.sendMessage(At(sender1)+"请在3000ms内发送一张图片")
        var ti1m1 = Instant.now().epochSecond
        globalEventChannel().subscribe<GroupMessageEvent>{
            if((Instant.now().epochSecond-ti1m1)>=30){
                this.group.sendMessage(buildMessageChain{
                    At(sender1.id)
                    +"已超时，请重新发送图片"
                } )
                return@subscribe ListeningStatus.STOPPED
            }
            if(this.sender.id == sender1.id){
                var chain = this.message;
                val image: Image? = chain.findIsInstance<Image>()

                if(image!=null) {

                    arg?.let { it1 -> ImageUtils.saveImage(it1,image) }
                    this.group.sendMessage(chain+ PlainText("保存成功噢"));
                    return@subscribe ListeningStatus.STOPPED
                }


            }
            return@subscribe ListeningStatus.LISTENING

        }
    }



}


