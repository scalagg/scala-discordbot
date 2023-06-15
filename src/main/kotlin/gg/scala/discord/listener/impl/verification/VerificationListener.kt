package gg.scala.discord.listener.impl.verification

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.role
import gg.scala.discord.verification.ScalaDiscordVerificationService
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
object VerificationListener : ListenerAdapter()
{
    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "verification")
        {
            ScalaDiscordVerificationService
                .startVerification(event.member!!, event)
        }
    }

    override fun onGuildMemberRemove(
        event: GuildMemberRemoveEvent
    )
    {
        if (event.member == null)
            return

        ScalaDiscordVerificationService
            .registry.remove(event.member!!.id)
    }

    override fun onMessageReceived(
        event: MessageReceivedEvent
    )
    {
        if (event.channelType != ChannelType.PRIVATE)
        {
            return
        }

        if (event.message.author.isBot)
            return

        val guildId = ScalaDiscordApp
            .settings.guild

        val guild = event.jda
            .getGuildById(guildId)
            ?: return

        val captcha = ScalaDiscordVerificationService
            .registry[event.author.id] ?: return

        if (captcha.isCorrect(event.message.contentRaw))
        {
            val player = guild.role("Member") ?: guild.role("Player")!!
            guild.addRoleToMember(event.author, player).queue()

            event.message.reply("You've been verified in the **${ScalaDiscordApp.settings.serverName} Network** Discord Server, have fun!")
                .queue()

            ScalaDiscordVerificationService
                .registry.remove(event.author.id)
        } else
        {
            event.message.reply("That's not the correct code! Please try again.")
                .queue()
        }
    }
}
