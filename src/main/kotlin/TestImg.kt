package org.longchuanclub.mirai.plugin

import org.longchuanclub.mirai.plugin.entity.outImg
import org.longchuanclub.mirai.plugin.util.graphicsUtil
import java.io.File

fun main() {
        val filepath = File("F:\\Mirai2.15.0\\data\\com.HuChat.LaiZhi\\LaiZhi\\695988692")
        val images = filepath.listFiles()
         var simgs =  mutableListOf<outImg>()
    images?.forEach {
        run {
            var o1 = outImg(it.name, null, 0);
            var img1 = File(filepath.absolutePath + "\\${it.name}").listFiles{file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
            o1.Img  = img1.get(0)
            o1.size = img1.size
            if(o1.Img!=null && o1.size!=0)simgs.add(o1)
        }
    }

    graphicsUtil.darw(simgs);

    }
