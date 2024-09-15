package org.longchuanclub.mirai.plugin.Service

import entity.LZException
import entity.data.GroupDetails
import entity.data.ImageFiles
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.longchuanclub.mirai.plugin.PluginMain
import org.longchuanclub.mirai.plugin.entity.GroupDetail
import org.longchuanclub.mirai.plugin.entity.ImageFile
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

object ImageService {
    private val db = Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres", password = "postgres"
    )

    /**
     * 获取群聊信息
     * @param id qq群id
     * @return 成功码
     */
    fun selectGroupDetail(id: Int): GroupDetail {
        return transaction(db) {
            GroupDetails.selectAll().where { GroupDetails.id eq id.toString() }
                .map {
                    GroupDetail(
                        it[GroupDetails.id],
                        it[GroupDetails.avatar],
                        it[GroupDetails.name],
                        it[GroupDetails.total],
                        it[GroupDetails.galleryNumber]
                    )
                }
                .firstOrNull() ?: throw IllegalArgumentException("Group with ID $id not found")
        }
    }


    /**
     * 更新并迁移图片
     */
    suspend fun updateGrouplist(id: Long): Int {
        val ParentfilePath = "LaiZhi/$id"
        val filepath = PluginMain.resolveDataFile(ParentfilePath)
        val filelist = filepath.listFiles()
        filelist?.forEach {
            run {
                val filename = it.name
                //获取图片列表
                val foldlist =
                    File(filepath.absolutePath + "\\${filename}")
                        .listFiles { file ->
                            file.extension == "jpg" ||
                            file.extension == "png" ||
                            file.extension == "gif"
                        }
                //遍历flodlist 把每个图片都用fun getMD5(bytes: ByteArray): String计算一遍md5
                for (fold in foldlist!!) {
                    val md5b = getMD5(fold.readBytes())
                    transaction(db) {
                        ImageFiles.select(ImageFiles.md5)
                            .where { (ImageFiles.md5 eq md5b) }.firstOrNull()
                    }.let {
                        if (it == null) {
                            ImageFiles.select(ImageFiles.md5)
                            saveImage(id, filename, fold.readBytes(),getFileExtension(fold))
                            fold.deleteOnExit();
                        }
                    }

                }

            }
        }
        return 1
    }

    /**
     * 获取文件后缀的方法
     *
     * @param file 要获取文件后缀的文件
     * @return 文件后缀
     * @author https://www.4spaces.org/
     */
    fun getFileExtension(file: File?): String {
        var extension = ""
        try {
            if (file != null && file.exists()) {
                val name = file.name
                extension = name.substring(name.lastIndexOf("."))
            }
        } catch (e: Exception) {
            extension = ""
        }
        return extension
    }
    /**
     * 获取群聊的图片列表
     */
    fun selectImageDetail(id: Long): List<ImageFile> {
        return transaction(db) {
            ImageFiles.selectAll()
                .where { ImageFiles.qq eq id.toString() }
                .map {
                    ImageFile(
                        it[ImageFiles.id] ?: 0, // 处理 id 为 null 的情况
                        it[ImageFiles.md5] ?: "", // 处理 md5 为 null 的情况
                        it[ImageFiles.qq] ?: "", // 处理 qq 为 null 的情况
                        it[ImageFiles.count] ?: 0, // 处理 count 为 null 的情况
                        it[ImageFiles.about] ?: "", // 处理 about 为 null 的情况
                        it[ImageFiles.type] ?: "", // 处理 type 为 null 的情况
                        it[ImageFiles.url] ?: "" // 处理 url 为 null 的情况
                    )
                }
        }
    }

    /**
     * 更新qq群图库信息
     */
    fun updateGroupDetail(qq: Long) {
        transaction(db) {
            val entity = selectImageDetail(qq)
            val entity2 = selectGroupDetail(qq.toInt())
            entity2.total = entity.size
            GroupDetails.update({ GroupDetails.id eq entity2.id }) {
                it[total] = entity2.total
            }
        }
    }

    /**
     * 获取图片
     */
    suspend fun getImage(q1: Long, name: String): ExternalResource {
        return transaction(db) {
            ImageFiles.selectAll()
                .where { (ImageFiles.qq eq q1.toString()) and (ImageFiles.about eq name) }
                .map {
                    ImageFile(
                        0,
                        it[ImageFiles.md5],
                        it[ImageFiles.qq],
                        it[ImageFiles.count],
                        it[ImageFiles.about],
                        it[ImageFiles.type],
                        it[ImageFiles.url]
                    )
                }
                .randomOrNull() ?: throw IllegalArgumentException("No image found for group $q1 and name $name")
        }.let {
            PluginMain.logger.info("获取到图片${it.md5},随机数")
            val ParentfilePath = "LaiZhi/$q1/$name/${it.md5}.${it.type}"
            val file = PluginMain.resolveDataFile(ParentfilePath)
            file.toExternalResource().toAutoCloseable()
        }
    }
    suspend fun getRandomImage(q1: Long): ExternalResource {
        return transaction(db) {
            ImageFiles.selectAll()
                .where { (ImageFiles.qq eq q1.toString()) }
                .map {
                    ImageFile(
                        0,
                        it[ImageFiles.md5],
                        it[ImageFiles.qq],
                        it[ImageFiles.count],
                        it[ImageFiles.about],
                        it[ImageFiles.type],
                        it[ImageFiles.url]
                    )
                }
                .randomOrNull()
        }.let {
            val ParentfilePath = "LaiZhi/$q1/${it?.about}/${it?.md5}.${it?.type}"
            val file = PluginMain.resolveDataFile(ParentfilePath)
            file.toExternalResource().toAutoCloseable()
        }
    }
    /**
     * 保存图片信息
     */
    suspend fun saveImage(q1: Long, name: String, imageByte: ByteArray, fileType: String) {


        val ParentfilePath = "LaiZhi/$q1/$name/"
        val fileParent = PluginMain.resolveDataFile(ParentfilePath)
        if (!fileParent.exists()) fileParent.mkdirs()

        val md5a = getMD5(imageByte)


        val filePath = ParentfilePath + "${md5a}.${fileType}"
        val file = PluginMain.resolveDataFile(filePath)
        file.writeBytes(imageByte)
        transaction(db) {
            val entity = ImageFiles.selectAll()
                .where { ImageFiles.md5 eq md5a.toString() }
                .firstOrNull()
            if (entity != null) throw LZException("已存在同md5的图片")
            ImageFiles.insert {
                it[md5] = md5a.toString()
                it[qq] = q1.toString()
                it[count] = 0L
                it[about] = name
                it[type] = fileType
                it[url] = ParentfilePath
            }
        }
    }

    /**
     * 更新图片信息
     */
    fun updateImage(imageFile: ImageFile) {
        transaction(db) {
            ImageFiles.update(
                {
                    ImageFiles.id eq 8
                }
            ) {
                it[md5] = imageFile.md5
                it[qq] = imageFile.qq
                it[count] = imageFile.count
                it[about] = imageFile.about
                it[type] = imageFile.type
                it[url] = imageFile.url
            }
        }
    }

    /**
     * 删除图片信息
     */
    fun deleteImage(id: Long) {
        transaction(db) {
            ImageFiles.deleteWhere { ImageFiles.id eq id }
        }
    }

    /**
     * 计算MD5
     */
    fun getMD5(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(bytes)
        val digest = md.digest()
        return BigInteger(1, digest).toString(16).padStart(32, '0')
    }

    fun close() {

    }
}