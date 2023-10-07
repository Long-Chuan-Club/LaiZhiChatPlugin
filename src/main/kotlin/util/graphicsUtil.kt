package org.longchuanclub.mirai.plugin.util

import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.longchuanclub.mirai.plugin.entity.outImg
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class graphicsUtil {


    companion object{
        private val titlefont = Font
            .createFont(Font.TRUETYPE_FONT, this::class.java.getResourceAsStream("/chinese_font.ttf"))
    fun darw(fileList: MutableList<outImg>) : ExternalResource?{
        val targetWidth = 185 // 目标宽度
        val targetHeight = 185 // 目标高度
        val imagesPerRow = 4 // 每行图片数量
        val verticalSpacing = 50 // 垂直间距
        val picpadding = 20; //图片间距
        val titleHeight =120; //标题高度

        val numRows = (fileList.size + imagesPerRow - 1) / imagesPerRow
        val outputWidth = imagesPerRow * (targetWidth+picpadding)+picpadding
        val outputHeight = numRows * (targetHeight+picpadding) + (numRows - 1) * verticalSpacing+picpadding+titleHeight

        val outputImage = BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = outputImage.createGraphics()

        // 设置背景颜色
        graphics.color = Color.decode("#e4e6eb")
        graphics.fillRect(0, 0, outputWidth, outputHeight)

        // 设置抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 设置字体
        //graphics.font = Font("宋体", Font.BOLD, 40)
        var sizedFont = titlefont.deriveFont(60f)
        graphics.font = sizedFont
        graphics.color = Color.BLACK

        // 计算标题的位置
        val title = "图库列表"
        val titleWidth = graphics.fontMetrics.stringWidth(title)
        val titleX = (outputWidth- titleWidth) / 2
        val titleY = titleHeight/2 + titlefont.size
        // 绘制标题
        graphics.drawString(title, titleX, titleY)
        sizedFont = titlefont.deriveFont(22f)
        //graphics.font = Font("宋体", Font.BOLD, 22)
        graphics.font = sizedFont;
        // 计算起始绘制的位置
        var x = 0
        var y = titleHeight
        for (i in fileList.indices) {
            // 设置背景颜色
            graphics.color = Color.decode("#e4e6eb")
            val file = fileList[i]
            val row = i / imagesPerRow
            val col = i % imagesPerRow

            // 计算图片在目标矩形中的缩放大小
            val sourceImage = ImageIO.read(file.Img)
            val scaleFactor = calculateScaleFactor(sourceImage.width, sourceImage.height, targetWidth, targetHeight)
            val scaledWidth = (sourceImage.width * scaleFactor).toInt()
            val scaledHeight = (sourceImage.height * scaleFactor).toInt()
             x = picpadding + col *( targetWidth +picpadding)

            y = titleHeight+(row * targetHeight + row * verticalSpacing)

            var x1 = x +  targetWidth
            var y1 = y + targetHeight
            graphics.color = Color.WHITE
            graphics.fillRect(x,y,targetWidth,targetHeight)
            // 绘制缩放后的图片
            graphics.drawImage(
                sourceImage.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH),
                x, y, null
            )
            graphics.color = Color.BLACK
            // 绘制文件名
            val fileName = file.ckname
            val fileNameWidth = graphics.fontMetrics.stringWidth(fileName)
            val fileNameX = x + (targetWidth - fileNameWidth) / 2
            val fileNameY = y + targetHeight + 35


            // 调整文字的垂直位置
            graphics.drawString("${fileName}:${file.size}", fileNameX, fileNameY)

        }
        val tempFile = File.createTempFile("image", ".png")
        ImageIO.write(outputImage, "png", tempFile)
        graphics.dispose()
        val externalResource = tempFile.toExternalResource()
        tempFile.delete()
        return externalResource
    }

        private fun calculateScaleFactor(sourceWidth: Int, sourceHeight: Int, targetWidth: Int, targetHeight: Int): Double {
            val widthRatio = targetWidth.toDouble() / sourceWidth.toDouble()
            val heightRatio = targetHeight.toDouble() / sourceHeight.toDouble()
            return if (widthRatio > heightRatio) heightRatio else widthRatio
        }



    }
}