package org.longchuanclub.mirai.plugin

import net.mamoe.mirai.utils.ExternalResource
import org.longchuanclub.mirai.plugin.entity.ImageData
import org.longchuanclub.mirai.plugin.util.graphicsUtil
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

fun main() {
        val filepath = File("C:\\Users\\dache\\Pictures\\re")
        val images = filepath.listFiles()
         val simgs =  mutableListOf<ImageData>()
    images?.forEach {
        run {
            val o1 = ImageData(it.name, null, 0);
            val img1 = File(filepath.absolutePath + "\\${it.name}").listFiles{ file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
            if (img1 != null && img1.isNotEmpty()) {
                o1.Img  = img1[0]
                o1.size = img1.size
                simgs.add(o1)
            }
        }
    }

    var d = graphicsUtil.draw(simgs);
    // 指定本地目录路径
    val localDirectoryPath = "C:\\Users\\dache\\Pictures\\re"


    // 生成本地文件路径
    val localFilePath = "$localDirectoryPath/outimg.png"

     //将 ExternalResource 对象保存到本地文件
    d.inputStream().use { inputStream ->
        File(localFilePath).outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    }
