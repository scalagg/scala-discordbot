package gg.scala.discord.ticketing

import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object Tickets
{
    val tickets = mutableMapOf<TextChannel, Ticket>()
}
