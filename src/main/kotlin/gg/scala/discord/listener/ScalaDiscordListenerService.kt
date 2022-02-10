package gg.scala.discord.listener

import gg.scala.discord.listener.impl.LargeContentListener
import gg.scala.discord.listener.impl.MassMentionListener
import gg.scala.discord.listener.impl.MemberJoinListener
import gg.scala.discord.listener.impl.ticket.TicketListener
import gg.scala.discord.listener.impl.verification.VerificationListener
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.JDA

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object ScalaDiscordListenerService
{
    @Inject
    lateinit var jda: JDA

    @Configure
    fun configure()
    {
        listOf(
            MassMentionListener,
            LargeContentListener,
            MemberJoinListener,
            TicketListener,
            VerificationListener
        ).forEach {
            jda.addEventListener(it)
        }
    }
}
