package org.example.mirai.plugin.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.PluginMain
import java.io.File
import java.net.URL
import java.util.*
import kotlin.random.Random

class ImageUtils {
    companion object
    {
        suspend fun saveImage(image: Image, folderpath:String ) {
            val filename = UUID.randomUUID().toString()
            val filepath = File(PluginMain.dataFolder, "$folderpath/")
            if(!filepath.exists()){filepath.mkdir()}
            val file = File(filepath, filename)
            PluginMain.logger.info { "保存中..." }
            val url = URL(image.queryUrl())
            withContext(Dispatchers.IO) {
                url.openStream()
            }.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            PluginMain.logger.info("保存至${file.absolutePath}!")
        }
        suspend fun GetImage(folderpath:String ): ExternalResource? {
            val filepath = File(PluginMain.dataFolder, "/$folderpath/")
            val images = filepath.listFiles { file -> file.extension == "jpg" || file.extension == "png" }

            if (images != null && images.isNotEmpty()) {
                val randomImage = images[Random.nextInt(images.size)]
                var res = randomImage.toExternalResource()
                PluginMain.logger.info { "本地已找到${randomImage.absolutePath}" }
                return res;
            }
            return null
        }

    }

}