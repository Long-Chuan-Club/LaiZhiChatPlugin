package org.example.mirai.plugin.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import okhttp3.OkHttpClient
import okhttp3.Request
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.PluginMain.logger
import org.example.mirai.plugin.PluginMain.resolveDataFile
import org.example.mirai.plugin.config.LzConfig
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ImageUtils {
    companion object
    {
        private suspend fun saveImage(image: Image, folderpath:String? ) {
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
            logger.info("保存至${file.absolutePath}!")
        }
        fun GetImage(folderpath:String ): ExternalResource? {
            val filepath = resolveDataFile(folderpath)
            val images = filepath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
            if (images != null) {
                logger.info { "已获取到图片列表${images.size}" }
            }
            if (images != null && images.isNotEmpty()) {
                var  rad= Random.nextInt(0,images.size+1)%images.size;
                logger.info("随机数为${rad}")
                val randomImage = images[rad]
                var res = randomImage.toExternalResource()
                logger.info { "本地已找到${randomImage.absolutePath}" }
                return res;
            }
            return null
        }
        private val okHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        /**
         * copy=>@jie65535
         */
        private suspend fun saveImages(from: String, message: MessageChain) {
            val fm = message[ForwardMessage]
            if (fm != null) {
                fm.nodeList.forEach { saveImages(from, it.messageChain) }
            } else {
                message.filterIsInstance<Image>().forEach { img ->
                    saveImage(from, img)
                }
            }
        }

        suspend fun saveImage(from: String, image: Image) {
            val url = image.queryUrl()
            val filePath = "${from}/${image.imageId}"
            val file  = resolveDataFile(filePath)
            logger.info("读取完毕${file.absolutePath}")
            if (!file.exists()) {
                val request = Request.Builder().url(url).build()
                val imageByte = okHttpClient.newCall(request).execute().body!!.bytes()
                val fileParent = file.parentFile
                if (!fileParent.exists()) fileParent.mkdirs()
                file.writeBytes(imageByte)
                PluginMain.logger.info("Saved ${file.path}.")
            }
        }



    }

}