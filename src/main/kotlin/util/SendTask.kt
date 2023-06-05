package org.example.mirai.plugin.util

import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import org.example.mirai.plugin.PluginMain.logger
import org.example.mirai.plugin.config.LzConfig

class SendTask {



    /**
     * 群聊回复
     */
    companion object{
        var messageInterval = LzConfig.MsgmessageIntervalTime
        public suspend fun sendMessage(event :GroupMessageEvent ,messages: List<Message>) = try {
            messages.forEach {
                event.group.sendMessage(it)
                delay(messageInterval)
            }
            delay(messageInterval)
        }catch (e: Throwable) {
            logger.error("发送消息失败！", e)
            delay(messageInterval)
        }

        /**
         * 私聊回复
         */
        public suspend fun sendMessage(event :CommandSender ,messages: List<Message>) = try {
            messages.forEach {
                event.sendMessage(it)
                delay(messageInterval)
            }
            delay(messageInterval)
        }catch (e: Throwable) {
            logger.error("发送消息失败！", e)
            delay(messageInterval)
        }

        public suspend fun sendMessage(sender: Contact, messageChain: MessageChain) = try {
                sender.sendMessage(messageChain)
                delay(messageInterval)
        }catch (e: Throwable) {
            logger.error("发送消息失败！", e)
            delay(messageInterval)
        }

        public suspend fun sendMessage(sender: Contact, messageChain: String) = try {
            sender.sendMessage(messageChain)
            delay(messageInterval)
        }catch (e: Throwable) {
            logger.error("发送消息失败！", e)
            delay(messageInterval)
        }

        public suspend fun sendMessage(sender: Contact, img: Image)= try {
            sender.sendMessage(img)
            delay(messageInterval)
        }catch (e: Throwable) {
            logger.error("发送消息失败！", e)
            delay(messageInterval)
        }
    }

}