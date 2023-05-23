package org.example.mirai.plugin.Command

import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.util.ContactUtils.getContact
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.example.mirai.plugin.PluginMain
import org.example.mirai.plugin.config.CommandList
import org.example.mirai.plugin.util.ImageUtils
import java.time.LocalTime
import java.util.*

class GroupListener
{

    //统计复读出现次数
    fun listenSend(eventChannel: EventChannel<Event>){
        eventChannel.subscribeAlways<GroupMessageEvent> {
            //判断是否在指令列表中
            var perfix = CommandList.values().find { message.content.startsWith(it.name) }?.name
            var arg= perfix?.let { it1 -> message.content.removePrefix(it1) }

            //分类示例
            message.forEach {
                //循环每个元素在消息里
                if (it is Image) {
                    //如果消息这一部分是图片
                    val url = it.queryUrl()
                    group.sendMessage("图片，下载地址$url")
                }
                if (it is PlainText) {
                    //如果消息这一部分是纯文本
                    group.sendMessage("纯文本，内容:${it.content}")
                }
            }


        }







    }




}