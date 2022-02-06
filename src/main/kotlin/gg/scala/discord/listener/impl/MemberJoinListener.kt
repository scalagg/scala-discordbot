package gg.scala.discord.listener.impl

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object MemberJoinListener : ListenerAdapter()
{
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent)
    {
        val channel = event.guild
            .getTextChannelsByName("welcome", true)[0]!!

        channel.sendMessage("Welcome to the **Verio Network** Discord Server, " + event.member.asMention + "! \uD83D\uDC4B").queue()
    }
}
