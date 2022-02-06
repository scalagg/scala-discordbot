package gg.scala.discord.language

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
interface Language
{
    fun getId(): String
    val locale: Map<String, String>
    fun configure()

    fun getTranslation(id: String): String?
    {
        return locale[id]
    }
}
