package gg.scala.discord.reaction

/**
 * @author GrowlyX
 * @since 4/4/2023
 */
enum class ReactionMappings(
    val roleId: Long, val emoji: String
)
{
    Announcements(1337906971659206666, "\uD83D\uDCE2"),
    ServerUpdates(1337906972539752489, "\uD83D\uDCDC"),
    Polls(1337906975052398602, "\uD83D\uDCCA"),
    Maintenance(1337906976302039202, "\uD83D\uDEA7"),
    StoreUpdates(1337938451852562493, "\uD83D\uDCB5"),
    Events(1337906976759349279, "\uD83C\uDF4F"),
    Giveaways(1337906973663957082, "\uD83C\uDF89");

    companion object
    {
        @JvmStatic
        val EMOJIS = values().associateBy(ReactionMappings::emoji)
    }
}
