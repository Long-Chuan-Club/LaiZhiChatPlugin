package org.example.mirai.plugin.util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.PluginMain
import java.io.File
import java.net.URL
import java.util.*

object ImageUtils {
    suspend fun saveImage(image: Image,folderpath:String ) {
        val filename = UUID.randomUUID().toString()
        val filepath = File(PluginMain.dataFolder,folderpath+"/")
        if(!filepath.exists()){filepath.mkdir()}

        val file = File(filepath, filename)
        PluginMain.logger.info { "保存中...保存至${file.absolutePath}" }
        val output = file.outputStream()
        val url = URL(image.queryUrl())
        withContext(Dispatchers.IO) {
            url.openStream()
        }.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}