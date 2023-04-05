package gg.scala.discord.reaction

/**
 * @author GrowlyX
 * @since 4/4/2023
 */
enum class ReactionMappings(
    val roleId: Long, val emoji: String
)
{
    ServerUpdates(1093010045391876128L, "\uD83D\uDCDC"),
    StoreUpdates(1093010092242247732L, "\uD83D\uDCB5"),
    Giveaways(1093010308978716682L, "\uD83C\uDF89")
}
