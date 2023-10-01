package org.example.mirai.plugin.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Group
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
        fun GetImage(group: Group, folderpath:String, picnum:Int ): ExternalResource? {

            try {
                val filepath = resolveDataFile("LaiZhi/${group.id}/${folderpath}")
                val images = filepath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "gif"}
                if (images != null && images.isNotEmpty()) {
                    val rad = if(picnum!=-1){
                        picnum-1
                    } else {
                        Random.nextInt(0,images.size+1)%images.size;
                    }
                    val randomImage = images[rad]
                    val res = randomImage.toExternalResource()
                    logger.info { "本地已找到${randomImage.absolutePath}" }
                    return res;
                }

            }catch (e:Exception){
                logger.error( "获取图片异常")
            }
            return null
        }

        /**
         *
         */
//        private suspend fun saveImages(from: String, message: MessageChain) {
//            val fm = message[ForwardMessage]
//            if (fm != null) {
//                fm.nodeList.forEach { saveImages(from, it.messageChain) }
//            } else {
//                message.filterIsInstance<Image>().forEach { img ->
//                    saveImage(from, img)
//                }
//            }
//        }

        /**
         * 保存图片到本地目录
         */
        suspend fun saveImage(group: Group,from: String, image: Image) {
            val url = image.queryUrl()
            val filePath = "LaiZhi/${group.id}/${from}/${image.imageId}"
            val file  = resolveDataFile(filePath)
            if (!file.exists()) {

                val imageByte = HttpClient.getHttp(url);
                val fileParent = file.parentFile
                if (!fileParent.exists()) fileParent.mkdirs()
                file.writeBytes(imageByte)
            }
        }

        /**
         * 从本地目录删除图片
         */
        public suspend fun delImages(group: Group,from: String, image: Image):Boolean {
            val url = image.queryUrl()
            val filePath = "LaiZhi/${group.id}/${from}/${image.imageId}"
            val file  = resolveDataFile(filePath)

            logger.info("${file.absolutePath}存在")
            if(file.exists()){

                file.deleteRecursively()
                if(file.exists()) file.delete()
            }
                return !file.exists();


        }

    }

    override fun close() {
        this.close()
    }

}