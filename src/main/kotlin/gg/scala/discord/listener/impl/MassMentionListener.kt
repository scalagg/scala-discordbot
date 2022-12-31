package gg.scala.discord.listener.impl

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object MassMentionListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.mentionedUsers.size >= 6)
        {
            event.message.delete().queue()
            event.guild.timeoutFor(event.member!!, Duration.ofDays(28))

            event.channel.sendMessage(
                "${event.member!!.asMention} has been muted for `pinging more than 6 users`."
            ).queue()
        }
    }
}
