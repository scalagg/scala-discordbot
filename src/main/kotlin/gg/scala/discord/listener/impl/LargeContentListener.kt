package gg.scala.discord.listener.impl

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object LargeContentListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.contentRaw.length >= 1700 && event.member != null)
        {
            event.message.delete().queue()
            event.guild.timeoutFor(event.member!!, Duration.ofDays(365)).queue()

            event.channel.sendMessage(
                "${event.member!!.asMention} has been muted for `sending large messages`."
            ).queue()
        }
    }
}
