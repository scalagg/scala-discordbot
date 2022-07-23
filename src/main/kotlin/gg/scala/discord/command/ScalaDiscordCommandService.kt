package gg.scala.discord.command

import gg.scala.discord.command.commands.PanelCommands
import gg.scala.discord.command.commands.TicketCommands
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.JDA
import revxrsal.commands.jda.JDACommandHandler

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object ScalaDiscordCommandService
{
    @Inject
    lateinit var jda: JDA

    @Configure
    fun configure()
    {
        val command = JDACommandHandler.create(jda, "-")
        command.register(PanelCommands)
        command.register(TicketCommands)
    }
}
