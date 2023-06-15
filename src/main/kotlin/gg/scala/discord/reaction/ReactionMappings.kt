package gg.scala.discord.reaction

/**
 * @author GrowlyX
 * @since 4/4/2023
 */
enum class ReactionMappings(
    val roleId: Long, val emoji: String
)
{
    ServerUpdates(1119007612604796960L, "\uD83D\uDCDC"),
    StoreUpdates(1119007643705553007, "\uD83D\uDCB5"),
    UHCAlerts(1119007789298229268, "\uD83C\uDF4F"),
    Giveaways(1119007727549698100, "\uD83C\uDF89");

    companion object
    {
        @JvmStatic
        val EMOJIS = values().associateBy(ReactionMappings::emoji)
    }
}
