package gg.scala.discord.listener.impl

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.listener.ScalaDiscordListenerService
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Supplier


/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object MemberJoinListener : ListenerAdapter()
{
    private val broadcasts = mutableListOf(
        100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
        1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900,
        2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800,
        2900, 3000
    )

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent)
    {
        val channel = event.guild
            .getTextChannelsByName("welcome", true)
            .firstOrNull()

        val member = event.guild
            .getRolesByName("Default", true)
            .firstOrNull()

        if (member != null)
        {
            event.member.guild.addRoleToMember(event.member, member).queue()
        }

        ScalaDiscordListenerService.mappings[event.member.id] = event.member

        channel?.sendMessage(
            "Welcome to the **${ScalaDiscordApp.settings.serverName} Network** Discord Server, " + event.member.asMention + "!"
        )?.queue()

        CompletableFuture
            .supplyAsync {
                event.guild.loadMembers().get()
            }
            .thenAccept {
                if (it.size in broadcasts)
                {
                    val memberLogChannel = event.guild
                        .getTextChannelsByName(
                            "members-reached", true
                        )[0]

                    memberLogChannel
                        ?.sendMessage(
                            "We've reached **${it.size}** members in our Discord server! Good job! @everyone"
                        )
                        ?.queue()
                }
            }
    }
}
