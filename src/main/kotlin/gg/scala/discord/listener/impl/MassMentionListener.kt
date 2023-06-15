package gg.scala.discord.listener.impl

import net.dv8tion.jda.api.entities.Message
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
        val userMentions = event.message.mentions
            .getMentions(Message.MentionType.USER)

        if (userMentions.size >= 6)
        {
            event.message.delete().queue()
            event.guild.timeoutFor(event.member!!, Duration.ofDays(28))

            event.channel.sendMessage(
                "${event.member!!.asMention} has been muted for `pinging more than 6 users`."
            ).queue()
        }
    }
}
