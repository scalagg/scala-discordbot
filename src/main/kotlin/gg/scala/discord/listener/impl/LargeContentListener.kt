package gg.scala.discord.listener.impl

import gg.scala.discord.role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object LargeContentListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.contentRaw.length >= 1700)
        {
            val role = event.guild.role("muted")
            event.message.delete().queue()

            if (role != null)
            {
                event.guild.addRoleToMember(event.member!!, role).queue()

                event.channel.sendMessage(
                     "${event.member!!.asMention} has been permanently muted for `sending large messages`."
                ).queue()
            }
        }
    }
}
