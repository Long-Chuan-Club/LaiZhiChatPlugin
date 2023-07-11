package org.example.mirai.plugin.Service

import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.PluginMain.originalMessageFromLocal
import org.example.mirai.plugin.config.LzConfig
import org.example.mirai.plugin.util.ImageUtils
import org.example.mirai.plugin.util.SendTask
import java.io.File
import java.time.Instant
import kotlin.coroutines.CoroutineContext

object myEvent : SimpleListenerHost(){

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        // 处理 onMessage 中未捕获的异常
        PluginMain.logger.error("未知错误")
    }
    var isKey:Boolean = true;
    @EventHandler
    suspend fun GroupMessageEvent.onMessage(): ListeningStatus { // 可以抛出任何异常, 将在 handleException 处理
        var msg = message.content;
        var strname : String?
        message[QuoteReply.Key]?.run {
            val msg1 = msg.trim();
            if(msg1.startsWith("@${bot.id} #删除")){
                val original = originalMessageFromLocal
                var inmage = original[Image]!!;
                if(original[Image] is Image){
                    var filename = msg.drop("@${bot.id} #删除".length)
                    PluginMain.logger.info("文件名${filename}");
                    if(ImageUtils.delImages(filename,inmage)) {
                        SendTask.sendMessage(sender,At(sender) +"删除成功");
                    }
                    else{
                        group.sendMessage(At(sender) +"删除失败")
                    }
                }
                else{
                    group.sendMessage(At(sender) +"没找到图片")
                }
                return ListeningStatus.LISTENING
            }


        }
        if(msg.equals("#获取图库")){
            val files = File(PluginMain.dataFolder.absolutePath).listFiles()
            var cnt = 0;
            if(files.isEmpty()) SendTask.sendMessage(group,"当前没有图库哇")
            SendTask.sendMessage(
                group,
                buildMessageChain {
                    At(sender.id)
                    +"检索到的图库如下:"
                    for(s in files!!)
                    {
                        cnt++;
                        +PlainText("\n《${cnt}:${s.name}》:${(s.listFiles()?.size ?: 0)}")
                    }

                }

            )
        }
        else if(msg.startsWith("#clear") && sender.id== LzConfig.adminQQid)
        {
            var filename = msg.drop(6).trim()
            clear(filename)
            SendTask.sendMessage(group,"清理成功")
            return ListeningStatus.LISTENING
        }
        else if(msg.equals("开关关键字") && sender.id== LzConfig.adminQQid){
            if(isKey){
                isKey=false;
                SendTask.sendMessage(group,"关键字检索=false")
            }else {
                isKey=true;
                SendTask.sendMessage(group,"关键字检索=true")
            }
        }
        /**
         *  匹配添加指令
         */

        else if(msg.equals("#help")){
            this.group.sendMessage(
                At(sender.id) +
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
            Lzsave(strname,sender)

            return ListeningStatus.LISTENING
        }
        else{
            if(isKey){
                var filenamelist = countFile()
                for(eqstr  in filenamelist){
                    if(msg.contains(eqstr)) {
                        Lzget(eqstr)
                        return ListeningStatus.LISTENING
                    }
                }
            }
            else{
                if(msg.startsWith("来只")){
                    strname = msg.drop("来只".length).trim()
                    Lzget(strname)
                }

            }

        }

        return ListeningStatus.LISTENING // 表示继续监听事件
        // return ListeningStatus.STOPPED // 表示停止监听事件
    }
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
                    SendTask.sendMessage(group, img);
                }
                res.closed
            } else {
                SendTask.sendMessage(group, "目录下找不到图片噢")
            }
        }
    }

    /**
     * 清理图库
     */
    private suspend fun GroupMessageEvent.clear(filename: String){
        if(  filename  in LzConfig.pdImageList)
            this.group.sendMessage(At(sender) +"这是受保护的图库，你无法删除噢(请联系管理员)")
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
    private suspend fun GroupMessageEvent.Lzsave(arg: String?, sender1: Member)
    {
        SendTask.sendMessage(group, At(sender1) +"请在300s内发送一张图片")
        var ti1m1 = Instant.now().epochSecond
        globalEventChannel().subscribe<GroupMessageEvent>{
            if((Instant.now().epochSecond-ti1m1)>=300){
                SendTask.sendMessage(group, At(sender1) +"已超时，请重新发送图片")
                return@subscribe ListeningStatus.STOPPED
            }
            if(this.sender.id == sender1.id){
                var chain = this.message;
                val image: Image? = chain.findIsInstance<Image>()

                if(image!=null) {

                    arg?.let { it1 -> ImageUtils.saveImage(it1,image) }
                    SendTask.sendMessage(group, chain+ PlainText("保存成功噢"));
                    return@subscribe ListeningStatus.STOPPED
                }


            }
            return@subscribe ListeningStatus.LISTENING

        }
    }


}