package gg.scala.discord

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ScalaDiscordBootstrap
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        ScalaDiscordApp.start()

        Runtime.getRuntime().addShutdownHook(Thread {
            ScalaDiscordApp.stop()
        })
    }
}
