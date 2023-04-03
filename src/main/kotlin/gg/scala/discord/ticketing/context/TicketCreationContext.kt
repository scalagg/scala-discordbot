package gg.scala.discord.ticketing.context

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
enum class TicketCreationContext(
    val formattedDisplay: String
)
{
    APPEAL("Punishment Appeal"),
    STORE_SUPPORT("Store Support"),
    REWARD_CLAIMING("Reward Claiming"),
    APPLICATION("Applications"),
    BUG_REPORT("Bug Report"),
    OTHER("Other")
}
