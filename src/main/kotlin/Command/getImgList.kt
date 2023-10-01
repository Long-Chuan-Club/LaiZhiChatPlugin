package org.example.mirai.plugin.Command


import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.entity.outImg
import org.example.mirai.plugin.util.graphicsUtil
import java.io.File

object getImgList :SimpleCommand(PluginMain,"获取图库",description = "获取全部的图库列表") {

    @Handler
    suspend fun handlerlist(sender: CommandSender){
        val files = File(PluginMain.dataFolder.absolutePath+"\\LaiZhi\\${sender.subject?.id}")
        var cnt = 0;
        val images = files.listFiles()
        var simgs =  mutableListOf<outImg>()
        images?.forEach {
            run {
                var o1 = outImg(it.name, null, 0);
                var img1 = File(files.absolutePath + "\\${it.name}").listFiles{file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
                if (img1 != null) {
                    if(img1.isNotEmpty()) {
                        o1.Img  = img1.get(0)
                        o1.size = img1.size
                    }
                }
                if(o1.Img!=null && o1.size!=0)simgs.add(o1)
            }
        }

        val res = graphicsUtil.darw(simgs);
        if(res!=null){
            val img = sender.subject?.let { res.uploadAsImage(it) }
            img?.let {
                sender.sendMessage(it) }
            } else {
            sender.sendMessage("图片生成错误，发送失败")
        }
    }




}