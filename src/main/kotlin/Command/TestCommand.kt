package org.example.mirai.plugin.Command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.Image
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.util.ImageUtils
import java.util.stream.Collectors


object TestCommand : CompositeCommand(PluginMain,"C"){
    @SubCommand("添加")
    suspend fun CommandSender.add(name:String){
        //val message = event.message as? Image ?: return
        //val image = event.message[Image.Key]
        val image =
            chain.stream().filter { obj: Any? -> Image::class.java.isInstance(obj) }.findFirst().orElse(null) as Image


    }
}