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
import java.io.Closeable
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ImageUtils: Closeable {
    companion object
    {
        fun GetImage(folderpath:String ): ExternalResource? {
            val filepath = resolveDataFile(folderpath)
            val images = filepath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
            if (images != null) {
                //logger.info { "已获取到图片列表${images.size}" }
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
            if (!file.exists()) {

                val imageByte = HttpClient.getHttp(url);
                val fileParent = file.parentFile
                if (!fileParent.exists()) fileParent.mkdirs()
                file.writeBytes(imageByte)
            }
        }

        public suspend fun delImages(from: String, image: Image):Boolean {
            val url = image.queryUrl()
            val filePath = "${from}/${image.imageId}"
            val file  = resolveDataFile(filePath)
            if(file.exists()){
                logger.info("${file.absolutePath}存在")
                file.deleteRecursively()
                if(!file.exists())return true
                else return false;
            }
            else{
                return false;
            }
        }

    }

    override fun close() {
        this.close()
    }

}