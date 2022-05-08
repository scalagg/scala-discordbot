package gg.scala.discord.language

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
interface Language
{
    val locale: Map<String, String>
    fun getId(): String
    fun configure()

    fun getTranslation(id: String): String?
    {
        return locale[id]
    }
}
