package gg.scala.discord.activity

import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
@Service
object ScalaDiscordActivityRotator : Runnable
{
    @Inject
    lateinit var jda: JDA

    @Inject
    lateinit var settings: ScalaDiscordSettings

    private var currentIndex = -1

    @Configure
    fun configure()
    {
        val executor = Executors
            .newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate(
            this, 0L, 5L, TimeUnit.SECONDS
        )
    }

    override fun run()
    {
        try
        {
            val activity = settings
                .activities[++currentIndex]

            jda.presence.activity = Activity.of(
                activity.type, activity.value
            )
        } catch (ignored: Exception)
        {
            currentIndex = -1
        }
    }

}
