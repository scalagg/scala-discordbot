package gg.scala.discord

import java.lang.Thread.sleep
import kotlin.concurrent.thread

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

        infinitySleeper()
    }

    private fun infinitySleeper()
    {
        thread {
            while (true)
            {
                sleep(Long.MAX_VALUE)
            }
        }.start()
    }
}
